package com.example.assesment.goalmanagement.controller;

import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.GoalUpdateRequest;
import com.example.assesment.goalmanagement.contract.GoalUpdateResponse;
import com.example.assesment.goalmanagement.service.GoalService;
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
    public ResponseEntity<List<GoalResponse>> getAllGoals(){
    List<GoalResponse> goalResponses=goalService.findAllGoals();
    return ResponseEntity.ok(goalResponses);
    }
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> findGoalById(@PathVariable long goalId){
        GoalResponse goal=goalService.findGoalById(goalId);
        return ResponseEntity.ok(goal);
    }

    @PostMapping()
    public ResponseEntity<GoalResponse> createAllGoals(@RequestBody GoalRequest goalRequest) {
        GoalResponse response = goalService.createGoal(goalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{goalId}")
    public ResponseEntity<String> updateGoalById(@PathVariable long goalId, @RequestBody GoalRequest goalRequest) {
        String updated = goalService.updateGoalById(goalId, goalRequest);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{goalId}")
    public ResponseEntity<String> deleteGoalById(@PathVariable long goalId) {
        goalService.deleteGoalById(goalId);
        return ResponseEntity.ok("Goal " + goalId + " deleted.");
    }
    @PostMapping("/{goalId}")
    public ResponseEntity<GoalUpdateResponse> addUpdateTextAndProgressToAGoal(@PathVariable long goalId, @RequestBody GoalUpdateRequest goalUpdateRequest){
        GoalUpdateResponse response=goalService.updateTextAndProgress(goalId,goalUpdateRequest);
        return ResponseEntity.ok(response);
    }

}
