package som.make.extend.wrapper;

import java.io.Serializable;

public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 199109285717L;

    public static final int NO_LOGIN = -1;
    public static final int SUCCESS = 200;
    public static final int CHECK_FAIL = 405;
    public static final int NO_PERMISSION = 401;
    public static final int SERVER_EXCEPTION = 501;
    public static final int UNKNOWN_EXCEPTION = 502;

    private int code = SUCCESS;
    private String message = "success";
    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(Throwable e) {
        super();
        this.message = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }

    public ResultBean(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultBean(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
