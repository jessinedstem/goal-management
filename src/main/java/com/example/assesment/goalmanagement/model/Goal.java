package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "goal")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int milestone;
    @OneToMany private List<GoalUpdate> updates;

    private int totalTasks;

    public void updateMilestoneInGoal(int completedTasks, int totalTasks) {
        if (totalTasks == 0) {
            this.milestone = 0;
        } else {
            this.milestone = (int) (((double) completedTasks / totalTasks) * 100);
        }
    }
}
