package com.example.assesment.goalmanagement.contract;

import lombok.*;
import java.time.LocalDate;
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
