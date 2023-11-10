package com.example.assesment.goalmanagement.contract;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
@Getter
@AllArgsConstructor
@Builder
public class GoalRequest {
    @NotEmpty(message = "Title should not be empty")
    private String title;
    @NotEmpty(message = "Description is mandatory")
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
