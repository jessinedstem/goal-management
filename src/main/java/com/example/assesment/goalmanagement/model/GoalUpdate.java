package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

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
    private int progress;
}
