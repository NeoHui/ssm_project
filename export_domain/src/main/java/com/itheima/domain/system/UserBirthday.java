package com.itheima.domain.system;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserBirthday implements Serializable {
    private String id;
    private String name;
    private String email;
    private String birthday;
}
