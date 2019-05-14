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

package com.here.genium.generator.swift

import com.here.genium.model.lime.LimeContainer
import com.here.genium.model.lime.LimeContainer.ContainerType
import com.here.genium.model.lime.LimeElement
import com.here.genium.model.lime.LimeType
import com.here.genium.model.lime.LimeTypeDef

class SwiftNameResolver(private val limeReferenceMap: Map<String, LimeElement>) {
    fun getFullName(limeType: LimeType) =
        getNamespacePrefix(limeType) + SwiftNameRules.getTypeName(limeType.name)

    fun getMapName(limeType: LimeType) = getFullName(limeType).replace('.', '_')

    private fun getNamespacePrefix(limeType: LimeType): String {
        if (limeType is LimeContainer) {
            return ""
        }

        val limeContainer = limeReferenceMap[limeType.path.parent.toString()] as LimeContainer
        return when (limeContainer.type) {
            ContainerType.CLASS -> SwiftNameRules.getTypeName(limeContainer.name) + "."
            ContainerType.INTERFACE -> when (limeType) {
                is LimeTypeDef -> SwiftNameRules.getTypeName(limeContainer.name) + "."
                else -> ""
            }
            ContainerType.TYPE_COLLECTION -> ""
        }
    }
}