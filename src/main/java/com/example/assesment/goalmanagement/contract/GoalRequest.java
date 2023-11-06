package com.example.assesment.goalmanagement.contract;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.time.LocalDate;
@Getter
@Setter
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
