package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.UserEntity;
import com.mahashakti.mahashaktiBE.repository.UserRepository;
import com.mahashakti.mahashaktiBE.security.JwtService;
import com.mahashakti.mahashaktiBE.security.MyUsersDetailService;
import com.mahashakti.mahashaktiBE.security.WebSecurityConfiguration;
import com.mahashakti.mahashaktiBe.model.Login;
import com.mahashakti.mahashaktiBe.model.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final WebSecurityConfiguration webSecurityConfiguration;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MyUsersDetailService myUsersDetailService;

    public SignUp saveUser(SignUp signUp) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(signUp, userEntity, "password");
        userEntity.setPassword(webSecurityConfiguration.passwordEncoder().encode(signUp.getPassword()));

        UserEntity userSaved = userRepository.save(userEntity);

        signUp.setId(userSaved.getId());
        signUp.setPassword("");

        return signUp;
    }

    public Login authenticateAndGenerateJWT(Login login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword()
        ));

        if(authentication.isAuthenticated()) {
            String generatedToken = jwtService.generateToken(myUsersDetailService.loadUserByUsername(login.getUsername()));
            login.setAccessToken(generatedToken);
            login.setPassword(null);
            return login;
        }
        else {
            return null;
        }
    }

}
