package com.unicam.chorchain.instance;

import com.unicam.chorchain.choreography.ChoreographyRepository;
import com.unicam.chorchain.mandatoryParticipantAddress.MandatoryParticipantAddressReposistory;
import com.unicam.chorchain.model.*;
import com.unicam.chorchain.participant.ParticipantRepository;
import com.unicam.chorchain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InstanceService {

    private final InstanceRepository repository;
    private final ChoreographyRepository choreographyRepository;
    private final InstanceMapper mapper;
    private final UserService userService;
    private final ParticipantRepository participantRepository;
    private final MandatoryParticipantAddressReposistory mandatoryParticipantAddressReposistory;

    public InstanceDTO create(InstanceRequest instanceRequest) {
        Instance instance = new Instance();

        //Set creation time
        instance.setCreated(LocalDateTime.now());

        //Set Choreography
        Choreography choreography = findChoreographyById(instanceRequest.getModelId());
        instance.setChoreography(choreography);

        //Set user as created by user
        User user = userService.findUserByAddress(userService.getLoggedUser().getUsername());
        instance.setUser(user);

        //Set mandatory participants list
        List<MandatoryParticipantAddress> mandatoryParticipants = new ArrayList<>();
        instanceRequest.getMandatoryParticipants().forEach((role) -> {
            Participant p = participantRepository.findById(role)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Participant " + role + " was not found in the database",
                            role)));
            MandatoryParticipantAddress mandatoryParticipantAddress = new MandatoryParticipantAddress();
            mandatoryParticipantAddress.setParticipant(p);
            mandatoryParticipants.add(mandatoryParticipantAddress);
        });
        instance.setMandatoryParticipants(mandatoryParticipants);


        return mapper.toInstanceDTO(repository.save(instance));
    }


    private Choreography findChoreographyById(Long id) {
        Choreography choreography = choreographyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Choreography not found!"));
        log.debug("Found model {}", id);
        return choreography;
    }

    public List<InstanceDTO> findAllByModelId(Long id) {
        List<InstanceDTO> instanceDTOList = repository.findAllByChoreographyId(findChoreographyById(id).getId())
                .stream()
                .map(mapper::toInstanceDTO)
                .collect(Collectors.toList());
        return instanceDTOList;
    }

    public Instance findInstanceById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Instance " + id + " was not found in the database",
                        id)));
    }

    public MandatoryParticipantAddress findMandatoryParticipantById(Long id) {
        return mandatoryParticipantAddressReposistory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("MandatoryParticipant " + id + " was not found in the database",
                        id)));
    }

    public InstanceDTO read(Long id) {
        return mapper.toInstanceDTO(findInstanceById(id));
    }

    public String delete(@Valid Long id) {
        repository.delete(findInstanceById(id));
        return "Instance with id " + id + " has been removed";
    }

    public void instanceSubscription(InstanceSubscribeRequest instanceSubscribeRequest) {

//        Instance instanceToSubscribe = findInstanceById(instanceId);


        log.debug("instanceSubscribeRequest {}", instanceSubscribeRequest);

//        log.debug("instanceSubscribeRequest {}", instanceId);
        MandatoryParticipantAddress mandatoryParticipantAddress =
                    findMandatoryParticipantById(instanceSubscribeRequest.getMandatoryParticipantAddressId());

        if (mandatoryParticipantAddress.getUser() == null) {
            log.info("Associating an user to this mandatoryParticipant {}",mandatoryParticipantAddress.getParticipant().getName());
            User user = userService.findUserByAddress(instanceSubscribeRequest.getAddress());
            mandatoryParticipantAddress.setUser(user);
        } else {
            //TODO notify that an user associated already exist
            log.info("An user is already associated {}", mandatoryParticipantAddress.getUser().getAddress());
        }

//        int instanceMaxNumber = instanceToSubscribe.getChoreography().getInstances().size();
//        int instanceActualNumber = instanceToSubscribe.getMandatoryParticipants().size();
//
//        if ((instanceActualNumber + 1) <= instanceMaxNumber) {


//            instanceToSubscribe.setActualNumber(instanceActualNumber+1);
//            List<MandatoryParticipantAddress> freeRoles = instanceToSubscribe.getMandatoryParticipants();
//            Predicate<MandatoryParticipantAddress> userNotSet = participantAddress -> participantAddress.getParticipant().getId() == null;
//            List<MandatoryParticipantAddress> s = freeRoles.stream().filter(userNotSet).collect(Collectors.toList());
//            freeRoles.remove(instanceSubscribeRequest.getRole());
//            Map<String, User> subscribers = instanceToSubscribe.getParticipants();
//            User user = userRepository.findByAddress(userService.getLoggedUser().getUsername())
//                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));
//            subscribers.put(instanceSubscribeRequest.getRole(), user);
        mandatoryParticipantAddressReposistory.save(mandatoryParticipantAddress);
        //List<Instance> userInstances = user.getInstances();
        //userInstances.add(instanceToSubscribe);
        //userRepository.save(user);
//        return mapper.toInstanceDTO(instanceToSubscribe);
    }

}
