package ru.liga.client.entity;

public enum Gender {
    MALE("Сударъ",FindGender.MALE),
    FEMALE("Сударыня",FindGender.FEMALE);

    String rus;
    FindGender forFind;

    Gender(String rus,FindGender forFind) {
        this.forFind = forFind;
        this.rus = rus;
    }

    public String getRus() {
        return rus;
    }

    public FindGender getForFind() {
        return forFind;
    }
}
