package com.example.assesment.goalmanagement.validation;

import com.example.assesment.goalmanagement.contract.GoalRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FutureDateValidator implements ConstraintValidator<FutureDate, GoalRequest> {
    @Override
    public boolean isValid(GoalRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.getEndDate() == null
                || value.getStartDate() == null
                || value.getEndDate().isAfter(value.getStartDate());
    }
}
