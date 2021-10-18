package ru.beetlerat.database.model;

public class Definition {
    // Хранимые данные
    private String name; // Название теоремы
    private String definition; // Ее основная часть

    // Конструкторы
    public Definition(){
        name="Не заданно";
        definition="Не заданно";
    }
    public Definition(String name, String definition) {
        this.name = name;
        this.definition = definition;
    }
    // Геттеры
    public String getName() {
        return name;
    }
    public String getDefinition() {
        return definition;
    }
    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
