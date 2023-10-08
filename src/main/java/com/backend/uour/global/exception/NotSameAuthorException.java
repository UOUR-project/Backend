package com.backend.uour.global.exception;

public class NotSameAuthorException extends Exception {
    private static final int ERR_CODE = 4;
    private static final String MSG = "작성자가 일치하지 않습니다.";

    public NotSameAuthorException() {
        super(MSG);
    }

    public int getERR_CODE() {
        return ERR_CODE;
    }

    public String getMSG() {
        return MSG;
    }
}
