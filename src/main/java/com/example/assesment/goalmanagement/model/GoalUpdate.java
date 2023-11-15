package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "goal_update")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String updateText;
    private LocalDateTime updatedDate;

    @OneToOne
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    private int completedTasks;
}
