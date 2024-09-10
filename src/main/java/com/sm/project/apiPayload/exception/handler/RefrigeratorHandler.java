package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class RefrigeratorHandler extends GeneralException {

    public RefrigeratorHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
