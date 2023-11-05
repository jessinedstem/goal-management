//package com.example.assesment.goalmanagement.controller;
//
//import com.example.assesment.goalmanagement.contract.GoalRequest;
//import com.example.assesment.goalmanagement.contract.GoalResponse;
//import com.example.assesment.goalmanagement.contract.GoalUpdateRequest;
//import com.example.assesment.goalmanagement.contract.GoalUpdateResponse;
//import com.example.assesment.goalmanagement.service.GoalService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import java.util.ArrayList;
//import java.util.List;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class GoalControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private GoalService goalService;
//
//    @Test
//    public void testGetAllGoals() throws Exception {
//        List<GoalResponse> mockGoalResponses = new ArrayList<>();
//        when(goalService.findAllGoals()).thenReturn(mockGoalResponses);
//
//        mockMvc.perform(get("/goals")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray());
//    }
//
//    @Test
//    public void testFindGoalById() throws Exception {
//        long goalId = 1L;
//        GoalResponse mockGoalResponse = new GoalResponse();
//        when(goalService.findGoalById(goalId)).thenReturn(mockGoalResponse);
//        mockMvc.perform(get("/goals/{goalId}", goalId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(mockGoalResponse.getId()));
//    }
//    @Test
//    public void testCreateAllGoals() throws Exception {
//        GoalRequest goalRequest = new GoalRequest();
//        GoalResponse mockGoalResponse = new GoalResponse();
//        when(goalService.createGoal(goalRequest)).thenReturn(mockGoalResponse);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/goals")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(goalRequest)))
//                .andExpect(status().isCreated());
//    }
//    private static String asJsonString(final Object obj) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    @Test
//    public void testDeleteGoalById() throws Exception {
//        long goalId = 1L;
//        doNothing().when(goalService).deleteGoalById(goalId);
//
//        mockMvc.perform(delete("/goals/{goalId}", goalId))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Goal " + goalId + " deleted."));
//    }
//    @Test
//    public void testAddUpdateTextAndProgressToAGoal() throws Exception {
//        long goalId = 1L;
//        GoalUpdateRequest goalUpdateRequest = new GoalUpdateRequest();
//        GoalUpdateResponse mockUpdateResponse = new GoalUpdateResponse();
//        when(goalService.updateTextAndProgress(goalId, goalUpdateRequest)).thenReturn(mockUpdateResponse);
//
//        mockMvc.perform(post("/goals/{goalId}", goalId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(goalUpdateRequest)))
//                .andExpect(status().isOk());
//    }
//    @Test
//    public void testUpdateGoalById() throws Exception {
//        long goalId = 1L;
//        GoalRequest goalRequest = new GoalRequest();
//        String updatedResponse = "Goal updated successfully";
//        when(goalService.updateGoalById(goalId, goalRequest)).thenReturn(updatedResponse);
//        mockMvc.perform(put("/goals/{goalId}", goalId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(goalRequest)))
//                .andExpect(status().isOk());
//    }
//}
