package com.nuvola.gxpenses.common.shared.business.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameUniqueContraintValidator.class)
public @interface UsernameUnique {
    String message() default "Username already exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
