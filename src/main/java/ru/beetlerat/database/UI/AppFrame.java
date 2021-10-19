package ru.beetlerat.database.UI;

import ru.beetlerat.database.DAO.DefinitionsDAO;
import ru.beetlerat.database.DAO.WordsDAO;
import ru.beetlerat.database.UI.panels.DefinitionPanel;
import ru.beetlerat.database.UI.panels.PasswordPanel;
import ru.beetlerat.database.UI.panels.WordPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class AppFrame {
    private JFrame mainFrame; // Отображаемая форма
    private PasswordPanel passwordPanel; // Панель ввода данных для подключения к БД
    // Панели отображаемые на форме
    private JTabbedPane multiPanel; // Панель с вкладками
    private DefinitionPanel definitionPanel; // Панель заучивания определений
    private WordPanel wordPanel; // Панель заучивания слов


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
        definitionPanel=new DefinitionPanel(mainFrame);
        wordPanel=new WordPanel();

        createPasswordPanel(); // Создание диалогового окна


        // Добавлеяем панели на панель со вкладками
        multiPanel.addTab("Definition",definitionPanel);
        multiPanel.addTab("Words", wordPanel);
    }

    // Создание диалогового окна
    private void createPasswordPanel(){
        // Вызвать диалоговое окно
        passwordPanel=new PasswordPanel(mainFrame);
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
        // Анонимный класс слушателя нажатия enter в текстовых полях и нажатия кнопки ok
        ActionListener textFieldListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Выносим поля в отдельные переменные для удобства работы
                JTextField textField=passwordPanel.getUserTextField();
                JPasswordField passwordField=passwordPanel.getPasswordTextField();

                // Сбросить границы для текстовых полей
                textField.setBorder(BorderFactory.createLineBorder(Color.black,1));
                passwordField.setBorder(BorderFactory.createLineBorder(Color.black,1));

                String user=textField.getText();
                String pass=passwordField.getText();
                if(user.equals("")){
                    // Установить красную границу
                    textField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                }
                else {
                    if(pass.equals("")){
                        // Установить красную границу
                        passwordField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                    }
                    else {
                        try {
                            // Занесение данных в поля требующие подключения к БД
                            definitionPanel.setDao(new DefinitionsDAO(user,pass));
                            wordPanel.setDao(new WordsDAO(user,pass));

                            passwordPanel.dispose(); // Закрыть окно, если произошло подключение к БД
                            // Разрешить работать с основным окном
                            mainFrame.setEnabled(true);
                            // Костыль, что б окошко один раз показалось выше всех окон
                            mainFrame.setAlwaysOnTop(true);
                            mainFrame.setAlwaysOnTop(false);

                        } catch (SQLException throwables) {
                            // Установить красную границу
                            textField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED,2));

                            textField.setText("Некорректыне данные");
                            passwordField.setText("Некорректыне данные");

                            System.out.println("Ошибка БД: "+throwables);
                        }
                    }
                }
            }
        };
        // Установка анонимного класса в качестве слушателя элементов диалогового поля
        passwordPanel.getUserTextField().addActionListener(textFieldListener);
        passwordPanel.getPasswordTextField().addActionListener(textFieldListener);
        passwordPanel.getOkButton().addActionListener(textFieldListener);
    }
}
