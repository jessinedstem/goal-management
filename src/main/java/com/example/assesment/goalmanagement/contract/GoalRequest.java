package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    }
