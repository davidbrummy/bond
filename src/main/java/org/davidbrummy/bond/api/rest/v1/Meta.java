package org.davidbrummy.bond.api.rest.v1;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Meta {
    
    private int code = HttpServletResponse.SC_OK;
    private Integer errorCode;
    private String errorMessage;
    
    private Meta() {}

    public static Meta createMeta() {
        Meta meta = new Meta();
        return meta;
    }

    public static Meta createMeta(int responseCode, String errorMessage) {
        Meta meta = createMeta();
        meta.setCode(responseCode);
        meta.setErrorMessage(errorMessage);
        return meta;
    }
    
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public Integer getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    
    
    
}
