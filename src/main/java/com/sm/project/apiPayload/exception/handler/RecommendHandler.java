package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class RecommendHandler extends GeneralException {

    public RecommendHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
