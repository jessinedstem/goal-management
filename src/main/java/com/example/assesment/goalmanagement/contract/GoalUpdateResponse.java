package com.example.assesment.goalmanagement.contract;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUpdateResponse {
    private long id;
    private LocalDateTime updatedDate;
    private String updateText;
    private int progress;
}
