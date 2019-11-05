package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Instance;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InstanceRepository  extends PagingAndSortingRepository<Instance, String> {
    List<Instance> findAllByChoreographyModelName(String id);
}
