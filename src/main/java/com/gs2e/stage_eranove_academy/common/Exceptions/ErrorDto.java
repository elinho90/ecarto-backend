package com.gs2e.stage_eranove_academy.common.Exceptions;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDto {

    private Integer httpCode;

    private ErrorCodes code;

    private String message;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}