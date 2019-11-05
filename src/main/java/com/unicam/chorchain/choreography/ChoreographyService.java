package com.unicam.chorchain.choreography;

import com.unicam.chorchain.PagedResources;
import com.unicam.chorchain.model.Choreography;
import com.unicam.chorchain.model.User;
import com.unicam.chorchain.storage.FileSystemStorageService;
import com.unicam.chorchain.user.UserRepository;
import com.unicam.chorchain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChoreographyService {

    private final ChoreographyRepository repository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FileSystemStorageService fileSystemStorageService;
    private final ChoreographyMapper mapper;


    public PagedResources<ChoreographyDTO> findAll(Pageable pageable) {
        return PagedResources.createResources(repository.findAll(pageable), mapper::toDTO);
    }

    public ChoreographyDTO create(String filename, String description) {
        Choreography choreography = new Choreography();
        choreography.setCreated(LocalDateTime.now());
        User user = userRepository.findByAddress(userService.getLoggedUser().getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));
        choreography.setUser(user);
        choreography.setDescription(description);
        choreography.setName(filename);
        choreography.setRoles(new ArrayList<String>(getChoreographyBpmnPartecipant(filename.concat(".bpmn"))));
        return mapper.toDTO(repository.save(choreography));
    }


    public Collection<String> getChoreographyBpmnPartecipant(String filename) {
        Path filenamePath = fileSystemStorageService.load(filename);
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(filenamePath.toFile());
        Collection<Participant> parti = modelInstance.getModelElementsByType(Participant.class);
        ArrayList<String> participants = new ArrayList<>();
        for (Participant p : parti) {
            participants.add(p.getName());
        }
        return new HashSet<>(participants);
    }

    public ChoreographyDTO read(@Valid String _id) {
        return mapper.toDTO(findChoreography(_id));
    }

    public Choreography findChoreography(String _id) {
        return repository.findById(_id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Choreography " + _id + " was not found in the database",
                        _id)));
    }

    public String delete(@Valid String _id) {
        repository.delete(findChoreography(_id));
        return "Choreography with id " + _id + " has been removed";
    }
}