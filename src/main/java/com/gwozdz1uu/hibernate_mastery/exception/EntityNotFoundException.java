package com.gwozdz1uu.hibernate_mastery.exception;

public class EntityNotFoundException extends DomainException{
    public EntityNotFoundException(String message) {
        super(message);
    }
}
