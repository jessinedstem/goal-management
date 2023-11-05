//package com.example.assesment.goalmanagement.service;
//
//import com.example.assesment.goalmanagement.contract.GoalRequest;
//import com.example.assesment.goalmanagement.contract.GoalResponse;
//import com.example.assesment.goalmanagement.contract.GoalUpdateRequest;
//import com.example.assesment.goalmanagement.contract.GoalUpdateResponse;
//import com.example.assesment.goalmanagement.exception.GoalNotFoundException;
//import com.example.assesment.goalmanagement.model.Goal;
//import com.example.assesment.goalmanagement.model.GoalUpdate;
//import com.example.assesment.goalmanagement.repository.GoalRepository;
//import com.example.assesment.goalmanagement.repository.GoalUpdateRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class GoalServiceTest {
//    @InjectMocks
//    private GoalService goalService;
//    @Mock
//    private GoalRepository goalRepository;
//    @Mock
//    private GoalUpdateRepository goalUpdateRepository;
//
//    @BeforeEach
//    public void init() {
//        MockitoAnnotations.openMocks(this);
//
//    }
//
//    @Test
//    public void testFindAllGoals() {
//        List<Goal> goals = new ArrayList<>();
//        goals.add(new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>()));
//        goals.add(new Goal(2L, "Goal 2", "Description 2", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>()));
//
//        when(goalRepository.findAll()).thenReturn(goals);
//        List<GoalResponse> goalResponses = goalService.findAllGoals();
//
//        assertEquals(2, goalResponses.size());
//        GoalResponse goalResponse1 = goalResponses.get(0);
//        assertEquals(1L, goalResponse1.getId());
//        assertEquals("Goal 1", goalResponse1.getTitle());
//        GoalResponse goalResponse2 = goalResponses.get(1);
//        assertEquals(2L, goalResponse2.getId());
//        assertEquals("Goal 2", goalResponse2.getTitle());
//    }
//
//    @Test
//    public void testFindGoalById() {
//        Goal testGoal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>());
//        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
//        GoalResponse goalResponse = goalService.findGoalById(1L);
//        assertEquals(1L, goalResponse.getId());
//        assertEquals("Goal 1", goalResponse.getTitle());
//        assertEquals("Description 1", goalResponse.getDescription());
//        when(goalRepository.findById(2L)).thenReturn(Optional.empty());
//        try {
//            goalService.findGoalById(2L);
//        } catch (GoalNotFoundException e) {
//            assertEquals("Goal not found", e.getMessage());
//        }
//    }
//    @Test
//    public void testCreateGoal() {
//        // Create a test goal request
//        GoalRequest goalRequest = new GoalRequest();
//        goalRequest.setTitle("Goal 1");
//        goalRequest.setDescription("Description 1");
//        goalRequest.setStartDate(LocalDate.now());
//        goalRequest.setEndDate(LocalDate.now());
//
//        GoalResponse goalResponse = goalService.createGoal(goalRequest);
//        assertNotNull(goalResponse.getId());
//        assertEquals("Goal 1", goalResponse.getTitle());
//        assertEquals("Description 1", goalResponse.getDescription());
//        assertEquals(LocalDate.now(), goalResponse.getStartDate());
//        assertEquals(LocalDate.now(), goalResponse.getEndDate());
//        assertEquals(0, goalResponse.getProgress());
//        assertTrue(goalResponse.getUpdates().isEmpty());
//    }
//    @Test
//    public void testUpdateGoalById() {
//        long goalId = 1L;
//        GoalRequest goalRequest = new GoalRequest("Updated Goal", "Updated Description", LocalDate.now(), LocalDate.now());
//        try {
//            String result = goalService.updateGoalById(goalId, goalRequest);
//            assertEquals("Successfully updated the goal with ID " + goalId, result);
//        } catch (GoalNotFoundException e) {
//
//        }
//    }
//    @Test
//    public void testDeleteGoalByIdGoalNotFound() {
//        Long goalId = 1L;
//        when(goalRepository.existsById(goalId)).thenReturn(false);
//        assertThrows(GoalNotFoundException.class, () -> {
//            goalService.deleteGoalById(goalId);
//        });
//    }
//
//    @Test
//    public void testUpdateTextAndProgress() {
//        long goalId = 1L;
//        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("Update Text", 50);
//        try {
//            GoalUpdateResponse response = goalService.updateTextAndProgress(goalId, goalUpdateRequest);
//        }
//        catch (GoalNotFoundException e)
//        {
//                    }
//    }
//}
