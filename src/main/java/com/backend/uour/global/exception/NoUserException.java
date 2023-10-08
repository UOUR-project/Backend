package com.backend.uour.global.exception;

public class NoUserException extends Exception{
    private static final int ERR_CODE = 2;
    private static final String MSG = "유저가 존재하지 안습니다.";

    public NoUserException(){
        super(MSG);
    }
    public int getERR_CODE() {
        return ERR_CODE;
    }
    public String getMSG(){
        return MSG;
    }
}
