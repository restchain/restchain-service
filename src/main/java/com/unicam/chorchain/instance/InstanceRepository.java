package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Choreography;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstanceRepository  extends PagingAndSortingRepository<Choreography, String> {
}
