package com.tradesettlement.trade_service.validator;


import com.tradesettlement.trade_service.validator.impl.ValidFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileValidator.class)
public @interface ValidFile {
    String message() default "Invalid file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] allowedContentTypes() default {};
    long maxSize() default Long.MAX_VALUE; //
}
