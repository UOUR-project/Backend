package com.backend.uour.global.network;

public enum STATUS {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERER_ERROR(500, "INTERNAL_SERVER_ERROR");

    final int statusCode;
    final String code;

    STATUS(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}
