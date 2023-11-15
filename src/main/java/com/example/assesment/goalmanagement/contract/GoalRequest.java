package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GoalRequest {
    @NotBlank(message = "Title should not be blank")
    private String title;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @NotNull(message = "Start date should not be null")
    private LocalDate startDate;

    @NotNull(message = "End date should not be null")
    private LocalDate endDate;

    @Positive(message = "Total tasks must be a positive")
    private int totalTasks;
}
