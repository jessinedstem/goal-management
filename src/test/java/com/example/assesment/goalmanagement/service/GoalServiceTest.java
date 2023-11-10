package com.example.assesment.goalmanagement.service;

import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.GoalProgressResponse;
import com.example.assesment.goalmanagement.contract.GoalUpdateResponse;
import com.example.assesment.goalmanagement.contract.GoalUpdateRequest;
import com.example.assesment.goalmanagement.exception.GoalNotFoundException;
import com.example.assesment.goalmanagement.exception.GoalUpdateNotFoundException;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.model.GoalUpdate;
import com.example.assesment.goalmanagement.repository.GoalRepository;
import com.example.assesment.goalmanagement.repository.GoalUpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalUpdateRepository goalUpdateRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testFindAllGoals() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now().plusDays(7), 50, new ArrayList<>()));
        goals.add(new Goal(2L, "Goal 2", "Description 2", LocalDate.now(), LocalDate.now().plusDays(14), 75, new ArrayList<>()));

        Page<Goal> page = new PageImpl<>(goals);
        when(goalRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);

        List<GoalResponse> goalResponses = goalService.findAllGoals(0, 5);
        assertNotNull(goalResponses);
        assertEquals(2, goalResponses.size());
        GoalResponse goalResponse1 = goalResponses.get(0);
        assertEquals(1L, goalResponse1.getId());
        assertEquals("Goal 1", goalResponse1.getTitle());
        assertEquals("Description 1", goalResponse1.getDescription());
        assertEquals(LocalDate.now(), goalResponse1.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), goalResponse1.getEndDate());
        assertEquals(50, goalResponse1.getProgress());
        assertEquals(0, goalResponse1.getUpdates().size());
        GoalResponse goalResponse2 = goalResponses.get(1);
        assertEquals(2L, goalResponse2.getId());
        assertEquals("Goal 2", goalResponse2.getTitle());
        assertEquals("Description 2", goalResponse2.getDescription());
        assertEquals(LocalDate.now(), goalResponse2.getStartDate());
        assertEquals(LocalDate.now().plusDays(14), goalResponse2.getEndDate());
        assertEquals(75, goalResponse2.getProgress());
        assertEquals(0, goalResponse2.getUpdates().size());
    }

    @Test
    public void testMapToGoalResponse() {
        Goal goal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now().plusDays(7), 50, new ArrayList<>());
        GoalResponse goalResponse = goalService.mapToGoalResponse(goal);
        assertNotNull(goalResponse);
        assertEquals(1L, goalResponse.getId());
        assertEquals("Goal 1", goalResponse.getTitle());
        assertEquals("Description 1", goalResponse.getDescription());
        assertEquals(LocalDate.now(), goalResponse.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), goalResponse.getEndDate());
        assertEquals(50, goalResponse.getProgress());
        assertEquals(0, goalResponse.getUpdates().size());
    }

    @Test
    public void testMapToGoalUpdateResponse() {
        GoalUpdate goalUpdate = new GoalUpdate(1L, "Update 1", LocalDateTime.now(), 25);
        GoalUpdateResponse goalUpdateResponse = goalService.mapToGoalUpdateResponse(goalUpdate);
        assertNotNull(goalUpdateResponse);
        assertEquals(1L, goalUpdateResponse.getId());
        assertEquals("Update 1", goalUpdateResponse.getUpdateText());
        assertNotNull(goalUpdateResponse.getUpdatedDate());
        assertEquals(25, goalUpdateResponse.getProgress());
    }

    @Test
    public void testFindGoalById() {
        Goal testGoal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>());
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        GoalResponse goalResponse = goalService.findGoalById(1L);
        assertEquals(1L, goalResponse.getId());
        assertEquals("Goal 1", goalResponse.getTitle());
        assertEquals("Description 1", goalResponse.getDescription());
        when(goalRepository.findById(2L)).thenReturn(Optional.empty());
        try {
            goalService.findGoalById(2L);
        } catch (GoalNotFoundException e) {
            assertEquals("Goal not found", e.getMessage());
        }
    }

    @Test
    public void testCreateGoal() {
        GoalRequest goalRequest = new GoalRequest("New Goal", "New Description", LocalDate.now(), LocalDate.now());
        Goal savedGoal = new Goal(1L, "New Goal", "New Description", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>());
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);
        GoalResponse response = goalService.createGoal(goalRequest);
        verify(goalRepository, times(1)).save(any(Goal.class));
        assertEquals(1L, response.getId());
        assertEquals("New Goal", response.getTitle());
        assertEquals("New Description", response.getDescription());
        assertEquals(LocalDate.now(), response.getStartDate());
        assertEquals(LocalDate.now(), response.getEndDate());
        assertEquals(0, response.getProgress());
        assertNotNull(response.getUpdates());
        assertEquals(0, response.getUpdates().size());
    }

    @Test
    public void testUpdateGoalById() {
        long goalId = 1L;
        GoalRequest goalRequest = new GoalRequest("Updated Goal", "Updated Description", LocalDate.now(), LocalDate.now());
        try {
            String result = goalService.updateGoalById(goalId, goalRequest);
            assertEquals("Successfully updated the goal with ID " + goalId, result);
        } catch (GoalNotFoundException e) {
        }
    }

    @Test
    public void testDeleteGoalByIdGoalNotFound() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);
        assertThrows(GoalNotFoundException.class, () -> {
            goalService.deleteGoalById(goalId);
        });
    }

    @Test
    public void testAddGoalUpdateToAGoal() {
        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("New Update", 50);
        Goal goal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now().plusDays(7), 50, new ArrayList<>());
        GoalUpdate goalUpdateSaved = new GoalUpdate(1L, "New Update", LocalDateTime.now(), 50);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalUpdateRepository.save(any(GoalUpdate.class))).thenReturn(goalUpdateSaved);
        GoalUpdateResponse response = goalService.addGoalUpdateToAGoal(1L, goalUpdateRequest);
        verify(goalRepository, times(1)).findById(1L);
        verify(goalUpdateRepository, times(1)).save(any(GoalUpdate.class));
        assertEquals(1L, response.getId());
        assertEquals("New Update", response.getUpdateText());
        assertNotNull(response.getUpdatedDate());
        assertEquals(50, response.getProgress());
    }

    @Test
    public void testDeleteGoalUpdate() {
        Goal goal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>());
        GoalUpdate goalUpdate = new GoalUpdate(1L, "Update 1", LocalDateTime.now(), 25);
        goal.getUpdates().add(goalUpdate);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        doNothing().when(goalUpdateRepository).delete(any(GoalUpdate.class));
        String result = goalService.deleteGoalUpdate(1L, 1L);
        verify(goalUpdateRepository, times(1)).delete(any(GoalUpdate.class));
        verify(goalRepository, times(1)).save(any(Goal.class));
        assertEquals("Successfully deleted the GoalUpdate", result);
    }

    @Test
    public void testUpdateGoalUpdate() {
        Goal goal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 0, new ArrayList<>());
        GoalUpdate goalUpdate = new GoalUpdate(1L, "Update 1", LocalDateTime.now(), 25);
        goal.getUpdates().add(goalUpdate);
        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("Updated Update Text", 50);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(goalUpdateRepository.save(any(GoalUpdate.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        GoalUpdateResponse response = goalService.updateGoalUpdate(1L, 1L, goalUpdateRequest);
        verify(goalUpdateRepository, times(1)).save(any(GoalUpdate.class));
        assertEquals(1L, response.getId());
        assertEquals("Updated Update Text", response.getUpdateText());
        assertNotNull(response.getUpdatedDate());
        assertEquals(50, response.getProgress());
    }

    @Test
    public void testGetGoalProgressById() {
        long goalId = 1L;
        Goal testGoal = new Goal(1L, "Goal 1", "Description 1", LocalDate.now(), LocalDate.now(), 50, new ArrayList<>());
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(testGoal));
        GoalProgressResponse goalProgressResponse = goalService.getGoalProgressById(goalId);
        assertEquals(50, goalProgressResponse.getProgress());
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    public void testGetGoalProgressByIdGoalNotFound() {
        long goalId = 2L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> {
            goalService.getGoalProgressById(goalId);
        });
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    public void testFindGoalById_throwsGoalNotFoundException() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.findGoalById(goalId);
        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoalById_throwsGoalNotFoundException() {
        long goalId = 1L;
        GoalRequest goalRequest = new GoalRequest("Sample Goal", "Sample Description", LocalDate.now(), LocalDate.now());
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.updateGoalById(goalId, goalRequest);
        });

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoalById_throwsGoalNotFoundException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.deleteGoalById(goalId);
        });
        assertEquals("Goal with ID " + goalId + " not found.", exception.getMessage());
    }

    @Test
    public void testAddGoalUpdateToAGoal_throwsGoalNotFoundException() {
        Long goalId = 1L;
        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("Sample Update", 50);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.addGoalUpdateToAGoal(goalId, goalUpdateRequest);
        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoalUpdate_throwsGoalNotFoundException() {
        Long goalId = 1L;
        Long goalUpdateId = 2L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.deleteGoalUpdate(goalId, goalUpdateId);
        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoalUpdate_throwsGoalNotFoundException() {
        Long goalId = 1L;
        Long goalUpdateId = 2L;
        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("Updated Update", 75);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class, () -> {
            goalService.updateGoalUpdate(goalId, goalUpdateId, goalUpdateRequest);
        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoalUpdate_throwsGoalUpdateNotFoundException() {

        Long goalId = 1L;

        Long goalUpdateId = 2L;

        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest("Updated Update", 75);
        List<GoalUpdate> goalUpdateList = new ArrayList<>();

        GoalUpdate goalUpdate1 = GoalUpdate.builder()
                .id(7L)
                .updateText("update")
                .build();
        goalUpdateList.add(goalUpdate1);

        Goal goal = Goal.builder()
                .id(1L)
                .title("new-one")
                .description("goalUpdate")
                .updates(goalUpdateList)
                .build();


        GoalUpdate goalUpdate = goal.getUpdates().stream()
                .filter(g -> g.getId() == 2L)
                .findFirst().orElse(null);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        // when(goal.getUpdates()).thenReturn(Collections.emptyList());
        GoalUpdateNotFoundException exception = assertThrows(GoalUpdateNotFoundException.class, () -> {
            goalService.updateGoalUpdate(goalId, goalUpdateId, goalUpdateRequest);
        });

        assertEquals("GoalUpdate not found", exception.getMessage());
    }

    @Test
    public void testGetProgressById() {
        Long goalId = 1L;
        Goal goal = Goal.builder()
                .id(100L)
                .title("goal")
                .progress(80)
                .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException ex=assertThrows(GoalNotFoundException.class, () -> {
            goalService.getGoalProgressById(goalId);
        });
        assertEquals("Goal not found", ex.getMessage());
    }
}
