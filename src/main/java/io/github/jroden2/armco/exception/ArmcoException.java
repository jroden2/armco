package io.github.jroden2.armco.exception;

public class ArmcoException extends RuntimeException {

    public ArmcoException(String message) {
        super(message);
    }

    public ArmcoException(String message, Throwable cause) {
        super(message, cause);
    }
}