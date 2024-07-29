package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.service.UsersService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.UsersApi;
import com.mahashakti.mahashaktiBe.model.Login;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UsersService usersService;

    @Override
    public ResponseEntity<MahashaktiResponse> postUsersSignup(SignUp signup) {

        SignUp signedUpUser = usersService.saveUser(signup);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "USER ADDED", "SUCCESS", signedUpUser);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postUsersLogin(Login login) {

        Login loggedIn = usersService.authenticateAndGenerateJWT(login);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "USER AUTHENTICATED", "SUCCESS", loggedIn);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

}
