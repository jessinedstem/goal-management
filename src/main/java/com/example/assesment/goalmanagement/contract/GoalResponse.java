package com.example.assesment.goalmanagement.contract;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;
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
    private int progress;
    private List<GoalUpdateResponse> updates;
}
