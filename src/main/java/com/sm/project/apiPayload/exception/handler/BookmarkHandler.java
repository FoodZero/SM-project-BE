package com.sm.project.apiPayload.exception.handler;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.GeneralException;
import com.sm.project.web.controller.recipe.BookmarkController;

public class BookmarkHandler extends GeneralException {

    public BookmarkHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
