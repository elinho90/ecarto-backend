package com.gs2e.stage_eranove_academy.common.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ApiException extends RuntimeException {
    private final String status;
    private final HttpStatus status2;
    private final ApiErrorCode code;

    public ApiException(String status, ApiErrorCode code, String message, HttpStatus status2) {
        super(message);
        this.status = status;
        this.code = code;
        this.status2 = status2;
    }
}
