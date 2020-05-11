package com.example.testapp;

public class TestClass {
    private String name;
    private String value;

    TestClass(String name, String value) {
        this.name=name;
        this.value=value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
