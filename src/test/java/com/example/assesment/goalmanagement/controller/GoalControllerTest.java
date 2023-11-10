package com.example.assesment.goalmanagement.controller;

import com.example.assesment.goalmanagement.contract.GoalUpdateRequest;
import com.example.assesment.goalmanagement.contract.GoalUpdateResponse;
import com.example.assesment.goalmanagement.contract.GoalProgressResponse;
import com.example.assesment.goalmanagement.contract.GoalResponse;
import com.example.assesment.goalmanagement.contract.GoalRequest;
import com.example.assesment.goalmanagement.model.Goal;
import com.example.assesment.goalmanagement.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GoalService goalService;

    @Test
    public void testGetAllGoals() throws Exception {
        List<GoalResponse> mockGoalResponses = new ArrayList<>();
        when(goalService.findAllGoals(0, 5)).thenReturn(mockGoalResponses);

        mockMvc.perform(get("/goals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testFindGoalById() throws Exception {
        long goalId = 1L;
        GoalResponse mockGoalResponse = new GoalResponse();
        when(goalService.findGoalById(goalId)).thenReturn(mockGoalResponse);
        mockMvc.perform(get("/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockGoalResponse.getId()));
    }
    @Test
    public void testCreateAGoal() throws Exception {
        GoalRequest goalRequest = GoalRequest.builder()
                .title("Sample Goal")
                .description("Sample Description")
                .build();
        GoalResponse mockResponse = GoalResponse.builder()
                .id(1L)
                .title("Sample Goal")
                .description("Sample Description")
                .build();
        when(goalService.createGoal(goalRequest)).thenReturn(mockResponse);
        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalRequest)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testUpdateGoalById() throws Exception {
        long goalId = 1L;
        GoalRequest mockGoalRequest = GoalRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();
        Goal updated=Goal.builder()
                        .id(1L)
                                .title("Updated Title")
                .description("Updated Description")
                .build();
        when(goalService.updateGoalById(goalId, mockGoalRequest)).thenReturn("Successfully updated the goal with ID " +updated.getId());
        mockMvc.perform(put("/goals/{goalId}", goalId)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(mockGoalRequest)))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddGoalUpdateToAGoal() throws Exception {
        long goalId = 1L;
        GoalUpdateRequest goalUpdateRequest = GoalUpdateRequest.builder()
                .updateText("New Update")
                .progress(50)
                .build();
        GoalUpdateResponse mockResponse = GoalUpdateResponse.builder()
                .id(1L)
                .updateText(goalUpdateRequest.getUpdateText())
                .progress(goalUpdateRequest.getProgress())
                .build();

        when(goalService.addGoalUpdateToAGoal(goalId, goalUpdateRequest)).thenReturn(mockResponse);

        mockMvc.perform(post("/goals/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockResponse)))
                .andExpect(status().isOk());
    }
    @Test
    public void testUpdateGoalUpdate() throws Exception {
        // Create sample input data
        long goalId = 1L;
        long goalUpdateId = 2L;
        GoalUpdateRequest request = new GoalUpdateRequest("Updated Update", 75);
        GoalUpdateResponse mockResponse = GoalUpdateResponse.builder()
                                                 .updateText("update")
                                        .progress(60)
                                                .build();
        when(goalService.updateGoalUpdate(goalId, goalUpdateId, request)).thenReturn(mockResponse);
                mockMvc.perform(put("/goals/{goalId}/{goalUpdateId}", goalId, goalUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(mockResponse)))
                        .andExpect(status().isOk());

    }
    @Test
    public void testDeleteGoalById() throws Exception {
        long goalId = 1L;
        doNothing().when(goalService).deleteGoalById(goalId);

        mockMvc.perform(delete("/goals/{goalId}", goalId))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal " + goalId + " deleted."));
    }
    @Test
    public void testDeleteGoalUpdate() throws Exception {
        long goalId = 1L;
        long goalUpdateId = 2L;
        String mockResponse = "Successfully deleted the GoalUpdate";

        when(goalService.deleteGoalUpdate(goalId, goalUpdateId)).thenReturn(mockResponse);

        mockMvc.perform(delete("/goals/{goalId}/goal-updates/{goalUpdateId}", goalId, goalUpdateId))
                .andExpect(status().isOk());
        verify(goalService).deleteGoalUpdate(goalId, goalUpdateId);
    }
    @Test
    public void testViewGoalProgress() throws Exception {
        long goalId = 1L;
        GoalProgressResponse expectedProgressResponse = GoalProgressResponse.builder()
                .progress(50)
                .build();

        when(goalService.getGoalProgressById(goalId)).thenReturn(expectedProgressResponse);

        mockMvc.perform(get("/goals/progress/{goalId}", goalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(expectedProgressResponse.getProgress()));
    }
}