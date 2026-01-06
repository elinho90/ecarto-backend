package com.gs2e.stage_eranove_academy.common.Exceptions;

public enum ErrorCodes {


    TYPE_PROJET_NOT_FOUND(1000),
    TYPE_PROJET_NOT_VALID(1001),
    TYPE_PROJET_ALREADY_IN_USE(1002),



    ;


    private int code;
    ErrorCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}