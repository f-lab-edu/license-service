package com.flab.license.api

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

object CommonResponseFields {

    fun success(): List<FieldDescriptor> = listOf(
        fieldWithPath("success")
            .type(JsonFieldType.BOOLEAN)
            .description("성공 여부")
    )
}
