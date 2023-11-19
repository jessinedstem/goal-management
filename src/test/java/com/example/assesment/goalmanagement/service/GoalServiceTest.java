package com.example.assesment.goalmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class GoalServiceTest {
    @InjectMocks private GoalService goalService;
    @Mock private GoalRepository goalRepository;
    @Mock private MilestoneRepository milestoneRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllGoals() {
        Goal goal1 =
                new Goal(
                        1L,
                        "Goal 1",
                        "Description 1",
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        50.0,
                        Arrays.asList(
                                new Milestone(1L, 1L, "Update 1", LocalDateTime.now(), true),
                                new Milestone(2L, 1L, "Update 2", LocalDateTime.now(), false)));
        Goal goal2 =
                new Goal(
                        2L,
                        "Goal 2",
                        "Description 2",
                        LocalDate.now(),
                        LocalDate.now().plusDays(60),
                        75.0,
                        Arrays.asList(
                                new Milestone(3L, 2L, "Update 3", LocalDateTime.now(), true),
                                new Milestone(4L, 2L, "Update 4", LocalDateTime.now(), true)));

        List<Goal> mockGoals = Arrays.asList(goal1, goal2);
        Page<Goal> mockGoalPage = Page.empty();
        when(goalRepository.findAll(PageRequest.of(0, 5))).thenReturn(mockGoalPage);
        when(goalRepository.findAll(PageRequest.of(1, 5))).thenReturn(Page.empty());
        when(goalRepository.findAll(PageRequest.of(0, 5))).thenReturn(new PageImpl<>(mockGoals));

        List<GoalResponse> goalResponses = goalService.findAllGoals(0, 5);

        assertEquals(2, goalResponses.size());
        assertEquals("Goal 1", goalResponses.get(0).getTitle());
        assertEquals("Goal 2", goalResponses.get(1).getTitle());
    }

    @Test
    public void testMapToGoalResponse() {
        Goal goal =
                new Goal(
                        1L,
                        "Goal 1",
                        "Description 1",
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        50,
                        new ArrayList<>());
        GoalResponse goalResponse = goalService.mapToGoalResponse(goal);
        assertNotNull(goalResponse);
        assertEquals(1L, goalResponse.getId());
        assertEquals("Goal 1", goalResponse.getTitle());
        assertEquals("Description 1", goalResponse.getDescription());
        assertEquals(LocalDate.now(), goalResponse.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), goalResponse.getEndDate());
        assertEquals(0, goalResponse.getMilestones().size());
    }

    @Test
    public void testMapToMilestoneResponse() {
        Milestone milestone = new Milestone(1L, 1L, "Update Text", LocalDateTime.now(), true);

        MilestoneResponse milestoneResponse = goalService.mapToMilestoneResponse(milestone);

        assertEquals(milestone.getId(), milestoneResponse.getId());
        assertEquals(milestone.getGoalId(), milestoneResponse.getGoalId());
        assertEquals(milestone.getUpdateText(), milestoneResponse.getUpdateText());
        assertEquals(milestone.getUpdatedDate(), milestoneResponse.getUpdatedDate());
        assertEquals(milestone.isCompleted(), milestoneResponse.isCompleted());
    }

    @Test
    public void testFindGoalById() {
        long goalId = 1L;
        Goal goal =
                new Goal(
                        goalId,
                        "Test Goal",
                        "Test Description",
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        50.0,
                        Collections.singletonList(
                                new Milestone(1L, goalId, "Update 1", LocalDateTime.now(), true)));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        GoalResponse goalResponse = goalService.findGoalById(goalId);

        assertEquals(goal.getId(), goalResponse.getId());
        assertEquals(goal.getTitle(), goalResponse.getTitle());
        assertEquals(goal.getDescription(), goalResponse.getDescription());
        assertEquals(goal.getStartDate(), goalResponse.getStartDate());
        assertEquals(goal.getEndDate(), goalResponse.getEndDate());
        assertEquals(goal.getCompletedPercentage(), goalResponse.getCompletedPercentage());

        assertEquals(1, goalResponse.getMilestones().size());
        Milestone milestone = goal.getMilestones().get(0);
        MilestoneResponse milestoneResponse = goalResponse.getMilestones().get(0);
        assertEquals(milestone.getId(), milestoneResponse.getId());
        assertEquals(goal.getId(), milestoneResponse.getGoalId());
        assertEquals(milestone.getUpdateText(), milestoneResponse.getUpdateText());
        assertEquals(milestone.getUpdatedDate(), milestoneResponse.getUpdatedDate());
        assertEquals(milestone.isCompleted(), milestoneResponse.isCompleted());
    }

    @Test
    public void testCreateGoal() {
        GoalRequest goalRequest =
                GoalRequest.builder()
                        .title("Test Goal")
                        .description("Test Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .build();

        Goal savedGoal =
                Goal.builder()
                        .id(1L)
                        .title("Test Goal")
                        .description("Test Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .milestones(new ArrayList<>())
                        .build();

        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        GoalResponse goalResponse = goalService.createGoal(goalRequest);

        assertEquals(savedGoal.getId(), goalResponse.getId());
        assertEquals(savedGoal.getTitle(), goalResponse.getTitle());
        assertEquals(savedGoal.getDescription(), goalResponse.getDescription());
        assertEquals(savedGoal.getStartDate(), goalResponse.getStartDate());
        assertEquals(savedGoal.getEndDate(), goalResponse.getEndDate());
        assertEquals(savedGoal.getCompletedPercentage(), goalResponse.getCompletedPercentage());
        assertEquals(new ArrayList<>(), goalResponse.getMilestones());
    }

    @Test
    public void testUpdateGoalById() {
        long goalId = 1L;
        GoalRequest goalRequest =
                GoalRequest.builder()
                        .title("Updated Title")
                        .description("Updated Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .build();
        Goal originalGoal =
                Goal.builder()
                        .id(goalId)
                        .title("Original Title")
                        .description("Original Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .milestones(new ArrayList<>())
                        .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(originalGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(originalGoal);

        String result = goalService.updateGoalById(goalId, goalRequest);
        assertEquals("Successfully updated the goal with ID " + goalId, result);

        verify(goalRepository, times(1)).findById(goalId);
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    public void testDeleteGoalById() {

        Long goalId = 1L;

        when(goalRepository.existsById(goalId)).thenReturn(true);

        goalService.deleteGoalById(goalId);

        verify(goalRepository, times(1)).existsById(goalId);
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void testDeleteGoalByIdGoalNotFound() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);
        assertThrows(
                GoalNotFoundException.class,
                () -> {
                    goalService.deleteGoalById(goalId);
                });
    }

    @Test
    public void testAddMilestoneToAGoal() {
        long goalId = 1L;
        MilestoneRequest milestoneRequest =
                MilestoneRequest.builder().updateText("Test Milestone").completed(false).build();
        Goal existingGoal =
                Goal.builder()
                        .id(goalId)
                        .title("Test Goal")
                        .description("Test Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .milestones(new ArrayList<>())
                        .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        Milestone savedMilestone =
                Milestone.builder()
                        .id(1L)
                        .goalId(goalId)
                        .updateText("Test Milestone")
                        .updatedDate(LocalDateTime.now())
                        .completed(false)
                        .build();

        when(milestoneRepository.save(any(Milestone.class))).thenReturn(savedMilestone);

        MilestoneResponse milestoneResponse =
                goalService.addMilestoneToAGoal(goalId, milestoneRequest);

        verify(goalRepository, times(1)).findById(goalId);
        verify(milestoneRepository, times(1)).save(any(Milestone.class));

        assertEquals(savedMilestone.getId(), milestoneResponse.getId());
        assertEquals(goalId, milestoneResponse.getGoalId());
        assertEquals(savedMilestone.getUpdateText(), milestoneResponse.getUpdateText());
        assertEquals(savedMilestone.getUpdatedDate(), milestoneResponse.getUpdatedDate());
        assertEquals(savedMilestone.isCompleted(), milestoneResponse.isCompleted());
    }

    @Test
    public void testDeleteMilestone() {
        Goal goal =
                new Goal(
                        1L,
                        "Goal 1",
                        "Description 1",
                        LocalDate.now(),
                        LocalDate.now(),
                        50,
                        new ArrayList<>());
        Milestone milestone =
                Milestone.builder()
                        .id(1L)
                        .goalId(1L)
                        .updateText("Update 1")
                        .updatedDate(LocalDateTime.now())
                        .completed(true)
                        .build();

        goal.getMilestones().add(milestone);
        doNothing().when(milestoneRepository).delete(any(Milestone.class));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));

        String result = goalService.deleteMilestone(1L, 1L);
        assertEquals("Successfully deleted the Milestone", result);
        assertEquals(0, goal.getMilestones().size());
        verify(milestoneRepository, times(1)).delete(any(Milestone.class));
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    public void testUpdateMilestone() {
        Goal goal =
                new Goal(
                        1L,
                        "Goal 1",
                        "Description 1",
                        LocalDate.now(),
                        LocalDate.now(),
                        50,
                        new ArrayList<>());
        MilestoneRequest milestoneRequest =
                MilestoneRequest.builder().updateText("Updated Text").completed(false).build();
        LocalDateTime updatedDateTime = LocalDateTime.now();
        Milestone milestone =
                Milestone.builder()
                        .id(1L)
                        .goalId(1L)
                        .updateText("Updated Update Text")
                        .updatedDate(updatedDateTime)
                        .completed(true)
                        .build();
        goal.getMilestones().add(milestone);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(milestoneRepository.findById(anyLong())).thenReturn(Optional.of(milestone));

        MilestoneResponse milestoneResponse =
                MilestoneResponse.builder()
                        .id(1L)
                        .goalId(1L)
                        .updateText("Updated Update Text")
                        .updatedDate(updatedDateTime)
                        .completed(true)
                        .build();
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);
        when(goalService.updateMilestone(1L, 1L, milestoneRequest)).thenReturn(milestoneResponse);
        assertEquals(milestone.getId(), milestoneResponse.getId());
        assertEquals(milestone.getGoalId(), milestoneResponse.getGoalId());
        assertEquals(milestone.getUpdatedDate(), milestoneResponse.getUpdatedDate());
        assertEquals("Updated Update Text", milestoneResponse.getUpdateText());
        verify(goalRepository, times(1)).findById(anyLong());
        verify(milestoneRepository, times(1)).save(any(Milestone.class));
    }

    @Test
    public void testFindGoalById_throwsGoalNotFoundException() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.findGoalById(goalId);
                        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoalById_throwsGoalNotFoundException() {
        long goalId = 1L;
        GoalRequest goalRequest =
                new GoalRequest(
                        "Sample Goal", "Sample Description", LocalDate.now(), LocalDate.now());
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.updateGoalById(goalId, goalRequest);
                        });

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoalById_throwsGoalNotFoundException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.deleteGoalById(goalId);
                        });
        assertEquals("Goal with ID " + goalId + " not found.", exception.getMessage());
    }

    @Test
    public void testAddMilestoneToAGoal_throwsGoalNotFoundException() {
        Long goalId = 1L;
        MilestoneRequest milestoneRequest = new MilestoneRequest("Sample Update", true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.addMilestoneToAGoal(goalId, milestoneRequest);
                        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteMilestone_throwsGoalNotFoundException() {
        Long goalId = 1L;
        Long milestoneId = 2L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.deleteMilestone(goalId, milestoneId);
                        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateMilestone_throwsGoalNotFoundException() {
        Long goalId = 1L;
        Long milestoneId = 2L;
        MilestoneRequest milestoneRequest = new MilestoneRequest("Updated Update", true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        GoalNotFoundException exception =
                assertThrows(
                        GoalNotFoundException.class,
                        () -> {
                            goalService.updateMilestone(goalId, milestoneId, milestoneRequest);
                        });
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateMilestone_throwsMilestoneNotFoundException() {

        Long goalId = 1L;

        Long milestoneId = 2L;

        MilestoneRequest milestoneRequest = new MilestoneRequest("Updated Update", false);
        List<Milestone> milestoneList = new ArrayList<>();

        Milestone milestone1 =
                Milestone.builder()
                        .id(7L)
                        .goalId(5L)
                        .updateText("update")
                        .updatedDate(LocalDateTime.now())
                        .completed(false)
                        .build();
        milestoneList.add(milestone1);

        Goal goal =
                Goal.builder()
                        .id(1L)
                        .title("new-one")
                        .description("goalUpdate")
                        .milestones(milestoneList)
                        .build();

        Milestone milestone =
                goal.getMilestones().stream().filter(g -> g.getId() == 2L).findFirst().orElse(null);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        MilestoneNotFoundException exception =
                assertThrows(
                        MilestoneNotFoundException.class,
                        () -> {
                            goalService.updateMilestone(goalId, milestoneId, milestoneRequest);
                        });

        assertEquals("Milestone not found", exception.getMessage());
    }
}
