package com.example.assesment.goalmanagement.contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalRequest {
    @NotNull(message = "Title is required")
    @NotEmpty(message = "Title should not be empty")
    private String title;
    private String description;
    @NotNull(message = "Start date is required")
    @NotEmpty(message = "Start date should not be empty")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    @NotEmpty(message="End date should not be empty")
    private LocalDate endDate;
}
