package ru.liga.client.entity;

public enum Gender {
    Male("Сударъ"),Female("Сударыня");
    String gender;
    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return gender;
    }
}
