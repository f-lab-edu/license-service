package com.flab.license.common.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValueValidator::class])
annotation class EnumValue(
    val message: String = "유효하지 않은 값입니다",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val enumClass: KClass<out Enum<*>>,
    val ignoreCase: Boolean = false
)

class EnumValueValidator : ConstraintValidator<EnumValue, String> {

    private lateinit var enumValues: Array<out Enum<*>>
    private var ignoreCase: Boolean = false

    override fun initialize(constraintAnnotation: EnumValue) {
        this.enumValues = constraintAnnotation.enumClass.java.enumConstants
        this.ignoreCase = constraintAnnotation.ignoreCase
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true  // @NotBlank가 처리

        return enumValues.any { enumValue ->
            value.equals(enumValue.name, ignoreCase = this.ignoreCase)
        }
    }
}
