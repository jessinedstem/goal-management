package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;
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
    private int progress;
    @OneToMany
    private List<GoalUpdate> updates;
    public void calculateProgress() {
        if (updates.isEmpty()) {
            progress = 0;
        } else {
            int totalProgress = updates.stream().mapToInt(update-> update.getProgress()).sum();
            progress = totalProgress / updates.size();
        }
    }
}
