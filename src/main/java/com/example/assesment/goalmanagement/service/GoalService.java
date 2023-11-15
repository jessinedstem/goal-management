package com.example.assesment.goalmanagement.service;

import com.example.assesment.goalmanagement.contract.*;
import com.example.assesment.goalmanagement.exception.GoalNotFoundException;
import com.example.assesment.goalmanagement.exception.GoalUpdateNotFoundException;
import com.example.assesment.goalmanagement.exception.TaskCompletedException;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.model.GoalUpdate;
import com.example.assesment.goalmanagement.model.Milestone;
import com.example.assesment.goalmanagement.repository.GoalRepository;
import com.example.assesment.goalmanagement.repository.GoalUpdateRepository;
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
    private final GoalUpdateRepository goalUpdateRepository;
    private final MilestoneRepository milestoneRepository;

    @Autowired
    public GoalService(
            GoalRepository goalRepository,
            GoalUpdateRepository goalUpdateRepository,
            MilestoneRepository milestoneRepository) {
        this.goalRepository = goalRepository;
        this.goalUpdateRepository = goalUpdateRepository;
        this.milestoneRepository = milestoneRepository;
    }

    public List<GoalResponse> findAllGoals(int pageNumber, int pageSize) {
        Page<Goal> goalPage = goalRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return goalPage.getContent().stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }

    public GoalResponse mapToGoalResponse(Goal goal) {
        List<GoalUpdateResponse> updateResponses =
                goal.getUpdates().stream()
                        .map(this::mapToGoalUpdateResponse)
                        .collect(Collectors.toList());
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .totalTasks(goal.getTotalTasks())
                .updates(updateResponses)
                .build();
    }

    public GoalUpdateResponse mapToGoalUpdateResponse(GoalUpdate update) {
        return GoalUpdateResponse.builder()
                .id(update.getId())
                .updateText(update.getUpdateText())
                .updatedDate(update.getUpdatedDate())
                .completedTasks(update.getCompletedTasks())
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
                .milestone(goal.getMilestone())
                .totalTasks(goal.getTotalTasks())
                .updates(
                        goal.getUpdates().stream()
                                .map(
                                        goalUpdate ->
                                                GoalUpdateResponse.builder()
                                                        .id(goalUpdate.getId())
                                                        .updateText(goalUpdate.getUpdateText())
                                                        .updatedDate(goalUpdate.getUpdatedDate())
                                                        .completedTasks(
                                                                goalUpdate.getCompletedTasks())
                                                        .milestone(
                                                                mapMilestoneResponse(
                                                                        goalUpdate.getMilestone()))
                                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    private MilestoneResponse mapMilestoneResponse(Milestone milestone) {
        return milestone != null
                ? MilestoneResponse.builder()
                        .id(milestone.getId())
                        .completedPercentage(milestone.getCompletedPercentage())
                        .build()
                : null;
    }

    public GoalResponse createGoal(GoalRequest goalRequest) {
        Goal newGoal =
                Goal.builder()
                        .title(goalRequest.getTitle())
                        .description(goalRequest.getDescription())
                        .startDate(goalRequest.getStartDate())
                        .endDate(goalRequest.getEndDate())
                        .milestone(0)
                        .totalTasks(goalRequest.getTotalTasks())
                        .updates(new ArrayList<>())
                        .build();
        Goal savedGoal = goalRepository.save(newGoal);
        return GoalResponse.builder()
                .id(savedGoal.getId())
                .title(savedGoal.getTitle())
                .description((savedGoal.getDescription()))
                .startDate(savedGoal.getStartDate())
                .endDate(savedGoal.getEndDate())
                .milestone(savedGoal.getMilestone())
                .totalTasks(savedGoal.getTotalTasks())
                .updates(new ArrayList<>())
                .build();
    }

    public String updateGoalById(long goalId, GoalRequestUpdate goalRequestUpdate) {
        Goal goal = goalRepository.findById(goalId).orElse(null);
        if (goal == null) {
            throw new GoalNotFoundException("Goal not found");
        }
        Goal updated =
                Goal.builder()
                        .id(goal.getId())
                        .title(goalRequestUpdate.getTitle())
                        .description(goalRequestUpdate.getDescription())
                        .startDate(goalRequestUpdate.getStartDate())
                        .endDate(goalRequestUpdate.getEndDate())
                        .totalTasks(goal.getTotalTasks())
                        .updates(goal.getUpdates())
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

    public GoalUpdateResponse addGoalUpdateToAGoal(
            long goalId, GoalUpdateRequest goalUpdateRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        Milestone newMilestone = Milestone.builder().build();
        milestoneRepository.save(newMilestone);

        int currentCompletedTask =
                goal.getUpdates().stream()
                        .mapToInt(goalUpdate -> goalUpdate.getCompletedTasks())
                        .sum();
        int difference = goal.getTotalTasks() - currentCompletedTask;
        if (goalUpdateRequest.getCompletedTasks() > difference) {
            throw new TaskCompletedException(
                    "You only have " + difference + " tasks left to complete");
        }

        GoalUpdate update =
                GoalUpdate.builder()
                        .updateText(goalUpdateRequest.getUpdateText())
                        .updatedDate(LocalDateTime.now())
                        .completedTasks(goalUpdateRequest.getCompletedTasks())
                        .milestone(newMilestone)
                        .build();

        GoalUpdate goalUpdateSaved = goalUpdateRepository.save(update);

        int progress =
                (int) (((double) goalUpdateSaved.getCompletedTasks() / goal.getTotalTasks()) * 100);

        Milestone addedMilestone =
                Milestone.builder()
                        .id(goalUpdateSaved.getMilestone().getId())
                        .completedPercentage(progress)
                        .build();

        Milestone saved = milestoneRepository.save(addedMilestone);

        GoalUpdate finalUpdate =
                GoalUpdate.builder()
                        .id(goalUpdateSaved.getId())
                        .updateText(goalUpdateSaved.getUpdateText())
                        .updatedDate(goalUpdateSaved.getUpdatedDate())
                        .completedTasks(goalUpdateSaved.getCompletedTasks())
                        .milestone(saved)
                        .build();

        GoalUpdate savedGoalUpdate = goalUpdateRepository.save(finalUpdate);

        goal.getUpdates().add(savedGoalUpdate);
        int finalCompletedTask =
                goal.getUpdates().stream()
                        .mapToInt(goalUpdate -> goalUpdate.getCompletedTasks())
                        .sum();
        goal.updateMilestoneInGoal(finalCompletedTask, goal.getTotalTasks());
        Goal savedGoal = goalRepository.save(goal);

        return GoalUpdateResponse.builder()
                .id(savedGoalUpdate.getId())
                .updateText(savedGoalUpdate.getUpdateText())
                .updatedDate(savedGoalUpdate.getUpdatedDate())
                .completedTasks(savedGoalUpdate.getCompletedTasks())
                .milestone(
                        MilestoneResponse.builder()
                                .id(savedGoalUpdate.getMilestone().getId())
                                .completedPercentage(
                                        savedGoalUpdate.getMilestone().getCompletedPercentage())
                                .build())
                .build();
    }

    public String deleteGoalUpdate(long goalId, long goalUpdateId) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        GoalUpdate goalUpdate =
                goal.getUpdates().stream()
                        .filter(g -> g.getId() == goalUpdateId)
                        .findFirst()
                        .orElse(null);
        if (goalUpdate == null) {
            throw new GoalUpdateNotFoundException("GoalUpdate not found");
        }
        int removedCompletedTasks = goalUpdate.getCompletedTasks();
        int totalBeforeRemove =
                goal.getUpdates().stream().mapToInt(g -> g.getCompletedTasks()).sum();
        int netCompletedTasks = totalBeforeRemove - removedCompletedTasks;
        goal.getUpdates().remove(goalUpdate);
        goalUpdateRepository.delete(goalUpdate);
        goal.updateMilestoneInGoal(netCompletedTasks, goal.getTotalTasks());
        goalRepository.save(goal);
        return "Successfully deleted the GoalUpdate";
    }

    public GoalUpdateResponse updateGoalUpdate(
            long goalId, long goalUpdateId, GoalUpdateRequest goalUpdateRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        GoalUpdate goalUpdate =
                goal.getUpdates().stream()
                        .filter(g -> g.getId() == goalUpdateId)
                        .findFirst()
                        .orElse(null);
        if (goalUpdate == null) {
            throw new GoalUpdateNotFoundException("GoalUpdate not found");
        }
        int currentCompletedTask =
                goal.getUpdates().stream().mapToInt(g -> g.getCompletedTasks()).sum();
        int alreadyExistedTask = goalUpdate.getCompletedTasks();
        int taskLeft = currentCompletedTask - alreadyExistedTask;
        int allowedTasks = goal.getTotalTasks() - taskLeft;
        if (goalUpdateRequest.getCompletedTasks() > allowedTasks) {
            throw new TaskCompletedException(
                    "You only have " + allowedTasks + " tasks to complete");
        }
        int progress =
                (int)
                        (((double) goalUpdateRequest.getCompletedTasks() / goal.getTotalTasks())
                                * 100);

        Milestone milestone = goalUpdate.getMilestone();
        if (milestone != null) {
            Milestone updatedMilestone =
                    Milestone.builder().id(milestone.getId()).completedPercentage(progress).build();

            milestoneRepository.save(updatedMilestone);
        }

        GoalUpdate updatedGoalUpdate =
                GoalUpdate.builder()
                        .id(goalUpdate.getId())
                        .updatedDate(LocalDateTime.now())
                        .updateText(goalUpdateRequest.getUpdateText())
                        .completedTasks(goalUpdateRequest.getCompletedTasks())
                        .milestone(goalUpdate.getMilestone())
                        .build();

        GoalUpdate saved = goalUpdateRepository.save(updatedGoalUpdate);
        int finalCompletedTask =
                goal.getUpdates().stream().mapToInt(g -> g.getCompletedTasks()).sum();
        goal.updateMilestoneInGoal(finalCompletedTask, goal.getTotalTasks());
        goalRepository.save(goal);
        return GoalUpdateResponse.builder()
                .id(saved.getId())
                .updatedDate(saved.getUpdatedDate())
                .updateText(saved.getUpdateText())
                .completedTasks(saved.getCompletedTasks())
                .milestone(mapNewMilestoneResponse(saved.getMilestone()))
                .build();
    }

    private MilestoneResponse mapNewMilestoneResponse(Milestone milestone) {
        return milestone != null
                ? MilestoneResponse.builder()
                        .id(milestone.getId())
                        .completedPercentage(milestone.getCompletedPercentage())
                        .build()
                : null;
    }
}
