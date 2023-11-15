package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdateRequest {
    @NotBlank(message = "Update text should not be blank")
    private String updateText;

    @Positive(message = "Completed tasks must be a positive number")
    private int completedTasks;
}
