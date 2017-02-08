package org.osidocker.open.exceptions;

public class OsidockerException extends RuntimeException {
	private static final long serialVersionUID = -5875371379845226068L;

    /**
     * 数据库操作,insert返回0
     */
    public static final OsidockerException DB_INSERT_RESULT_0 = new OsidockerException(
            10040001, "数据库操作,insert返回0");

    /**
     * 数据库操作,update返回0
     */
    public static final OsidockerException DB_UPDATE_RESULT_0 = new OsidockerException(
            10040002, "数据库操作,update返回0");

    /**
     * 数据库操作,selectOne返回null
     */
    public static final OsidockerException DB_SELECTONE_IS_NULL = new OsidockerException(
            10040003, "数据库操作,selectOne返回null");

    /**
     * 数据库操作,list返回null
     */
    public static final OsidockerException DB_LIST_IS_NULL = new OsidockerException(
            10040004, "数据库操作,list返回null");

    /**
     * Token 验证不通过
     */
    public static final OsidockerException TOKEN_IS_ILLICIT = new OsidockerException(
            10040005, "Token 验证非法");
    /**
     * 会话超时　获取session时，如果是空，throws 下面这个异常 拦截器会拦截爆会话超时页面
     */
    public static final OsidockerException SESSION_IS_OUT_TIME = new OsidockerException(
            10040006, "会话超时");

    /**
     * 生成序列异常时
     */
    public static final OsidockerException DB_GET_SEQ_NEXT_VALUE_ERROR = new OsidockerException(
            10040007, "序列生成超时");

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    public OsidockerException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
        this.msg = String.format(msgFormat, args);
    }

    public OsidockerException() {
        super();
    }

    public OsidockerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OsidockerException(Throwable cause) {
        super(cause);
    }

    public OsidockerException(String message) {
        super(message);
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    /**
     * 实例化异常
     * 
     * @param msgFormat
     * @param args
     * @return
     */
    public OsidockerException newInstance(String msgFormat, Object... args) {
        return new OsidockerException(this.code, msgFormat, args);
    }

}
