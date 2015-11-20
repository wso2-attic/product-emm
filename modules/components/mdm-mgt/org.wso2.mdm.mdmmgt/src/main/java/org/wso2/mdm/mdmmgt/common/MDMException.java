package org.wso2.mdm.mdmmgt.common;


public class MDMException extends Exception{

    private static final long serialVersionUID = 5136875495185597926L;
    private String errorMessage;


    public MDMException(String msg, Exception e) {
        super(msg, e);
        setErrorMessage(msg);
    }

    public MDMException(String msg, Throwable cause) {
        super(msg, cause);
        setErrorMessage(msg);
    }

    public MDMException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public MDMException() {
        super();
    }

    public MDMException(Throwable cause) {
        super(cause);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
