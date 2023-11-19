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
public class MilestoneResponse {
    private long id;
    private long goalId;
    private LocalDateTime updatedDate;
    private String updateText;
    private boolean completed;
}
