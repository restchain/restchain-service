package com.unicam.chorchain.choreography;

import com.unicam.chorchain.PagedResources;
import com.unicam.chorchain.model.Choreography;
import com.unicam.chorchain.model.User;
import com.unicam.chorchain.participant.ParticipantRepository;
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
import java.util.Set;

//import com.unicam.chorchain.model.Participant;

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
    private final ParticipantRepository participantRepository;

    public PagedResources<ChoreographyDTO> findAll(Pageable pageable) {
        return PagedResources.createResources(repository.findAll(pageable), mapper::toDTO);
    }

    public ChoreographyDTO create(String filename, String description) {

        Choreography choreography = new Choreography();

        choreography.setCreated(LocalDateTime.now());
        choreography.setDescription(description);
        choreography.setName(filename);

        //Setting user
        User user = userRepository.findByAddress(userService.getLoggedUser().getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));
        choreography.setUploadedBy(user);


        //Retrieves participants from the bpmn and add them to the Participant entity
        Collection<String> participantNames = getChoreographyBpmnPartecipant(filename.concat(".bpmn"));
        Set<com.unicam.chorchain.model.Participant> participants = choreography.getParticipants();
        participantNames.forEach(
                (p) -> {
                    com.unicam.chorchain.model.Participant participant = new com.unicam.chorchain.model.Participant(p);
                    participantRepository.save(participant);
                    participants.add(participant);
                }
        );
        choreography.setParticipants(participants);

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

    public ChoreographyDTO read(@Valid Long id) {
        return mapper.toDTO(findChoreography(id));
    }

    public Choreography findChoreography(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Choreography " + id + " was not found in the database",
                        id)));
    }

    public String delete(@Valid Long id) {
        repository.delete(findChoreography(id));
        return "Choreography with id " + id + " has been removed";
    }
}