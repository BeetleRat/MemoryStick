package ru.beetlerat.database;

import ru.beetlerat.database.UI.AppFrame;

import javax.swing.*;

public class MemoryStick {
    public static void main(String args[]){
        // Создать форму в потоке диспетчерезации событий через анонимный класс
        SwingUtilities.invokeLater(new Runnable() {
            // Перегрузка методов интерфейса
            public void run() {
                // Создание формы в парралельном потоке Swing
                new AppFrame();
            }
        });
    }
}
