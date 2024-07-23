package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.service.UsersService;
import com.mahashakti.mahashaktiBe.api.UsersApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UsersService usersService;

    @Override
    public ResponseEntity<MahashaktiResponse> postUsersSignup(SignUp signup) {
        usersService.saveUser(signup);
        MahashaktiResponse mahashaktiResponse = new MahashaktiResponse();
        mahashaktiResponse.code("MSBE200");
        mahashaktiResponse.setMessage("USER ADDED");
        mahashaktiResponse.setStatus("SUCCESS");

        return ResponseEntity.ok(mahashaktiResponse);
    }

}
