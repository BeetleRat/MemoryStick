package ru.beetlerat.database.model;

public class Word {
    // Хранимые данные
    private String word;
    private String translation;
    private String firstForm;
    private String secondForm;
    private String thirdForm;

    // Конструкторы
    public Word(){
        word="Слово не указанно";
        translation="Перевод не указан";
        firstForm="Не является неправильным глаголом";
        secondForm="Не является неправильным глаголом";
        thirdForm="Не является неправильным глаголом";
    }
    public Word(String word, String translation) {
        this.word = word;
        this.translation = translation;
        firstForm="Не является неправильным глаголом";
        secondForm="Не является неправильным глаголом";
        thirdForm="Не является неправильным глаголом";
    }
    public Word( String firstForm, String secondForm, String thirdForm,String translation) {
        word="Неправильный глагол: "+firstForm;
        this.translation = translation;
        this.firstForm = firstForm;
        this.secondForm = secondForm;
        this.thirdForm = thirdForm;
    }

    // Сеттеры
    public void setIrregularVerbs(String firstForm, String secondForm, String thirdForm,String translation){
        word="Неправильный глагол: "+firstForm;
        this.translation = translation;
        this.firstForm = firstForm;
        this.secondForm = secondForm;
        this.thirdForm = thirdForm;
    }
    public void setWord(String word, String translation){
        this.word = word;
        this.translation = translation;
        firstForm="Не является неправильным глаголом";
        secondForm="Не является неправильным глаголом";
        thirdForm="Не является неправильным глаголом";
    }

    // Геттеры
    public String getWord() {
        return word;
    }
    public String getTranslation() {
        return translation;
    }
    public String getFirstForm() {
        return firstForm;
    }
    public String getSecondForm() {
        return secondForm;
    }
    public String getThirdForm() {
        return thirdForm;
    }
}
