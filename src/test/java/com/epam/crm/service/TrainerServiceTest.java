package com.epam.crm.service;

import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    TrainerRepository trainerRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    TrainerService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_persistsAndReturnsCreds() {
        when(userRepository.findAll()).thenReturn(List.of());
        Trainer saved = new Trainer();
        saved.setId(100L);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setUsername("john.doe");
        saved.setPassword("pass");
        saved.setIsActive(true);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(saved);

        var res = service.createTrainer("John", "Doe", "Yoga");
        assertThat(res.getUserId()).isEqualTo(100L);
        assertThat(res.getUsername()).isNotBlank();
        assertThat(res.getPassword()).isNotBlank();
    }

    @Test
    void activate_deactivate_respectsState() {
        User u = new User(1L, "A", "B", "ab", "p", false);
        when(userRepository.findByUsername("ab")).thenReturn(Optional.of(u));

        service.activate("ab");
        assertThat(u.getIsActive()).isTrue();

        assertThatThrownBy(() -> service.activate("ab"))
                .isInstanceOf(IllegalStateException.class);

        service.deactivate("ab");
        assertThat(u.getIsActive()).isFalse();

        assertThatThrownBy(() -> service.deactivate("ab"))
                .isInstanceOf(IllegalStateException.class);
    }
}
