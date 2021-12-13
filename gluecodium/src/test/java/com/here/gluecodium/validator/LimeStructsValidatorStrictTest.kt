/*
 * Copyright (C) 2016-2021 HERE Europe B.V.
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

package com.here.gluecodium.validator

import com.here.gluecodium.model.lime.LimeAttributeType
import com.here.gluecodium.model.lime.LimeAttributes
import com.here.gluecodium.model.lime.LimeBasicTypeRef
import com.here.gluecodium.model.lime.LimeElement
import com.here.gluecodium.model.lime.LimeField
import com.here.gluecodium.model.lime.LimeFieldConstructor
import com.here.gluecodium.model.lime.LimeFunction
import com.here.gluecodium.model.lime.LimeModel
import com.here.gluecodium.model.lime.LimePath.Companion.EMPTY_PATH
import com.here.gluecodium.model.lime.LimeStruct
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LimeStructsValidatorStrictTest {

    private val allElements = mutableMapOf<String, LimeElement>()
    private val limeModel = LimeModel(allElements, emptyList())

    private val limeField = LimeField(EMPTY_PATH, typeRef = LimeBasicTypeRef.INT)
    private val immutableAttributes =
        LimeAttributes.Builder().addAttribute(LimeAttributeType.IMMUTABLE).build()

    private val validator = LimeStructsValidator(mockk(relaxed = true), strictMode = true)

    @Test
    fun validateMutableStruct() {
        allElements[""] = LimeStruct(EMPTY_PATH, fields = listOf(limeField))

        assertTrue(validator.validate(limeModel))
    }

    @Test
    fun validateImmutableStructWithNoConstructors() {
        allElements[""] = LimeStruct(EMPTY_PATH, fields = listOf(limeField), attributes = immutableAttributes)

        assertFalse(validator.validate(limeModel))
    }

    @Test
    fun validateImmutableStructWithFieldConstructors() {
        val fieldConstructor =
            LimeFieldConstructor(EMPTY_PATH, structRef = LimeBasicTypeRef.INT, fieldRefs = emptyList())
        allElements[""] = LimeStruct(
            EMPTY_PATH,
            fields = listOf(limeField),
            attributes = immutableAttributes,
            fieldConstructors = listOf(fieldConstructor)
        )

        assertTrue(validator.validate(limeModel))
    }

    @Test
    fun validateImmutableStructWithCustomConstructors() {
        val customConstructor = LimeFunction(EMPTY_PATH, isConstructor = true)
        allElements[""] = LimeStruct(
            EMPTY_PATH,
            fields = listOf(limeField),
            attributes = immutableAttributes,
            functions = listOf(customConstructor)
        )

        assertTrue(validator.validate(limeModel))
    }
}