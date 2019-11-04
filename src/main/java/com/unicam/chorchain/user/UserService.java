package com.unicam.chorchain.user;

import com.unicam.chorchain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserMapper mapper;
    private final UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute("loggedUser")
    public UserDetails getLoggedUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UserDTO create(@Valid UserRequest userRequest) {


        final Optional<User> userByAddress = repository.findByAddress(userRequest.getAddress());

        if (userByAddress.isPresent()) {
            throw new EntityExistsException(String.format("User with this address '%s' already presents",
                    userRequest.getAddress()));
        }

        final User user = mapper.toUser(userRequest);
        user.setCreated(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return mapper.toUserDTO(repository.save(user));
    }

    public UserDTO read(@Valid Long id) throws UsernameNotFoundException {

        final Optional<User> userById = repository.findById(id);

        if (userById.isPresent()) {
            return mapper.toUserDTO(userById.get());
        } else {
            System.out.println("User not found! " + id);
            throw new UsernameNotFoundException("User " + id + " was not found in the database");
        }
    }

    public UserDTO readByAddress(@Valid String address) throws UsernameNotFoundException {

        final Optional<User> userByAddress = repository.findByAddress(address);

        if (userByAddress.isPresent()) {
            return mapper.toUserDTO(userByAddress.get());
        } else {
            System.out.println("User not found! " + address);
            throw new UsernameNotFoundException("User " + address + " was not found in the database");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("username! " + s);

        final Optional<User> userByAddress = repository.findByAddress(s);


        if (!userByAddress.isPresent()) {
            System.out.println("User not found! " + s);
            throw new UsernameNotFoundException("User " + s + " was not found in the database");
        }
        System.out.println("userByAddress.get().getPassword()! " + userByAddress.get().getPassword());

        UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(
                userByAddress.get().getAddress(),
                userByAddress.get().getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        System.out.println(userDetails.toString());

        return userDetails;
    }
}
