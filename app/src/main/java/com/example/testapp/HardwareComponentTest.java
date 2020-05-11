package com.example.testapp;

public class HardwareComponentTest {
    private String name;
    private int number;

    HardwareComponentTest(String name, int number) {
        this.name=name;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
