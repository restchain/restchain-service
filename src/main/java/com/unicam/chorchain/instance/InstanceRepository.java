package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Instance;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstanceRepository  extends PagingAndSortingRepository<Instance, String> {
}
