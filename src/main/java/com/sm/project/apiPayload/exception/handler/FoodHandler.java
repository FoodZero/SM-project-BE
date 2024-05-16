package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class FoodHandler extends GeneralException {

    public FoodHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
