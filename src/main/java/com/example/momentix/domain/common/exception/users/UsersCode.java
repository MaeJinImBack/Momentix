package com.example.momentix.domain.common.exception.users;


public enum UsersCode {

    INVALID_PHONE_NUMBER_FORMAT(400, "전화번호 형식이 올바르지 않습니다."),
    NO_CHANGES_TO_UPDATE(400, "변경할 정보가 없습니다."),
    PASSWORD_CONFIRMATION_REQUIRED(400, "새 비밀번호와 확인 비밀번호를 모두 입력해주세요."),
    PASSWORD_MISMATCH(400, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다."),
    INCORRECT_PASSWORD(403, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다.");

    private final int status;
    private final String message;

    UsersCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
