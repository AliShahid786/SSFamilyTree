package com.example.familytree.service;

import com.example.familytree.dto.PersonRequest;
import com.example.familytree.dto.PersonResponse;
import com.example.familytree.model.Person;
import com.example.familytree.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonResponse create(PersonRequest request) {
        Person person = new Person();
        applyRequestToEntity(person, request);
        Person saved = personRepository.save(person);
        return toResponse(saved);
    }

    @Override
    public PersonResponse update(Long id, PersonRequest request) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));
        applyRequestToEntity(existing, request);
        Person saved = personRepository.save(existing);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonResponse getById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));
        return toResponse(person);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonResponse> searchByName(String name) {
        List<Person> persons;
        if (name == null || name.isBlank()) {
            persons = personRepository.findAll();
        } else {
            persons = personRepository.findByFirstNameContainingIgnoreCase(name);
        }
        return persons.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Person not found: " + id);
        }
        personRepository.deleteById(id);
    }

    private void applyRequestToEntity(Person person, PersonRequest request) {
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setGender(request.getGender());
        person.setDateOfBirth(request.getDateOfBirth());

        if (request.getFatherId() != null) {
            person.setFather(personRepository.getReferenceById(request.getFatherId()));
        } else {
            person.setFather(null);
        }

        if (request.getMotherId() != null) {
            person.setMother(personRepository.getReferenceById(request.getMotherId()));
        } else {
            person.setMother(null);
        }
    }

    private PersonResponse toResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setFirstName(person.getFirstName());
        response.setLastName(person.getLastName());
        response.setGender(person.getGender());
        response.setDateOfBirth(person.getDateOfBirth());
        response.setFatherId(person.getFather() != null ? person.getFather().getId() : null);
        response.setMotherId(person.getMother() != null ? person.getMother().getId() : null);
        return response;
    }
}

