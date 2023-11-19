package com.example.assesment.goalmanagement.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilestoneRequest {
    @NotBlank(message = "Update text should not be blank")
    private String updateText;

    @NotNull(message = "Completed is mandatory")
    private boolean completed;
}
