package com.example.assesment.goalmanagement.contract;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalResponse {
    private long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private double completedPercentage;
    private List<MilestoneResponse> milestones;
}
