package com.example.assesment.goalmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class GoalServiceTest {
//    @InjectMocks private GoalService goalService;
//    @Mock private GoalRepository goalRepository;
//    @Mock private MilestoneRepository milestoneRepository;
//    @Mock private ModelMapper modelMapper;
private GoalRepository goalRepository;
private MilestoneRepository milestoneRepository;
private ModelMapper modelMapper;
private GoalService goalService;
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        goalRepository=mock(GoalRepository.class);
        milestoneRepository=mock(MilestoneRepository.class);
        modelMapper=mock(ModelMapper.class);
        goalService=new GoalService(goalRepository,milestoneRepository,modelMapper);

    }

    @Test
    public void testFindAllGoals() {
        List<Goal> mockGoals =
                Arrays.asList(
                        new Goal(1L, "Goal 1", "Description 1", null, null, 0, new ArrayList<>()),
                        new Goal(2L, "Goal 2", "Description 2", null, null, 0, new ArrayList<>()));

        when(goalRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(mockGoals));

        when(modelMapper.map(any(Goal.class), eq(GoalResponse.class)))
                .thenAnswer(
                        invocation -> {
                            Goal goal = invocation.getArgument(0);
                            return GoalResponse.builder()
                                    .id(goal.getId())
                                    .title(goal.getTitle())
                                    .description(goal.getDescription())
                                    .startDate(goal.getStartDate())
                                    .endDate(goal.getEndDate())
                                    .completedPercentage(goal.getCompletedPercentage())
                                    .milestones(new ArrayList<>())
                                    .build();
                        });

        List<GoalResponse> goalResponses = goalService.findAllGoals(0, 2);

        System.out.println(goalResponses.toString());
        assertEquals(mockGoals.size(), goalResponses.size());
        assertEquals("Goal 1", goalResponses.get(0).getTitle());
        assertEquals("Goal 2", goalResponses.get(1).getTitle());

        verify(goalRepository, times(1)).findAll(any(PageRequest.class));
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
        GoalResponse expectedGoalResponse =
                new GoalResponse(
                        goalId,
                        "Test Goal",
                        "Test Description",
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        50.0,
                        Collections.emptyList());

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(modelMapper.map(goal, GoalResponse.class)).thenReturn(expectedGoalResponse);

        GoalResponse actualGoalResponse = goalService.findGoalById(goalId);

        assertNotNull(actualGoalResponse);
        assertEquals(expectedGoalResponse, actualGoalResponse);
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

        Goal sampleGoal =
                Goal.builder()
                        .id(1L)
                        .title("Test Goal")
                        .description("Test Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .milestones(new ArrayList<>())
                        .build();
        GoalResponse expectedGoalResponse =
                new GoalResponse(
                        1L,
                        "Test Goal",
                        "Test Description",
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        0.0,
                        Collections.emptyList());

        when(goalRepository.save(any(Goal.class))).thenReturn(sampleGoal);
        when(modelMapper.map(sampleGoal, GoalResponse.class)).thenReturn(expectedGoalResponse);
        GoalResponse actualGoalResponse = goalService.createGoal(goalRequest);
        assertNotNull(actualGoalResponse);
        assertEquals(expectedGoalResponse, actualGoalResponse);
        verify(goalRepository, times(1))
                .save(
                        argThat(
                                argument ->
                                        "Test Goal".equals(argument.getTitle())
                                                && "Test Description"
                                                        .equals(argument.getDescription())));
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

        Goal existingGoal =
                Goal.builder()
                        .id(goalId)
                        .title("Updated Title")
                        .description("Updated Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .completedPercentage(50.0)
                        .build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = goalService.updateGoalById(goalId, goalRequest);

        verify(goalRepository, times(1)).findById(goalId);
        verify(goalRepository, times(1)).save(any(Goal.class));

        assertEquals("Successfully updated the goal with ID " + goalId, result);

        assertEquals(goalRequest.getTitle(), existingGoal.getTitle());
        assertEquals(goalRequest.getDescription(), existingGoal.getDescription());
        assertEquals(goalRequest.getStartDate(), existingGoal.getStartDate());
        assertEquals(goalRequest.getEndDate(), existingGoal.getEndDate());
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
        Milestone milestoneSaved =
                Milestone.builder()
                        .id(1L)
                        .goalId(goalId)
                        .updateText(milestoneRequest.getUpdateText())
                        .updatedDate(LocalDateTime.now())
                        .completed(milestoneRequest.getCompleted())
                        .build();
        MilestoneResponse savedResponse =
                MilestoneResponse.builder()
                        .id(1L)
                        .goalId(milestoneSaved.getGoalId())
                        .updateText(milestoneSaved.getUpdateText())
                        .updatedDate(milestoneSaved.getUpdatedDate())
                        .completed(milestoneSaved.getCompleted())
                        .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestoneSaved);
        when(goalService.addMilestoneToAGoal(goalId, milestoneRequest)).thenReturn(savedResponse);
        when(modelMapper.map(milestoneSaved, MilestoneResponse.class)).thenReturn(savedResponse);
        MilestoneResponse result = goalService.addMilestoneToAGoal(goalId, milestoneRequest);

        verify(goalRepository, times(2)).findById(goalId);
        verify(milestoneRepository, times(2)).save(any(Milestone.class));
        verify(goalRepository, times(2)).save(existingGoal);

        assertNotNull(result);
        assertEquals(milestoneRequest.getUpdateText(), result.getUpdateText());
    }

    @Test
    public void testDeleteMilestone() {

        long goalId = 1L;
        long milestoneId = 1L;

        Milestone milestoneToDelete =
                Milestone.builder()
                        .id(milestoneId)
                        .goalId(goalId)
                        .updateText("Test Milestone")
                        .completed(false)
                        .build();

        Goal existingGoal =
                Goal.builder()
                        .id(goalId)
                        .title("Test Goal")
                        .description("Test Goal Description")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .completedPercentage(0.0)
                        .milestones(new ArrayList<>(List.of(milestoneToDelete)))
                        .build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(milestoneRepository.findById(milestoneId)).thenReturn(Optional.of(milestoneToDelete));

        goalService.deleteMilestone(goalId, milestoneId);

        verify(goalRepository, times(1)).findById(goalId);
        verify(milestoneRepository, times(1)).delete(milestoneToDelete);
        verify(goalRepository, times(1)).save(existingGoal);

        assertEquals(0, existingGoal.getMilestones().size());
    }

    @Test
    public void testUpdateMilestone() {
        long goalId = 1L;
        long milestoneId = 1L;

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

        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

        when(modelMapper.map(any(), eq(MilestoneResponse.class)))
                .thenReturn(
                        MilestoneResponse.builder()
                                .id(milestone.getId())
                                .goalId(milestone.getGoalId())
                                .updatedDate(milestone.getUpdatedDate())
                                .updateText(milestone.getUpdateText())
                                .completed(milestone.getCompleted())
                                .build());

        MilestoneResponse response =
                goalService.updateMilestone(goalId, milestoneId, milestoneRequest);

        assertNotNull(response);
        assertEquals(milestone.getId(), response.getId());
        assertEquals(milestone.getGoalId(), response.getGoalId());
        assertEquals(milestone.getUpdatedDate(), response.getUpdatedDate());
        assertEquals("Updated Update Text", response.getUpdateText());

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
