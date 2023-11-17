package com.example.assesment.goalmanagement.service;

import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.MilestoneRequest;
import com.example.assesment.goalmanagement.contract.MilestoneResponse;
import com.example.assesment.goalmanagement.exception.GoalNotFoundException;
import com.example.assesment.goalmanagement.exception.MilestoneNotFoundException;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.model.Milestone;
import com.example.assesment.goalmanagement.repository.GoalRepository;
import com.example.assesment.goalmanagement.repository.MilestoneRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    @Autowired
    public GoalService(
            GoalRepository goalRepository,
            MilestoneRepository milestoneRepository) {
        this.goalRepository = goalRepository;
        this.milestoneRepository = milestoneRepository;
    }

    public List<GoalResponse> findAllGoals(int pageNumber, int pageSize) {
        Page<Goal> goalPage = goalRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return goalPage.getContent().stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }

    public GoalResponse mapToGoalResponse(Goal goal) {
        List<MilestoneResponse> updateResponses =
                goal.getMilestones().stream()
                        .map(this::mapToMilestoneResponse)
                        .collect(Collectors.toList());
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .milestones(updateResponses)
                .build();
    }

    public MilestoneResponse mapToMilestoneResponse(Milestone update) {
        return MilestoneResponse.builder()
                .id(update.getId())
                .goalId(update.getGoalId())
                .updateText(update.getUpdateText())
                .updatedDate(update.getUpdatedDate())
                .completed(update.isCompleted())
                .build();
    }

    public GoalResponse findGoalById(long goalId) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .completedPercentage(goal.getCompletedPercentage())
                .milestones(
                        goal.getMilestones().stream()
                                .map(
                                        milestone ->
                                                MilestoneResponse.builder()
                                                        .id(milestone.getId())
                                                        .goalId(goal.getId())
                                                        .updateText(milestone.getUpdateText())
                                                        .updatedDate(milestone.getUpdatedDate())
                                                        .completed(
                                                                milestone.isCompleted())

                                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    public GoalResponse createGoal(GoalRequest goalRequest) {
        Goal newGoal =
                Goal.builder()
                        .title(goalRequest.getTitle())
                        .description(goalRequest.getDescription())
                        .startDate(goalRequest.getStartDate())
                        .endDate(goalRequest.getEndDate())
                        .milestones(new ArrayList<>())
                        .build();
        Goal savedGoal = goalRepository.save(newGoal);
        return GoalResponse.builder()
                .id(savedGoal.getId())
                .title(savedGoal.getTitle())
                .description((savedGoal.getDescription()))
                .startDate(savedGoal.getStartDate())
                .endDate(savedGoal.getEndDate())
                .milestones(new ArrayList<>())
                .completedPercentage(0)
                .build();
    }

    public String updateGoalById(long goalId, GoalRequest goalRequest) {
        Goal goal = goalRepository.findById(goalId).orElse(null);
        if (goal == null) {
            throw new GoalNotFoundException("Goal not found");
        }
        Goal updated =
                Goal.builder()
                        .id(goal.getId())
                        .title(goalRequest.getTitle())
                        .description(goalRequest.getDescription())
                        .startDate(goalRequest.getStartDate())
                        .endDate(goalRequest.getEndDate())
                        .milestones(goal.getMilestones())
                        .completedPercentage(goal.getCompletedPercentage())
                        .build();
        Goal saved = goalRepository.save(updated);
        return "Successfully updated the goal with ID " + saved.getId();
    }

    public void deleteGoalById(Long goalId) {
        if (!goalRepository.existsById((goalId))) {
            throw new GoalNotFoundException("Goal with ID " + goalId + " not found.");
        }
        goalRepository.deleteById(goalId);
    }

    public MilestoneResponse addMilestoneToAGoal(
            long goalId, MilestoneRequest milestoneRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

        Milestone update =
                Milestone.builder()
                        .goalId(goal.getId())
                        .updateText(milestoneRequest.getUpdateText())
                        .updatedDate(LocalDateTime.now())
                        .completed(milestoneRequest.isCompleted())
                                .build();

        Milestone milestoneSaved = milestoneRepository.save(update);
        goal.getMilestones().add(milestoneSaved);
        long totalMilestones=goal.getMilestones().size();
        long completedMilestones=goal.getMilestones()
                .stream()
                .filter(Milestone::isCompleted)
                .count();

        goal.updatePercentageInGoal(completedMilestones, totalMilestones);
        Goal savedGoal = goalRepository.save(goal);

        return MilestoneResponse.builder()
                .id(milestoneSaved.getId())
                .goalId(goal.getId())
                .updateText(milestoneSaved.getUpdateText())
                .updatedDate(milestoneSaved.getUpdatedDate())
                .completed(milestoneSaved.isCompleted())
                .build();
    }

    public String deleteMilestone(long goalId, long milestoneId) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        Milestone milestone =
                goal.getMilestones().stream()
                        .filter(m -> m.getId() == milestoneId)
                        .findFirst()
                        .orElse(null);
        if (milestone == null) {
            throw new MilestoneNotFoundException("Milestone not found");
        }
        goal.getMilestones().remove(milestone);
        milestoneRepository.delete(milestone);
        long totalMilestonesNow = goal.getMilestones().size();
        long completedMilestones=
                goal.getMilestones().stream().filter(Milestone::isCompleted)
                        .count();
        goal.updatePercentageInGoal(completedMilestones,totalMilestonesNow);
        goalRepository.save(goal);
        return "Successfully deleted the Milestone";
    }

    public MilestoneResponse updateMilestone(
            long goalId, long milestoneId, MilestoneRequest milestoneRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        Milestone milestone =
                goal.getMilestones().stream()
                        .filter(m-> m.getId() == milestoneId)
                        .findFirst()
                        .orElse(null);
        if (milestone == null) {
            throw new MilestoneNotFoundException("Milestone not found");
        }

        Milestone updatedMilestone =
                Milestone.builder()
                        .id(milestone.getId())
                        .goalId(goal.getId())
                        .updatedDate(LocalDateTime.now())
                        .updateText(milestoneRequest.getUpdateText())
                        .completed(milestoneRequest.isCompleted())
                        .build();

        Milestone saved = milestoneRepository.save(updatedMilestone);
        long totalMilestones=goal.getMilestones().size();
        long completedMilestones=goal.getMilestones()
                .stream()
                .filter(Milestone::isCompleted)
                        .count();
        goal.updatePercentageInGoal(completedMilestones, totalMilestones);
        goalRepository.save(goal);
        return MilestoneResponse.builder()
                .id(saved.getId())
                .goalId(goal.getId())
                .updatedDate(saved.getUpdatedDate())
                .updateText(saved.getUpdateText())
                .completed(saved.isCompleted())
                .build();
    }
}
