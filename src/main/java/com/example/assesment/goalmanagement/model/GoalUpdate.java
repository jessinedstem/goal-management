package com.example.assesment.goalmanagement.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

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
