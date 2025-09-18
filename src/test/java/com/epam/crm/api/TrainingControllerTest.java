package com.epam.crm.api;

import com.epam.crm.api.error.GlobalExceptionHandler;
import com.epam.crm.model.*;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TrainingService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingControllerTest {

    private MockMvc mvc;
    private TrainingService trainingService;
    private AuthService authService;

    @BeforeEach
    void setup() {
        trainingService = Mockito.mock(TrainingService.class);
        authService = Mockito.mock(AuthService.class);
        var controller = new TrainingController(trainingService, authService);

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTraineeTrainings_ok() throws Exception {
        doNothing().when(authService).authenticate("u","p");

        TrainingType type = new TrainingType(); type.setName("Cardio");
        Trainer trainer = new Trainer(); trainer.setFirstName("Jane"); trainer.setLastName("Doe");
        Training tr = new Training();
        tr.setName("Run");
        tr.setDate(LocalDate.of(2024,1,2));
        tr.setDuration(45);
        tr.setTrainingType(type);
        tr.setTrainer(trainer);

        given(trainingService.findForTraineeWithCriteria(eq("trainee1"), any(), any(), any(), any()))
                .willReturn(List.of(tr));

        mvc.perform(get("/api/trainees/trainings")
                        .param("username", "trainee1")
                        .header("X-Username","u").header("X-Password","p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName", is("Run")))
                .andExpect(jsonPath("$[0].trainingType", is("Cardio")))
                .andExpect(jsonPath("$[0].trainerName", is("Jane Doe")))
                .andExpect(jsonPath("$[0].trainingDuration", is(45)));
    }

    @Test
    void addTraining_ok() throws Exception {
        doNothing().when(authService).authenticate("u","p");

        String body = """
          {
            "traineeUsername": "trainee1",
            "trainerUsername": "trainer1",
            "trainingTypeName": "Cardio",
            "trainingName": "Run",
            "trainingDate": "2024-01-02",
            "trainingDuration": 45
          }
        """;

        mvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Username","u").header("X-Password","p"))
                .andExpect(status().isOk());
    }

    @Test
    void addTraining_badDuration_400() throws Exception {
        Mockito.doNothing().when(authService).authenticate("u","p");

        String body = """
      {
        "traineeUsername":"t1",
        "trainerUsername":"r1",
        "trainingTypeName":"Cardio",
        "trainingName":"Run",
        "trainingDate":"2024-01-02",
        "trainingDuration": 0
      }
    """;

        mvc.perform(post("/api/trainings")
                        .header("X-Username","u").header("X-Password","p")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTraining_notFound_404() throws Exception {
        Mockito.doNothing().when(authService).authenticate("u","p");
        Mockito.doThrow(new java.util.NoSuchElementException("Trainee not found"))
                .when(trainingService).addTraining(Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyInt());

        String body = """
      {
        "traineeUsername":"missing",
        "trainerUsername":"r1",
        "trainingTypeName":"Cardio",
        "trainingName":"Run",
        "trainingDate":"2024-01-02",
        "trainingDuration": 30
      }
    """;

        mvc.perform(post("/api/trainings")
                        .header("X-Username","u").header("X-Password","p")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

}
