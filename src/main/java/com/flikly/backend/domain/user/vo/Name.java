package com.flikly.backend.domain.user.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Name {

    private String value;
    public Name(String nameValue) {
    }

    public Name() {

    }
}
