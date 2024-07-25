package com.mahashakti.mahashaktiBE.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MismatchException extends RuntimeException {
    private String message;
}
