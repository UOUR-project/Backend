package com.backend.uour.global.exception;

public class WrongJwtException extends Exception{
    private static final int ERR_CODE = 1;
    private static final String MSG = "토큰이 일치하지 않습니다.";

    public WrongJwtException(){
        super(MSG);
    }
    public int getERR_CODE() {
        return ERR_CODE;
    }
    public String getMSG(){
        return MSG;
    }
}
