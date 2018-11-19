package com.orcode.test;

import java.io.Serializable;

public class User implements Serializable {
    private String userid;
    private String username;
    private int age;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "用户Id：" + userid + ", 用户名：" + username + ", 年龄：" + age ;
    }
}
