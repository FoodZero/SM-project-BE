package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.BaseErrorCode;
import com.sm.project.apiPayload.exception.GeneralException;

public class FcmHandler extends GeneralException {
    public FcmHandler(BaseErrorCode code) {
        super(code);
    }
}
