package com.unicam.chorchain.participantUser;

import com.unicam.chorchain.model.ParticipantUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ParticipantUserRepository extends PagingAndSortingRepository<ParticipantUser, Long> {
}
