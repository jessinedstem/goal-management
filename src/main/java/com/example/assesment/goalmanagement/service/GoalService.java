package com.example.assesment.goalmanagement.service;

import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.GoalResponse;
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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    private final ModelMapper modelMapper;

    public List<GoalResponse> findAllGoals(int pageNumber, int pageSize) {
        Page<Goal> goalPage = goalRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return goalPage.getContent().stream()
                .map(goal -> modelMapper.map(goal, GoalResponse.class))
                .collect(Collectors.toList());
    }

    public GoalResponse findGoalById(long goalId) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        return modelMapper.map(goal, GoalResponse.class);
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
        return modelMapper.map(savedGoal, GoalResponse.class);
    }

    public String updateGoalById(long goalId, GoalRequest goalRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

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

    public MilestoneResponse addMilestoneToAGoal(long goalId, MilestoneRequest milestoneRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

        Milestone update =
                Milestone.builder()
                        .goalId(goal.getId())
                        .updateText(milestoneRequest.getUpdateText())
                        .updatedDate(LocalDateTime.now())
                        .completed(milestoneRequest.getCompleted())
                        .build();

        Milestone milestoneSaved = milestoneRepository.save(update);
        goal.getMilestones().add(milestoneSaved);
        long totalMilestones = goal.getMilestones().size();
        long completedMilestones =
                goal.getMilestones().stream().filter(Milestone::getCompleted).count();

        goal.updatePercentageInGoal(completedMilestones, totalMilestones);
        goalRepository.save(goal);
        return modelMapper.map(milestoneSaved, MilestoneResponse.class);
    }

    public void deleteMilestone(long goalId, long milestoneId) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        Milestone milestone =
                goal.getMilestones().stream()
                        .filter(m -> m.getId() == milestoneId)
                        .findFirst()
                        .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));

        goal.getMilestones().remove(milestone);
        milestoneRepository.delete(milestone);
        long totalMilestonesNow = goal.getMilestones().size();
        long completedMilestones =
                goal.getMilestones().stream().filter(Milestone::getCompleted).count();
        goal.updatePercentageInGoal(completedMilestones, totalMilestonesNow);
        goalRepository.save(goal);
    }

    public MilestoneResponse updateMilestone(
            long goalId, long milestoneId, MilestoneRequest milestoneRequest) {
        Goal goal =
                goalRepository
                        .findById(goalId)
                        .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        Milestone milestone =
                goal.getMilestones().stream()
                        .filter(m -> m.getId() == milestoneId)
                        .findFirst()
                        .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found"));

        Milestone updatedMilestone =
                Milestone.builder()
                        .id(milestone.getId())
                        .goalId(goal.getId())
                        .updatedDate(LocalDateTime.now())
                        .updateText(milestoneRequest.getUpdateText())
                        .completed(milestoneRequest.getCompleted())
                        .build();

        Milestone saved = milestoneRepository.save(updatedMilestone);
        long totalMilestones = goal.getMilestones().size();
        long completedMilestones =
                goal.getMilestones().stream().filter(Milestone::getCompleted).count();
        goal.updatePercentageInGoal(completedMilestones, totalMilestones);
        goalRepository.save(goal);
        return modelMapper.map(saved, MilestoneResponse.class);
    }
}
