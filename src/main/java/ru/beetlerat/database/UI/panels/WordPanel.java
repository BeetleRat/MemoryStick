package ru.beetlerat.database.UI.panels;


import ru.beetlerat.database.DAO.WordsDAO;

import javax.swing.*;

public class WordPanel extends JPanel {
    private WordsDAO wordsDAO;

    public void setDao(WordsDAO dao){
        this.wordsDAO=dao;
    }
}
