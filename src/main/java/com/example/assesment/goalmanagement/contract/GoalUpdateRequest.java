package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdateRequest {
    @NotNull(message = "provide necessary updates")
    @NotEmpty(message = "Update should not be empty")
    private String updateText;
    private int progress;
}
