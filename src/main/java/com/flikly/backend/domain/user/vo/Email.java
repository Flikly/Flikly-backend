package com.flikly.backend.domain.user.vo;

import jakarta.persistence.Embeddable;

import com.flikly.backend.domain.user.exception.EmailException.EmptyEmailException;
import com.flikly.backend.domain.user.exception.EmailException.InvalidEmailFormatException;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class Email {

    public Email(String emailValue) {
    }


    public Email() {

    }
}

