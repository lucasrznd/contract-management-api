package br.com.lucasrznd.contractmanagementapi.storage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that an optional {@link org.springframework.web.multipart.MultipartFile} is an image
 * (JPEG, PNG or WebP). Null or empty files are considered valid.
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ImageFile {

    String message() default "File must be an image (JPEG, PNG or WebP)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
