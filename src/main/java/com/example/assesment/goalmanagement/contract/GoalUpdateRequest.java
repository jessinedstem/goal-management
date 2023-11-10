package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdateRequest {
    @NotEmpty(message = "Update should not be empty")
    private String updateText;
    private int progress;
}
