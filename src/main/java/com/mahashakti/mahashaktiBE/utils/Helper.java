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

    public static String capitalizeFirstLetter(String input) {

        String[] words = input.split(" ");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

}
