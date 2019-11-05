package com.unicam.chorchain.instance;

import com.unicam.chorchain.choreography.ChoreographyRepository;
import com.unicam.chorchain.model.Choreography;
import com.unicam.chorchain.model.Instance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InstanceService {

    private final InstanceRepository repository;
    private final ChoreographyRepository choreographyRepository;
    private final InstanceMapper mapper;

    public InstanceDTO create(InstanceRequest instanceRequest) {
        Instance instance = new Instance();
        Choreography choreography = choreographyRepository.findById(instanceRequest.modelId)
                .orElseThrow(() -> new EntityNotFoundException("Choregraphy not found!"));
        instance.setChoreographyModelName(choreography.getName());
        return mapper.toInstanceDTO(repository.save(instance));
    }
}
