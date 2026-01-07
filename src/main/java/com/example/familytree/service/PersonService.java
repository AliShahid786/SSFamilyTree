package com.example.familytree.service;

import com.example.familytree.dto.PersonRequest;
import com.example.familytree.dto.PersonResponse;

import java.util.List;

public interface PersonService {
    PersonResponse create(PersonRequest request);
    PersonResponse update(Long id, PersonRequest request);
    PersonResponse getById(Long id);
    List<PersonResponse> searchByName(String name);
    void delete(Long id);
}

