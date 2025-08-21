package com.tradesettlement.trade_service.validator.impl;

import com.tradesettlement.trade_service.validator.ValidFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

// ConstraintValidator
public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private String[] allowedContentTypes;
    private long maxSize;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.allowedContentTypes = constraintAnnotation.allowedContentTypes();
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty").addConstraintViolation();
            return false;
        }
        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size exceeds limit").addConstraintViolation();
            return false;
        }
        if (allowedContentTypes.length > 0 && !Arrays.asList(allowedContentTypes).contains(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid file type").addConstraintViolation();
            return false;
        }
        final Set<String> tradeIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                boolean isHeader = Arrays.stream(tokens).anyMatch(
                     token -> token.equalsIgnoreCase("tradeId"));
                if (isHeader) {
                    continue;
                }
                String tradeId = tokens[0];
                int quantity = Integer.parseInt(tokens[2]);
                String price = tokens[3];
                if (!tradeIds.add(tradeId)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Trade ID is not unique").addConstraintViolation();
                    return false;
                }
                if (quantity < 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Quantity should be positive").addConstraintViolation();
                    return false;
                }
                if (!isValidDecimal(price)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Price should be a valid decimal").addConstraintViolation();
                    return false;
                }
            }
            // Process the CSV lines (e.g., parse, save to database)
            System.out.println("Uploaded CSV content:");
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false; // Handle null or empty strings
        }
        try {
            new BigDecimal(str); // Attempt to create a BigDecimal object
            return true; // If successful, it's a valid decimal
        } catch (NumberFormatException e) {
            return false; // If parsing fails, it's not a valid decimal
        }
    }
}
