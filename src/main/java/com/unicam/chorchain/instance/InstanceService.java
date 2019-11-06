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
        instance.setChoreography(choreography);
//        List<Instance> choreographyInstances = choreography.getInstances();
//        choreographyInstances.add(instance);
//        choreography.setInstances(choreographyInstances);
//        choreographyRepository.save(choreography);
//        repository.save(instance);

        return mapper.toInstanceDTO(repository.save(instance));
    }


    private Choreography findChoreographyById(Long id) {
        Choreography choreography = choreographyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Choregraphy not found!"));
        log.debug("Found model {}", id);
        return choreography;
    }

    public List<InstanceDTO> findAllByModelId(Long id) {
        List<InstanceDTO> aa = repository.findAllByChoreographyId(findChoreographyById(id).getId())
                .stream()
                .map(mapper::toInstanceDTO)
                .collect(Collectors.toList());
        return aa;
    }

    public Instance findInstanceById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Instance " + id + " was not found in the database",
                        id)));
    }

    public InstanceDTO read(Long id) {
        return mapper.toInstanceDTO(findInstanceById(id));
    }

    public String delete(@Valid Long id) {
        repository.delete(findInstanceById(id));
        return "Instance with id " + id + " has been removed";
    }
}
