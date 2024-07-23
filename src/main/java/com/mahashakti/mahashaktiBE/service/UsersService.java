package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.Users;
import com.mahashakti.mahashaktiBE.repository.UsersRepository;
import com.mahashakti.mahashaktiBe.model.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public SignUp saveUser(SignUp signUp) {
        Users users = new Users();
        //users.setId(UUID.randomUUID());
        users.setName(signUp.getName());
        users.setRole(signUp.getRole());
        users.setEmail(signUp.getEmail());
        users.setPassword(signUp.getPassword());
        users.setStatus(Boolean.TRUE);
        users.setPhoneNumber(signUp.getPhoneNumber());

        usersRepository.save(users);

        return signUp;
    }
}
