package com.example.assesment.goalmanagement.controller;

import com.example.assesment.goalmanagement.contract.*;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.model.GoalUpdate;
import com.example.assesment.goalmanagement.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;
    @Autowired
    public GoalController(GoalService goalService){
            this.goalService=goalService;
}
    @GetMapping()
    public ResponseEntity<List<GoalResponse>> getAllGoals(@RequestParam(defaultValue = "0")int page,
                                                          @RequestParam(defaultValue = "5")int size){
    List<GoalResponse> goalResponses=goalService.findAllGoals(page, size);
    return ResponseEntity.ok(goalResponses);
    }
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> findGoalById(@PathVariable long goalId){
        GoalResponse goal=goalService.findGoalById(goalId);
        return ResponseEntity.ok(goal);
    }

    @PostMapping()
    public ResponseEntity<GoalResponse> createAllGoals(@Valid @RequestBody GoalRequest goalRequest) {
        GoalResponse response = goalService.createGoal(goalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{goalId}")
    public ResponseEntity<String> updateGoalById(@PathVariable long goalId,@Valid @RequestBody GoalRequest goalRequest) {
        String updated = goalService.updateGoalById(goalId, goalRequest);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{goalId}")
    public ResponseEntity<String> deleteGoalById(@PathVariable long goalId) {
        goalService.deleteGoalById(goalId);
        return ResponseEntity.ok("Goal " + goalId + " deleted.");
    }
    @PostMapping("/{goalId}")
    public ResponseEntity<GoalUpdateResponse> addGoalUpdateToAGoal(@PathVariable long goalId,@Valid @RequestBody GoalUpdateRequest goalUpdateRequest){
        GoalUpdateResponse response=goalService.addGoalUpdateToAGoal(goalId,goalUpdateRequest);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{goalId}/goal-updates/{goalUpdateId}")
    public ResponseEntity<String> deleteGoalUpdate(@PathVariable long goalId, @PathVariable long goalUpdateId) {
        String response = goalService.deleteGoalUpdate(goalId, goalUpdateId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{goalId}/{goalUpdateId}")
    public ResponseEntity<GoalUpdateResponse> updateGoalUpdate(
            @PathVariable long goalId,
            @PathVariable long goalUpdateId,
            @RequestBody GoalUpdateRequest goalUpdateRequest) {
        GoalUpdateResponse response = goalService.updateGoalUpdate(goalId, goalUpdateId, goalUpdateRequest);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/progress/{goalId}")
        public ResponseEntity<GoalProgressResponse> viewGoalProgress(@PathVariable long goalId) {
        GoalProgressResponse progressResponse = goalService.getGoalProgressById(goalId);
        return ResponseEntity.ok(progressResponse);
    }
}
