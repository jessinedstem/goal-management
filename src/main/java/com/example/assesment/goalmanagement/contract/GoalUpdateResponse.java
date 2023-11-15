package com.example.assesment.goalmanagement.contract;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUpdateResponse {
    private long id;
    private LocalDateTime updatedDate;
    private String updateText;
    private MilestoneResponse milestone;
    private int completedTasks;
}
