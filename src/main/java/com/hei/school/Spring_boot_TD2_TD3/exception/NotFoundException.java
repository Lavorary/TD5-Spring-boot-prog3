package com.hei.school.Spring_boot_TD2_TD3.exception;

public class NotFoundException  extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
