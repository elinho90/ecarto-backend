package com.gs2e.stage_eranove_academy.common.Exceptions;

public enum ApiErrorCode {
    VALIDATION_ERROR("4002"),
    DATABASE_ERROR("4003"),
    BAD_REQUEST("4004"),
    INVALID_ARGUMENT("4005"),
    INVALID_FORMAT("4006"),
    UNSUPPORTED_OPERATION("4007"),
    INVALID_REQUEST("4008"),
    INVALID_BODY_REQUEST("40081"),
    UNAUTHORIZED("4011"),
    UNAUTHORIZED_ACTION("4012"),
    ENTITY_NOT_FOUND("4041"),

    SERVICE_UNAVAILABLE("5031");

    private final String value;

    ApiErrorCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}