package com.unicam.chorchain.instance;

import com.unicam.chorchain.choreography.ChoreographyRepository;
import com.unicam.chorchain.model.Choreography;
import com.unicam.chorchain.model.Instance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
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

    public InstanceDTO create(InstanceRequest instanceRequest) {
        Instance instance = new Instance();
        Choreography choreography = findChoreographyById(instanceRequest.modelId);
        instance.setChoreographyModelName(choreography.getName());
        List<Instance> choreographyInstances = choreography.getInstances();
        choreographyInstances.add(instance);
        choreography.setInstances(choreographyInstances);
        choreographyRepository.save(choreography);
        repository.save(instance);

        return mapper.toInstanceDTO(repository.save(instance));
    }


    private Choreography findChoreographyById(String id) {
        Choreography choreography = choreographyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Choregraphy not found!"));
        log.debug("Found model {}", id);
        return choreography;
    }

    public List<InstanceDTO> findAllByModelId(String id) {
        List<InstanceDTO> aa = repository.findAllByChoreographyModelName(findChoreographyById(id).getName())
                .stream()
                .map(mapper::toInstanceDTO)
                .collect(Collectors.toList());
        return aa;
    }

    public Instance findInstanceById(String _id) {
        return repository.findById(_id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Instance " + _id + " was not found in the database",
                        _id)));
    }

    public InstanceDTO read(String id) {
        return mapper.toInstanceDTO(findInstanceById(id));
    }

    public String delete(@Valid String _id) {
        repository.delete(findInstanceById(_id));
        return "Instance with id " + _id + " has been removed";
    }
}
