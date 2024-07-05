package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class CommentHandler extends GeneralException {

    public CommentHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
