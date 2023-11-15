package com.example.assesment.goalmanagement.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilestoneResponse {
    private long id;
    private int completedPercentage;
}
