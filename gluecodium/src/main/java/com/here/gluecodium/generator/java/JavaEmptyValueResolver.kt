/*
 * Copyright (C) 2016-2020 HERE Europe B.V.
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

package com.here.gluecodium.generator.java

import com.here.gluecodium.cli.GluecodiumExecutionException
import com.here.gluecodium.generator.common.NameResolver
import com.here.gluecodium.model.lime.LimeBasicType
import com.here.gluecodium.model.lime.LimeBasicType.TypeId
import com.here.gluecodium.model.lime.LimeField
import com.here.gluecodium.model.lime.LimeType

/**
 * Resolves strings for "empty" default values of struct fields in Java.
 */
internal class JavaEmptyValueResolver(private val javaNameResolver: JavaNameResolver) : NameResolver {

    override fun resolveName(element: Any): String =
        when (element) {
            is LimeField -> resolveEmptyValue(element)
            else -> throw GluecodiumExecutionException("Unsupported element type ${element.javaClass.name}")
        }

    private fun resolveEmptyValue(limeField: LimeField) =
        when (val limeType = limeField.typeRef.type.actualType) {
            is LimeBasicType -> resolveBasicTypeEmptyValue(limeType)
            else -> resolveNullValue(limeType)
        }

    private fun resolveBasicTypeEmptyValue(limeType: LimeBasicType) =
        when (limeType.typeId) {
            TypeId.BOOLEAN -> "false"
            TypeId.INT8 -> "(byte)0"
            TypeId.UINT8, TypeId.INT16 -> "(short)0"
            TypeId.UINT16, TypeId.INT32 -> "0"
            TypeId.UINT32, TypeId.INT64, TypeId.UINT64 -> "0L"
            TypeId.FLOAT -> "0f"
            TypeId.DOUBLE -> "0"
            else -> resolveNullValue(limeType)
        }

    private fun resolveNullValue(limeType: LimeType): String {
        val typeName = javaNameResolver.resolveReferenceName(limeType)
        return "($typeName)null"
    }
}
