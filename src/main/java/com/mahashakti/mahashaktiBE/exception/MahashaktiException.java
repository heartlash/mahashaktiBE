package com.mahashakti.mahashaktiBE.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MahashaktiException extends RuntimeException {
    private String code;
    private String status;
    private String message;
    private String errorMessage;
}
