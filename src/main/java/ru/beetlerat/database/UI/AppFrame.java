package ru.beetlerat.database.UI;

import ru.beetlerat.database.UI.panels.DefinitionPanel;
import ru.beetlerat.database.UI.panels.PasswordPanel;
import ru.beetlerat.database.UI.panels.WordPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppFrame {
    private JFrame mainFrame; // Отображаемая форма
    private JTabbedPane multiPanel; // Панель с вкладками
    private PasswordPanel passwordPanel; // Панель ввода данных для подключения к БД

    public AppFrame(){
        // Создать новый контейнер типа JFrame с подписью Запоминалка
        mainFrame=new JFrame("Запоминалка");
        // Задать размеры формы
        mainFrame.setSize(600,600);
        // Установить действие при закрытии формы - завершить приложение
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Установить потоковый диспетчер компановки
        mainFrame.setLayout(new FlowLayout());

        // Создать панель с вкладками
        multiPanel=new JTabbedPane();
        // Установить размеры панели с вкладками
        multiPanel.setPreferredSize(new Dimension(400,510));
        // Заполнить панель с вкладками
        fillTabbedPane();

        // Добавить панель с вкладками на форму
        mainFrame.add(multiPanel);

        // Отобразить форму
        mainFrame.setVisible(true);
    }

    // Заполнить панель с вкладками
    private void fillTabbedPane() {

        // Создаем добавляемые панели
        DefinitionPanel definitionPanel=new DefinitionPanel(mainFrame);
        WordPanel wordPanel=new WordPanel();

        // Вызвать диалоговое окно
        passwordPanel=new PasswordPanel(mainFrame,definitionPanel,wordPanel);
        passwordPanel.setVisible(true);
        // Создать слушателя закрытия диалогового окна
        passwordPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Заполнить comboBox с именами всех таблиц
                definitionPanel.fillTablesNames();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // Заполнить comboBox с именами всех таблиц
                definitionPanel.fillTablesNames();
            }
        });

        // Добавлеяем панели на панель со вкладками
        multiPanel.addTab("Definition",definitionPanel);
        multiPanel.addTab("Words", wordPanel);
    }
}
