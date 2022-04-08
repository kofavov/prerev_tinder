package ru.liga.client.entity;

public enum Gender {
    MALE("Сударъ"),FEMALE("Сударыня");
    String rus;
    Gender(String rus) {
        this.rus = rus;
    }

    public String getRus() {
        return rus;
    }
}
