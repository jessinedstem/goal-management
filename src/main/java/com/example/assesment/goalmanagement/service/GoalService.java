package com.example.assesment.goalmanagement.service;

import com.example.assesment.goalmanagement.contract.*;
import com.example.assesment.goalmanagement.exception.GoalNotFoundException;
import com.example.assesment.goalmanagement.exception.GoalUpdateNotFoundException;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.model.GoalUpdate;
import com.example.assesment.goalmanagement.repository.GoalRepository;
import com.example.assesment.goalmanagement.repository.GoalUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalUpdateRepository goalUpdateRepository;

    @Autowired
    public GoalService(GoalRepository  goalRepository,GoalUpdateRepository goalUpdateRepository) {
        this.goalRepository = goalRepository;
        this.goalUpdateRepository=goalUpdateRepository;
    }
    public List<GoalResponse> findAllGoals(int pageNumber, int pageSize) {
        Page<Goal> goalPage=goalRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return goalPage.getContent().stream()
                .map(this::mapToGoalResponse)
                .collect(Collectors.toList());
    }
    public GoalResponse mapToGoalResponse(Goal goal) {
        List<GoalUpdateResponse> updateResponses = goal.getUpdates().stream()
                .map(this::mapToGoalUpdateResponse)
                .collect(Collectors.toList());
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .progress(goal.getProgress())
                .updates(updateResponses)
                .build();
    }
    public GoalUpdateResponse mapToGoalUpdateResponse(GoalUpdate update) {
        return GoalUpdateResponse.builder()
                .id(update.getId())
                .updateText(update.getUpdateText())
                .updatedDate(update.getUpdatedDate())
                .progress(update.getProgress())
                .build();
    }
    public GoalResponse findGoalById(long goalId) {
        Goal goal=goalRepository.findById(goalId)
                .orElseThrow(()->new GoalNotFoundException("Goal not found"));
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .progress(goal.getProgress())
                .updates(goal.getUpdates().stream()
                        .map(goalUpdate -> GoalUpdateResponse.builder()
                                .id(goalUpdate.getId())
                                .updateText(goalUpdate.getUpdateText())
                                .updatedDate(goalUpdate.getUpdatedDate())
                                .progress(goalUpdate.getProgress())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
    public GoalResponse createGoal(GoalRequest goalRequest) {
        Goal newGoal = Goal.builder()
                .title(goalRequest.getTitle())
                .description(goalRequest.getDescription())
                .startDate(goalRequest.getStartDate())
                .endDate(goalRequest.getEndDate())
                .updates(new ArrayList<>())
                .build();
        Goal savedGoal = goalRepository.save(newGoal);
               return GoalResponse.builder()
                .id(savedGoal.getId())
                       .title(savedGoal.getTitle())
                       .description((savedGoal.getDescription()))
                       .startDate(savedGoal.getStartDate())
                       .endDate(savedGoal.getEndDate())
                       .progress(savedGoal.getProgress())
                       .updates(new ArrayList<>())
                .build();
    }
    public String updateGoalById(long goalId, GoalRequest goalRequest) {
        Goal goal=goalRepository.findById(goalId).orElse(null);
        if(goal==null){
            throw new GoalNotFoundException("Goal not found");
        }
        Goal updated=Goal.builder()
                .id(goal.getId())
                .title(goalRequest.getTitle())
                .description(goalRequest.getDescription())
                .startDate(goalRequest.getStartDate())
                .endDate(goalRequest.getEndDate())
                .progress(goal.getProgress())
                .updates(goal.getUpdates())
                .build();
        Goal saved=goalRepository.save(updated);
        return "Successfully updated the goal with ID " +saved.getId();
    }
    public void deleteGoalById(Long goalId) {
        if (!goalRepository.existsById((goalId))) {
            throw new GoalNotFoundException("Goal with ID " + goalId + " not found.");
        }
        goalRepository.deleteById(goalId);
    }
    public GoalUpdateResponse addGoalUpdateToAGoal(long goalId, GoalUpdateRequest goalUpdateRequest) {
        Goal goal=goalRepository.findById(goalId)
                .orElseThrow(()-> new
                        GoalNotFoundException("Goal not found"));
        GoalUpdate update=GoalUpdate.builder()
                .updateText(goalUpdateRequest.getUpdateText())
                .updatedDate(LocalDateTime.now())
                .progress(goalUpdateRequest.getProgress())
                .build();
        GoalUpdate goalUpdateSaved=goalUpdateRepository.save(update);
        goal.getUpdates().add(goalUpdateSaved);
        goal.calculateProgress();
        Goal saved=goalRepository.save(goal);
        return GoalUpdateResponse.builder()
        .id(goalUpdateSaved.getId())
                .updateText(goalUpdateSaved.getUpdateText())
                .updatedDate(goalUpdateSaved.getUpdatedDate())
                .progress(goalUpdateSaved.getProgress())
               .build();
    }

    public String deleteGoalUpdate(long goalId, long goalUpdateId) {
        Goal goal=goalRepository.findById(goalId)
                .orElseThrow(()-> new
                        GoalNotFoundException("Goal not found"));
        GoalUpdate goalUpdate=goal.getUpdates().stream()
                        .filter(g ->g.getId()==goalUpdateId)
                                .findFirst().orElse(null);
        if(goalUpdate==null)
        {throw new GoalUpdateNotFoundException("GoalUpdate not found");}
        goal.getUpdates().remove(goalUpdate);
        goalUpdateRepository.delete(goalUpdate);
        goalRepository.save(goal);
        return "Successfully deleted the GoalUpdate";
    }
    public GoalUpdateResponse updateGoalUpdate(long goalId, long goalUpdateId, GoalUpdateRequest goalUpdateRequest) {
        Goal goal=goalRepository.findById(goalId)
                .orElseThrow(()-> new
                        GoalNotFoundException("Goal not found"));
GoalUpdate goalUpdate=goal.getUpdates().stream()
        .filter(g->g.getId()==goalUpdateId)
        .findFirst().orElse(null);
        if(goalUpdate==null)
        {throw new GoalUpdateNotFoundException("GoalUpdate not found");}
        GoalUpdate updatedGoalUpdate = GoalUpdate.builder()
                .id(goalUpdate.getId())
                .updatedDate(LocalDateTime.now())
                .updateText(goalUpdateRequest.getUpdateText())
                .progress(goalUpdateRequest.getProgress())
                .build();
       GoalUpdate saved= goalUpdateRepository.save(updatedGoalUpdate);
       goal.calculateProgress();
        return GoalUpdateResponse.builder()
                .id(saved.getId())
                .updatedDate(saved.getUpdatedDate())
                .updateText(saved.getUpdateText())
                .progress(saved.getProgress())
                .build();
    }

    public GoalProgressResponse getGoalProgressById(long goalId) {
        Goal goal=goalRepository.findById(goalId)
                .orElseThrow(()-> new
                        GoalNotFoundException("Goal not found"));
        return GoalProgressResponse.builder()
                .progress(goal.getProgress())
                .build();
    }
}
