package net.spatula.dspatula.exception;

public class ProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProcessingException(String message, Throwable e) {
        super(message, e);
    }

}
