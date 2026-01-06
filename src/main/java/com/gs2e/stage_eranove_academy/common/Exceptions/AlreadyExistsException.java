package com.gs2e.stage_eranove_academy.common.Exceptions;

public class AlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
