package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class FamilyHandler extends GeneralException {
    public FamilyHandler(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
