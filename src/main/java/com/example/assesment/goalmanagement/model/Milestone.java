package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "milestone")
@AllArgsConstructor
@NoArgsConstructor
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int completedPercentage;

    public int calculateProgressValue(int completedTasks, int totalTasks) {
        if (totalTasks == 0) {
            return 0;
        } else {
            return (int) (((double) completedTasks / totalTasks) * 100);
        }
    }
}
