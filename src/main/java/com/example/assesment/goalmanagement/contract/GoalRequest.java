package com.example.assesment.goalmanagement.contract;

import com.example.assesment.goalmanagement.validation.FutureDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@FutureDate(message = "End date must be after start date")
public class GoalRequest {
    @NotBlank(message = "Title should not be blank")
    private String title;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @NotNull(message = "Start date should not be null")
    @FutureOrPresent(message = "Start date should be a present date or a date in the future ")
    private LocalDate startDate;

    @NotNull(message = "End date should not be null")
    @Future(message = "End date should be a date in the future")
    private LocalDate endDate;
}
