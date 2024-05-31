package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class PostHandler extends GeneralException {

    public PostHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
