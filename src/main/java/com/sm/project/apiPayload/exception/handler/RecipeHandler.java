package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;

public class RecipeHandler extends GeneralException {

    public RecipeHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
