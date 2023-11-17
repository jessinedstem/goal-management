package com.example.assesment.goalmanagement.contract;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

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

