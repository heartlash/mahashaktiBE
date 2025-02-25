package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.config.TwilioConfig;
import com.mahashakti.mahashaktiBE.entities.UserEntity;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.UserRepository;
import com.mahashakti.mahashaktiBE.security.JwtService;
import com.mahashakti.mahashaktiBE.security.MyUsersDetailService;
import com.mahashakti.mahashaktiBE.security.WebSecurityConfiguration;
import com.mahashakti.mahashaktiBe.model.Login;
import com.mahashakti.mahashaktiBe.model.SignUp;
import com.mahashakti.mahashaktiBe.model.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final WebSecurityConfiguration webSecurityConfiguration;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MyUsersDetailService myUsersDetailService;
    private final JavaMailSender mailSender;
    private final TwilioConfig twilioConfig;


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
            UserEntity userEntity = userRepository.findByEmailAndStatus(login.getUsername(), "ACTIVE").get();
            login.setAccessToken(generatedToken);
            login.setName(userEntity.getName());
            login.setRole(userEntity.getRole());
            login.setPassword(null);
            return login;
        }
        else {
            return null;
        }
    }

    public Verification userVerification(Verification verification) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("app.mahashakti@gmail.com");
            message.setTo(verification.getEmail());
            message.setSubject("Verification OTP");
            String emailOtp = String.format("%04d", new SecureRandom().nextInt(9999));
            String emailText = String.format("Your Mahashakti verification code is %s", emailOtp);
            message.setText(emailText);
            mailSender.send(message);

            verification.setEmailOtp(emailOtp);


            String phoneNumberOtp = String.format("%04d", new SecureRandom().nextInt(9999));
            String phoneNumberText = String.format("Your Mahashakti verification code is %s", phoneNumberOtp);


        /*Message.creator(
                new PhoneNumber(verification.getPhoneNumber()),
                new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
                phoneNumberText
        ).create();

         */

            //TimeUnit.SECONDS.sleep(2);

            verification.setPhoneNumberOtp(phoneNumberOtp);

            return verification;
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity getUserDetail(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmailAndStatus(email, "ACTIVE");
        if(userEntityOptional.isEmpty()) throw new ResourceNotFoundException("User Not Found");

        UserEntity userEntity = userEntityOptional.get();
        userEntity.setPassword(null);
        return userEntity;

    }

    public void resetPassword(Login login) {
        UserEntity userEntity = getUserDetail(login.getUsername());
        userEntity.setPassword(webSecurityConfiguration.passwordEncoder().encode(login.getPassword()));
        userRepository.save(userEntity);
    }

}
