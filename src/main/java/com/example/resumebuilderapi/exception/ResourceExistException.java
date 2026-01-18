package com.example.resumebuilderapi.exception;

public class ResourceExistException extends RuntimeException {

    public ResourceExistException(String message){
        super(message);
    }
}
