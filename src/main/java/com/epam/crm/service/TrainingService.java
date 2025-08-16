package com.epam.crm.service;

import com.epam.crm.dao.TrainingDao;
import com.epam.crm.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingDao dao;

    public Training create(Training t) {
        return dao.create(t);
    }

    public Optional<Training> get(Long id) {
        return dao.findById(id);
    }

    public List<Training> list() {
        return dao.findAll();
    }
}
