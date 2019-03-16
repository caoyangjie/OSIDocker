package com.osidocker.open.micro.vo;

/**
 * 业务异常对象，方便统一处理，支持链式调用。
 * AppException可自动转换成Result格式返回。
 */
public class CoreException extends RuntimeException implements GlobalException {

    // 异常类型、结果编码
    private int code = 0;

    // 异常可带出结果数据
    private Object data;

    public CoreException(ServiceExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.data = serviceExceptionEnum.getMessage();
    }

    public CoreException() {
        super();
    }

    public CoreException(String message) {
        super(message);
    }

    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public CoreException setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public CoreException setData(Object data) {
        this.data = data;
        return this;
    }

}
