package com.mahashakti.mahashaktiBE.utils;

import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;

import org.springframework.stereotype.Component;

@Component
public class Helper {

    public static MahashaktiResponse createResponse(String code, String message, String status, Object data) {
        MahashaktiResponse mahashaktiResponse = new MahashaktiResponse();
        mahashaktiResponse.setCode(code);
        mahashaktiResponse.setMessage(message);
        mahashaktiResponse.setStatus(status);
        mahashaktiResponse.setData(data);
        return mahashaktiResponse;
    }
}
