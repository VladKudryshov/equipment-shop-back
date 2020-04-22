package com.sites.equipmentshop.security.exceptions;

public class MailException extends RuntimeException{

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
