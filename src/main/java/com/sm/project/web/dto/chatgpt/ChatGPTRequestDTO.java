package com.sm.project.web.dto.chatgpt;

import lombok.Getter;

public class ChatGPTRequestDTO {

    @Getter
    public static class PromptDTO {
        private String prompt;  //사용자가 지피티한테 입력할 내용을 담음
    }

}
