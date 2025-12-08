package com.sm.project.redis.pub_sub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {  //직렬화하므로 Serializable 구현

    //직렬화된 객체의 버전 식별용 -> 이게 같으면 역직렬화 시 Dto가 달라도 에러 안남
    private static final long serialVersionUID = 1L;
    private String email;  //전송할 메일(메시지)
    private String verificationCode;  //인증 번호(메시지)
}
