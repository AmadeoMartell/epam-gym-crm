package com.epam.crm.api;

import com.epam.crm.model.TrainingType;
import com.epam.crm.repository.TrainingTypeRepository;
import com.epam.crm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingTypeControllerTest {

    private MockMvc mvc;
    private TrainingTypeRepository repo;
    private AuthService authService;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(TrainingTypeRepository.class);
        authService = Mockito.mock(AuthService.class);
        mvc = MockMvcBuilders.standaloneSetup(new TrainingTypeController(repo, authService)).build();
        doNothing().when(authService).authenticate(anyString(), anyString());
    }

    @Test
    void getAll_ok() throws Exception {
        TrainingType t1 = new TrainingType(); t1.setId(1L); t1.setName("Cardio");
        TrainingType t2 = new TrainingType(); t2.setId(2L); t2.setName("Strength");
        when(repo.findAll()).thenReturn(List.of(t1, t2));

        mvc.perform(get("/api/training-types")
                        .header("X-Username","auth").header("X-Password","pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", anyOf(is("Cardio"), is("Strength"))));
    }
}
