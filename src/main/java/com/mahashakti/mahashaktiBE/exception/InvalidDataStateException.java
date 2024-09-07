package com.mahashakti.mahashaktiBE.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvalidDataStateException extends RuntimeException {
    private String message;
}
