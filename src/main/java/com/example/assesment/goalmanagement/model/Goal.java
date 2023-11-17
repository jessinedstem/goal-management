package com.example.assesment.goalmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.text.DecimalFormat;
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
    private double completedPercentage;
    @OneToMany private List<Milestone> milestones;
    public void updatePercentageInGoal(long completedMilestones, long totalMilestones) {
        if (totalMilestones == 0) {
            this.completedPercentage = 0.0;
        } else {
            double percentage = ((double) completedMilestones / totalMilestones) * 100;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            this.completedPercentage = Double.parseDouble(decimalFormat.format(percentage));}
    }
}
