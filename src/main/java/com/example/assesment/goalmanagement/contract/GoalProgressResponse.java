package com.example.assesment.goalmanagement.contract;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalProgressResponse {
    private int progress;
}
