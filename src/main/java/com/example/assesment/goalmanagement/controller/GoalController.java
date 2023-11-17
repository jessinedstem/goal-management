package com.example.assesment.goalmanagement.controller;

import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.MilestoneRequest;
import com.example.assesment.goalmanagement.contract.MilestoneResponse;
import com.example.assesment.goalmanagement.service.GoalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {

        this.goalService = goalService;
    }

    @GetMapping()
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<GoalResponse> goalResponses = goalService.findAllGoals(page, size);
        return ResponseEntity.ok(goalResponses);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> findGoalById(@PathVariable long goalId) {
        GoalResponse goal = goalService.findGoalById(goalId);
        return ResponseEntity.ok(goal);
    }

    @PostMapping()
    public ResponseEntity<GoalResponse> createAGoal(@Valid @RequestBody GoalRequest goalRequest) {
        GoalResponse response = goalService.createGoal(goalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<String> updateGoalById(
            @PathVariable long goalId, @Valid @RequestBody GoalRequest goalRequest) {
        String updated = goalService.updateGoalById(goalId, goalRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<String> deleteGoalById(@PathVariable long goalId) {
        goalService.deleteGoalById(goalId);
        return ResponseEntity.ok("Goal " + goalId + " deleted.");
    }

    @PostMapping("/{goalId}")
    public ResponseEntity<MilestoneResponse> addMilestoneToAGoal(
            @PathVariable long goalId, @Valid @RequestBody MilestoneRequest milestoneRequest) {
        MilestoneResponse response = goalService.addMilestoneToAGoal(goalId, milestoneRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}/milestone/{milestoneId}")
    public ResponseEntity<String> deleteMilestone(
            @PathVariable long goalId, @PathVariable long milestoneId) {
        String response = goalService.deleteMilestone(goalId, milestoneId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{goalId}/{milestoneId}")
    public ResponseEntity<MilestoneResponse> updateMilestone(
            @PathVariable long goalId,
            @PathVariable long milestoneId,
            @RequestBody MilestoneRequest milestoneRequest) {
        MilestoneResponse response =
                goalService.updateMilestone(goalId, milestoneId, milestoneRequest);
        return ResponseEntity.ok(response);
    }
}
