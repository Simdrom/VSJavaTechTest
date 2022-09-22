package com.raulfd.vsjavatech.domain;

import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "Fullname")
    private String fullName;
    private String phone;
    private String address;

    public User() {
    }

    public User(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"fullname\":" + '\"' + fullName + '\"' + "," +
                "\"phone\":" + '\"' + phone + '\"' + "," +
                "\"address\":" + '\"' + address + '\"' +
                '}';
    }
}