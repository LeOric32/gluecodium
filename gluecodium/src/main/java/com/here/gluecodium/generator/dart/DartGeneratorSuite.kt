/*
 * Copyright (C) 2016-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.gluecodium.generator.dart

import com.here.gluecodium.Gluecodium
import com.here.gluecodium.cli.GluecodiumExecutionException
import com.here.gluecodium.generator.common.GeneratedFile
import com.here.gluecodium.generator.common.NameResolver
import com.here.gluecodium.generator.common.NameRules
import com.here.gluecodium.generator.common.nameRuleSetFromConfig
import com.here.gluecodium.generator.common.templates.TemplateEngine
import com.here.gluecodium.generator.cpp.CppNameRules
import com.here.gluecodium.generator.ffi.FfiCppIncludeResolver
import com.here.gluecodium.generator.ffi.FfiCppNameResolver
import com.here.gluecodium.generator.ffi.FfiNameResolver
import com.here.gluecodium.model.lime.LimeClass
import com.here.gluecodium.model.lime.LimeContainer
import com.here.gluecodium.model.lime.LimeEnumeration
import com.here.gluecodium.model.lime.LimeException
import com.here.gluecodium.model.lime.LimeFunction
import com.here.gluecodium.model.lime.LimeInterface
import com.here.gluecodium.model.lime.LimeModel
import com.here.gluecodium.model.lime.LimeNamedElement
import com.here.gluecodium.model.lime.LimeStruct
import com.here.gluecodium.model.lime.LimeType
import com.here.gluecodium.model.lime.LimeTypeAlias
import com.here.gluecodium.model.lime.LimeTypesCollection
import com.here.gluecodium.platform.common.GeneratorSuite

class DartGeneratorSuite(options: Gluecodium.Options) : GeneratorSuite() {

    private val libraryName = options.libraryName
    private val nameRules = NameRules(nameRuleSetFromConfig(options.dartNameRules))
    private val cppNameRules =
        CppNameRules(options.cppRootNamespace, nameRuleSetFromConfig(options.cppNameRules))
    private val internalNamespace = options.cppInternalNamespace

    override fun generate(limeModel: LimeModel): List<GeneratedFile> {
        val dartNameResolver = DartNameResolver(limeModel.referenceMap, nameRules)
        val ffiNameResolver = FfiNameResolver(limeModel.referenceMap, nameRules)

        val dartResolvers = mapOf(
            "" to dartNameResolver,
            "Ffi" to ffiNameResolver,
            "FfiApiTypes" to FfiApiTypeNameResolver(),
            "FfiDartTypes" to FfiDartTypeNameResolver()
        )
        val ffiResolvers = mapOf(
            "" to ffiNameResolver,
            "C++" to FfiCppNameResolver(limeModel.referenceMap, cppNameRules, internalNamespace)
        )
        val includeResolver =
            FfiCppIncludeResolver(limeModel.referenceMap, cppNameRules, internalNamespace)
        val pathsCollector = mutableListOf<String>()

        return limeModel.topElements.flatMap {
            listOfNotNull(generateDart(it, dartResolvers, pathsCollector)) +
                    generateFfi(it, ffiResolvers, includeResolver)
        } + generateDartCommonFiles(pathsCollector) + generateFfiCommonFiles()
    }

    private fun generateDart(
        rootElement: LimeNamedElement,
        nameResolvers: Map<String, NameResolver>,
        pathsCollector: MutableList<String>
    ): GeneratedFile? {
        val contentTemplateName = selectTemplate(rootElement) ?: return null
        val content = TemplateEngine.render(
            "dart/DartFile",
            mapOf("model" to rootElement, "contentTemplate" to contentTemplateName),
            nameResolvers
        )

        val packagePath = rootElement.path.head.joinToString(separator = "/")
        val fileName = nameRules.getName(rootElement)
        val relativePath = "$SRC_DIR_SUFFIX/$packagePath/$fileName.dart"
        pathsCollector += relativePath

        return GeneratedFile(content, "$LIB_DIR/$relativePath")
    }

    private fun generateFfi(
        rootElement: LimeNamedElement,
        nameResolvers: Map<String, NameResolver>,
        includeResolver: FfiCppIncludeResolver
    ): List<GeneratedFile> {
        val limeType = rootElement as? LimeType ?: return emptyList()

        val functions = collectFunctions(limeType).sortedBy { it.fullName }
        if (functions.isEmpty()) {
            return emptyList()
        }

        val packagePath = rootElement.path.head.joinToString(separator = "_")
        val fileName = "ffi_${packagePath}_${nameRules.getName(rootElement)}"
        val includes = includeResolver.resolveIncludes(limeType) +
            functions.flatMap { collectTypeRefs(it) }.flatMap { includeResolver.resolveIncludes(it) }

        val headerContent = TemplateEngine.render(
            "ffi/FfiHeader",
            mapOf("model" to rootElement, "internalNamespace" to internalNamespace),
            nameResolvers
        )
        val implContent = TemplateEngine.render(
            "ffi/FfiImplementation",
            mapOf(
                "model" to rootElement,
                "internalNamespace" to internalNamespace,
                "headerName" to fileName,
                "includes" to includes.distinct().sorted()
            ),
            nameResolvers
        )

        return listOf(
            GeneratedFile(headerContent, "$FFI_DIR/$fileName.h"),
            GeneratedFile(implContent, "$FFI_DIR/$fileName.cpp")
        )
    }

    private fun generateDartCommonFiles(relativePaths: List<String>): List<GeneratedFile> {
        val templateData = mapOf("libraryName" to libraryName, "files" to relativePaths)
        return listOf(
            GeneratedFile(
                TemplateEngine.render("dart/DartExports", templateData),
                "$LIB_DIR/$libraryName.dart"
            ),
            GeneratedFile(
                TemplateEngine.render("dart/DartLibraryInit", templateData),
                "$LIB_DIR/$SRC_DIR_SUFFIX/_library_init.dart"
            ),
            GeneratedFile(
                TemplateEngine.render("dart/DartStringHandle", templateData),
                "$LIB_DIR/$SRC_DIR_SUFFIX/_string_handle.dart"
            ),
            GeneratedFile(
                TemplateEngine.render("dart/DartPubspec", templateData),
                "$ROOT_DIR/pubspec.yaml"
            )
        )
    }

    private fun generateFfiCommonFiles(): List<GeneratedFile> {
        val headerOnly = listOf("ConversionBase", "Export", "OpaqueHandle")
        val headerAndImpl = listOf("StringHandle")
        val data = mapOf(
            "opaqueHandleType" to OPAQUE_HANDLE_TYPE,
            "internalNamespace" to internalNamespace
        )

        return headerOnly.map {
            GeneratedFile(TemplateEngine.render("ffi/Ffi$it", data), "$FFI_DIR/$it.h")
        } + headerAndImpl.flatMap { listOf(
            GeneratedFile(TemplateEngine.render("ffi/Ffi${it}Header", data), "$FFI_DIR/$it.h"),
            GeneratedFile(TemplateEngine.render("ffi/Ffi${it}Impl", data), "$FFI_DIR/$it.cpp")
        ) }
    }

    private fun selectTemplate(limeElement: LimeNamedElement) =
        when (limeElement) {
            is LimeTypesCollection -> "dart/DartTypes"
            is LimeClass -> "dart/DartClass"
            is LimeInterface -> "dart/DartInterface"
            is LimeStruct -> "dart/DartStruct"
            is LimeEnumeration -> "dart/DartEnumeration"
            is LimeException -> "dart/DartException"
            is LimeTypeAlias -> null
            else -> throw GluecodiumExecutionException("Unsupported top-level element: " +
                    limeElement::class.java.name)
        }

    private fun collectFunctions(limeType: LimeType): List<LimeFunction> =
        when (limeType) {
            is LimeContainer ->
                limeType.functions + limeType.structs.flatMap { collectFunctions(it) }
            else -> emptyList()
        }

    private fun collectTypeRefs(limeFunction: LimeFunction) =
        limeFunction.parameters.map { it.typeRef } +
            limeFunction.returnType.typeRef +
            listOfNotNull(limeFunction.exception?.errorType)

    override val name = "com.here.DartGenerator"

    companion object {
        const val GENERATOR_NAME = "dart"
        private const val ROOT_DIR = GENERATOR_NAME
        private const val LIB_DIR = "$ROOT_DIR/lib"
        private const val SRC_DIR_SUFFIX = "src"
        private const val FFI_DIR = "$ROOT_DIR/ffi"
        private const val OPAQUE_HANDLE_TYPE = "void*"
    }
}
