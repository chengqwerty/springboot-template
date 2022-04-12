package som.make.extend.check;

/**
 * 参数检查结果
 */
public class CheckStatus {

    // 1：通过，-1：未通过,2：无法检测
    private String status;
    private String code;
    private String message;
    private String paramCode;

    public CheckStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public void setResult(String status, String code, String paramCode, String message) {
        this.status = status;
        this.code = code;
        this.paramCode = paramCode;
        this.message = message;
    }

}
