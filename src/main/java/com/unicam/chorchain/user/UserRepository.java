package com.unicam.chorchain.user;


import com.unicam.chorchain.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    Optional<User> findByAddress(String address);
    Optional<User> findById(String _id);
}
