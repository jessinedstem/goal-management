package com.example.assesment.goalmanagement.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.MilestoneRequest;
import com.example.assesment.goalmanagement.contract.MilestoneResponse;
import com.example.assesment.goalmanagement.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private GoalService goalService;

    @Test
    public void testGetAllGoals() throws Exception {
        List<GoalResponse> mockGoalResponses = new ArrayList<>();
        when(goalService.findAllGoals(0, 5)).thenReturn(mockGoalResponses);

        mockMvc.perform(
                        get("/goals")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testFindGoalById() throws Exception {
        long goalId = 1L;
        GoalResponse mockGoalResponse = new GoalResponse();
        when(goalService.findGoalById(goalId)).thenReturn(mockGoalResponse);
        MockMvc.perform(get("/goals/{goalId}", goalId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockGoalResponse.getId()));
    }

    @Test
    public void testCreateAGoal() throws Exception {
        GoalRequest goalRequest =
                GoalRequest.builder()
                        .title("Test Goal")
                        .description("Test Goal Description")
                        .startDate(LocalDate.of(2023, 12, 1))
                        .endDate(LocalDate.of(2023, 12, 31))
                        .build();
        GoalResponse mockResponse =
                GoalResponse.builder()
                        .id(1L)
                        .title("Test Goal")
                        .description("Test Goal Description")
                        .startDate(LocalDate.of(2023, 12, 1))
                        .endDate(LocalDate.of(2023, 12, 31))
                        .completedPercentage(60.0)
                        .build();
        when(goalService.createGoal(goalRequest)).thenReturn(mockResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(
                        post("/goals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(goalRequest)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testUpdateGoalById() throws Exception {
        long goalId = 2L;
        GoalRequest goalRequest =
                GoalRequest.builder()
                        .title("Updated Goal")
                        .description("Updated Goal Description")
                        .startDate(LocalDate.of(2023, 12, 1))
                        .endDate(LocalDate.of(2023, 12, 31))
                        .build();

        GoalResponse.builder()
                .id(goalId)
                .title(goalRequest.getTitle())
                .description(goalRequest.getDescription())
                .startDate(goalRequest.getStartDate())
                .endDate(goalRequest.getEndDate())
                .completedPercentage(60.0)
                .build();
        when(goalService.updateGoalById(goalId, goalRequest))
                .thenReturn("Successfully updated the goal");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(
                        put("/goals/" + goalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(goalRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testMilestoneToAGoal() throws Exception {
        long goalId = 1L;
        MilestoneRequest milestoneRequest =
                MilestoneRequest.builder().updateText("New Update").completed(true).build();

        MilestoneResponse mockResponse =
                MilestoneResponse.builder()
                        .id(1L)
                        .goalId(goalId)
                        .updateText(milestoneRequest.getUpdateText())
                        .updatedDate(LocalDateTime.now())
                        .completed(true)
                        .build();

        when(goalService.addMilestoneToAGoal(goalId, milestoneRequest)).thenReturn(mockResponse);

        mockMvc.perform(
                        post("/goals/{goalId}", goalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(milestoneRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteGoalById() throws Exception {
        long goalId = 1L;
        mockMvc.perform(delete("/goals/{goalId}", goalId)).andExpect(status().isNoContent());
        verify(goalService).deleteGoalById(goalId);
    }

    @Test
    public void testDeleteMilestone() throws Exception {
        long goalId = 1L;
        long milestoneId = 101L;
        mockMvc.perform(delete("/goals/{goalId}/milestone/{milestoneId}", goalId, milestoneId))
                .andExpect(status().isNoContent());

        verify(goalService).deleteMilestone(goalId, milestoneId);
    }

    @Test
    public void testUpdateMilestone() throws Exception {
        long goalId = 1L;
        long milestoneId = 2L;

        MilestoneRequest request =
                MilestoneRequest.builder()
                        .updateText("Updated update text")
                        .completed(true)
                        .build();

        MilestoneResponse mockResponse =
                MilestoneResponse.builder()
                        .id(milestoneId)
                        .goalId(goalId)
                        .updatedDate(LocalDateTime.now())
                        .updateText(request.getUpdateText())
                        .completed(request.getCompleted())
                        .build();

        when(goalService.updateMilestone(goalId, milestoneId, request)).thenReturn(mockResponse);

        mockMvc.perform(
                        put("/goals/{goalId}/{milestoneId}", goalId, milestoneId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
