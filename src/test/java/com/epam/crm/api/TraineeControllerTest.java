package com.epam.crm.api;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TraineeControllerTest {

    private MockMvc mvc;
    private TraineeService traineeService;
    private AuthService authService;

    private Trainee makeTrainee(String u, String f, String l, boolean active) {
        Trainee t = new Trainee();
        t.setUsername(u);
        t.setFirstName(f);
        t.setLastName(l);
        t.setIsActive(active);
        return t;
    }

    private Trainer makeTrainer(String u, String f, String l, String spec, boolean active) {
        Trainer tr = new Trainer();
        tr.setUsername(u);
        tr.setFirstName(f);
        tr.setLastName(l);
        tr.setSpecialization(spec);
        tr.setIsActive(active);
        return tr;
    }

    @BeforeEach
    void setup() {
        traineeService = Mockito.mock(TraineeService.class);
        authService = Mockito.mock(AuthService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService, authService))
                .setControllerAdvice(new com.epam.crm.api.error.GlobalExceptionHandler())
                .build();
        doNothing().when(authService).authenticate(anyString(), anyString());
    }

    @Test
    void getProfile_ok() throws Exception {
        Trainee t = makeTrainee("john.smith","John","Smith", true);
        Trainer tr1 = makeTrainer("coach.anna","Anna","Lee","Cardio", true);
        Trainer tr2 = makeTrainer("coach.bob","Bob","Ray","Strength", false);
        t.setDateOfBirth(LocalDate.of(2000,1,2));
        t.setAddress("Addr");
        t.setTrainers(Set.of(tr1, tr2));

        when(traineeService.findByUsername("john.smith")).thenReturn(Optional.of(t));

        mvc.perform(get("/api/trainees/profile")
                        .param("username","john.smith")
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.trainers", hasSize(2)))
                .andExpect(jsonPath("$.trainers[0].username", anyOf(is("coach.anna"), is("coach.bob"))));
    }

    @Test
    void updateProfile_ok_and_toggleActivation() throws Exception {
        Trainee current = makeTrainee("john.smith","John","Smith", false);
        current.setDateOfBirth(LocalDate.of(2000,1,2));
        current.setAddress("Old");
        when(traineeService.updateProfile(eq("john.smith"), anyString(), anyString(), any(), any()))
                .thenReturn(current);

        Trainee after = makeTrainee("john.smith","John","Smith", true);
        when(traineeService.findByUsername("john.smith")).thenReturn(Optional.of(after));

        String body = """
        {"username":"john.smith","firstName":"John","lastName":"Smith","dateOfBirth":"2000-01-02","address":"New","isActive":true}
        """;

        mvc.perform(put("/api/trainees/profile")
                        .contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(true)));

        verify(traineeService).activate("john.smith");
    }

    @Test
    void deleteProfile_ok() throws Exception {
        doNothing().when(traineeService).deleteByUsername("john.smith");

        mvc.perform(delete("/api/trainees/profile")
                        .param("username","john.smith")
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk());

        verify(traineeService).deleteByUsername("john.smith");
    }

    @Test
    void getUnassignedActiveTrainers_ok() throws Exception {
        var active = makeTrainer("coach.anna","Anna","Lee","Cardio", true);
        var inactive = makeTrainer("coach.bob","Bob","Ray","Strength", false);
        when(traineeService.getUnassignedTrainersForTrainee("john.smith"))
                .thenReturn(List.of(active, inactive)); // контроллер сам отфильтрует по isActive=true

        mvc.perform(get("/api/trainees/unassigned-trainers")
                        .param("username","john.smith")
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("coach.anna")))
                .andExpect(jsonPath("$[0].specialization", is("Cardio")));
    }

    @Test
    void updateTraineeTrainers_ok() throws Exception {
        Trainee t = makeTrainee("john.smith","John","Smith", true);
        Trainer tr = makeTrainer("coach.anna","Anna","Lee","Cardio", true);
        t.setTrainers(Set.of(tr));

        doNothing().when(traineeService).updateTraineeTrainers(eq("john.smith"), eq(List.of("coach.anna")));
        when(traineeService.findByUsername("john.smith")).thenReturn(Optional.of(t));

        String body = """
        {"traineeUsername":"john.smith","trainers":["coach.anna"]}
        """;

        mvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", is("coach.anna")))
                .andExpect(jsonPath("$[0].firstName", is("Anna")));
    }

    @Test
    void activation_conflict_409() throws Exception {
        doThrow(new IllegalStateException("already active"))
                .when(traineeService).activate("john.smith");

        String body = """
      {"username":"john.smith","isActive":true}
    """;

        mvc.perform(patch("/api/trainees/activation")
                        .header("X-Username","auth").header("X-Password","pass")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }
}
