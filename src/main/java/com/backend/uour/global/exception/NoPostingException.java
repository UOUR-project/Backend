package com.backend.uour.global.exception;

public class NoPostingException extends Exception{
    private static final int ERR_CODE = 3;
    private static final String MSG = "포스팅이 존재하지 않습니다.";

    public NoPostingException(){
        super(MSG);
    }
    public int getERR_CODE() {
        return ERR_CODE;
    }
    public String getMSG(){
        return MSG;
    }
}
