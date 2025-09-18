package com.epam.crm.api;

import com.epam.crm.api.dto.trainer.UpdateTrainerRequest;
import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainerControllerTest {

    private MockMvc mvc;
    private TrainerService trainerService;
    private AuthService authService;

    private Trainer makeTrainer(String u, String f, String l, String spec, boolean active) {
        Trainer tr = new Trainer();
        tr.setUsername(u);
        tr.setFirstName(f);
        tr.setLastName(l);
        tr.setSpecialization(spec);
        tr.setIsActive(active);
        return tr;
    }

    private Trainee makeTrainee(String u, String f, String l) {
        Trainee t = new Trainee();
        t.setUsername(u);
        t.setFirstName(f);
        t.setLastName(l);
        return t;
    }

    @BeforeEach
    void setup() {
        trainerService = Mockito.mock(TrainerService.class);
        authService = Mockito.mock(AuthService.class);
        mvc = MockMvcBuilders.standaloneSetup(new TrainerController(trainerService, authService)).build();
        doNothing().when(authService).authenticate(anyString(), anyString());
    }

    @Test
    void getProfile_ok() throws Exception {
        Trainer tr = makeTrainer("coach.anna","Anna","Lee","Cardio", true);
        tr.setTrainees(Set.of(makeTrainee("john.smith","John","Smith")));
        when(trainerService.findByUsernameOrThrow("coach.anna")).thenReturn(tr);

        mvc.perform(get("/api/trainers/profile")
                        .param("username","coach.anna")
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Anna")))
                .andExpect(jsonPath("$.specialization", is("Cardio")))
                .andExpect(jsonPath("$.trainees[0].username", is("john.smith")));
    }

    @Test
    void updateProfile_ok_and_toggleActivation() throws Exception {
        Trainer before = makeTrainer("coach.anna","Anna","Lee","Cardio", false);
        when(trainerService.findByUsernameOrThrow("coach.anna")).thenReturn(before);
        when(trainerService.updateProfile(eq("coach.anna"), anyString(), anyString(), eq("Cardio")))
                .thenReturn(before);

        Trainer after = makeTrainer("coach.anna","Annabelle","Lee","Cardio", true);
        when(trainerService.findByUsernameOrThrow("coach.anna")).thenReturn(after);

        String body = """
    {"username":"coach.anna","firstName":"Annabelle","lastName":"Lee","isActive":true}
    """;

        mvc.perform(put("/api/trainers/profile")
                        .contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Annabelle")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.specialization", is("Cardio")));

        verify(trainerService).activate("coach.anna");
        verify(trainerService).updateProfile("coach.anna","Annabelle","Lee","Cardio");
    }


    @Test
    void activation_patch_ok() throws Exception {
        doNothing().when(trainerService).activate("coach.anna");

        String body = """
        {"username":"coach.anna","isActive":true}
        """;

        mvc.perform(patch("/api/trainers/activation")
                        .contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk());

        verify(trainerService).activate("coach.anna");
    }
}
