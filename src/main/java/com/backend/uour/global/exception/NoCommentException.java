package com.backend.uour.global.exception;

public class NoCommentException extends Exception{
    private static final int ERR_CODE = 5;
    private static final String MSG = "댓글이 존재하지 않습니다.";

    public NoCommentException(){
        super(MSG);
    }
    public int getERR_CODE() {
        return ERR_CODE;
    }
    public String getMSG(){
        return MSG;
    }
}
