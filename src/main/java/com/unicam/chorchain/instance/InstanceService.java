package com.unicam.chorchain.instance;

import com.unicam.chorchain.choreography.ChoreographyRepository;
import com.unicam.chorchain.instanceParticipantUser.InstanceParticipantUserRepository;
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
    private final InstanceParticipantUserRepository instanceParticipantUserRepository;

    public InstanceDTO create(InstanceRequest instanceRequest) {

        //Set Choreography
        Choreography choreography = findChoreographyById(instanceRequest.getModelId());
        //Set user as created by user
        User user = userService.findUserByAddress(userService.getLoggedUser().getUsername());

        Instance instance = new Instance(choreography, LocalDateTime.now(), user);

        //Set mandatory participants list
        instanceRequest.getMandatoryParticipants().forEach((role) -> {

            Participant p = participantRepository.findById(role)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Participant " + role + " was not found in the database",
                            role)));

            InstanceParticipantUser instanceParticipantUser = new InstanceParticipantUser(instance, p);

            instanceParticipantUserRepository.save(instanceParticipantUser);

        });


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

    public InstanceParticipantUser findInstanceParticipantUserById(Long id) {
        return instanceParticipantUserRepository.findById(id)
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

        log.debug("instanceSubscribeRequest {}", instanceSubscribeRequest);

        InstanceParticipantUser instanceParticipantUser =
                findInstanceParticipantUserById(instanceSubscribeRequest.getInstanceParticipantUserId());

        if (instanceParticipantUser.getUser() == null) {
            log.info("Associating an user to this mandatoryParticipant {}",
                    instanceParticipantUser.getParticipant().getName());
            User user = userService.findUserByAddress(instanceSubscribeRequest.getAddress());
            instanceParticipantUser.setUser(user);
        } else {
            //TODO notify that an user associated already exist
            log.info("An user is already associated {}", instanceParticipantUser.getUser().getAddress());
        }

//        int instanceMaxNumber = instanceToSubscribe.getChoreography().getInstances().size();
//        int instanceActualNumber = instanceToSubscribe.getMandatoryParticipants().size();
//
//        if ((instanceActualNumber + 1) <= instanceMaxNumber) {


//            instanceToSubscribe.setActualNumber(instanceActualNumber+1);
//            List<InstanceParticipantUser> freeRoles = instanceToSubscribe.getMandatoryParticipants();
//            Predicate<InstanceParticipantUser> userNotSet = participantAddress -> participantAddress.getParticipant().getId() == null;
//            List<InstanceParticipantUser> s = freeRoles.stream().filter(userNotSet).collect(Collectors.toList());
//            freeRoles.remove(instanceSubscribeRequest.getRole());
//            Map<String, User> subscribers = instanceToSubscribe.getParticipants();
//            User user = userRepository.findByAddress(userService.getLoggedUser().getUsername())
//                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));
//            subscribers.put(instanceSubscribeRequest.getRole(), user);
        instanceParticipantUserRepository.save(instanceParticipantUser);
        //List<Instance> userInstances = user.getInstances();
        //userInstances.add(instanceToSubscribe);
        //userRepository.save(user);
//        return mapper.toInstanceDTO(instanceToSubscribe);
    }

}
