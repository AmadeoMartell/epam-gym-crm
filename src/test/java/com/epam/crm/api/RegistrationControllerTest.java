package com.epam.crm.api;

import com.epam.crm.service.TraineeService;
import com.epam.crm.service.TrainerService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RegistrationControllerTest {

    private MockMvc mvc;
    private TraineeService traineeService;
    private TrainerService trainerService;

    @BeforeEach
    void setup() {
        traineeService = Mockito.mock(TraineeService.class);
        trainerService = Mockito.mock(TrainerService.class);

        var controller = new RegistrationController(traineeService, trainerService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registerTrainee_ok() throws Exception {
        var acc = new TraineeService.CreatedAccount(1L, "John.Smith", "secret");
        given(traineeService.createTrainee(any(), any(), any(), any())).willReturn(acc);

        String body = """
          {"firstName":"John","lastName":"Smith","dateOfBirth":"2000-01-02","address":"Addr"}
        """;

        mvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("John.Smith")))
                .andExpect(jsonPath("$.password", is("secret")));
    }

    @Test
    void registerTrainer_ok() throws Exception {
        var acc = new TrainerService.CreatedAccount(2L, "Jane.Doe", "pw");
        given(trainerService.createTrainer(any(), any(), any())).willReturn(acc);

        String body = """
          {"firstName":"Jane","lastName":"Doe","specialization":"Cardio"}
        """;

        mvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("Jane.Doe")))
                .andExpect(jsonPath("$.password", is("pw")));
    }
}
