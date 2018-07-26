package com.osidocker.open.micro.vo;

public interface GlobalException {

    String getMessage();

    Throwable getCause();

    int getCode();

    GlobalException setCode(int code);

    Object getData();

    GlobalException setData(Object data);
}
