package ru.beetlerat.database.UI.panels;

import ru.beetlerat.database.DAO.DefinitionsDAO;
import ru.beetlerat.database.DAO.WordsDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class PasswordPanel extends JDialog {
    // Кнопки
    JButton okButton;
    JButton cancelButton;

    JLabel userLabel;
    JLabel passwordLabel;
    JLabel mainLabel;
    // Поля ввода
    JTextField userTextField;
    JPasswordField passwordTextField;
    // Панели требующие логин в БД
    DefinitionPanel definitionPanel;
    WordPanel wordPanel;
    // Создатель диалогового окна
    JFrame owner;

    public PasswordPanel(JFrame owner, DefinitionPanel definitionPanel,WordPanel wordPanel) {
        // Создание диалогового с именем "Подключение к базе данных" окна от родительского окна parent
        super(owner, "Подключение к базе данных");// Вызов конструктора суперкласса
        this.owner=owner; // Сохранение создателя
        // Слушатель закрытия окна по нажатию крестика
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();// Закрыть окно
                System.exit(0); // Завершить программу
            }
        });
        // Настроить размеры диалогового окна
        setSize(450,200);
        setResizable(false); // Запретить изменять размер пользователю

        // Сохранение панелей требующих доступ к БД
        this.definitionPanel=definitionPanel;
        this.wordPanel=wordPanel;

        createElements(); // Создать элементы GPU
        addListeners(); // Добавить слушателей
        addElementsToDialog(); // Добавить элементы в диалоговое окно

        // Запретить работу с основным окном
        this.owner.setEnabled(false);
    }

    // Создать элементы GPU
    private void createElements(){
        okButton=new JButton("OK");
        cancelButton=new JButton("Cancel");

        userLabel=new JLabel("User: ");
        userLabel.setHorizontalAlignment(JLabel.RIGHT);

        passwordLabel=new JLabel("Password: ");
        passwordLabel.setHorizontalAlignment(JLabel.RIGHT);

        mainLabel=new JLabel("Для подключения к базе данных введите имя пользователя и пароль.");

        userTextField=new JTextField(10);
        userTextField.setToolTipText("Поле для ввода имени пользователя");

        passwordTextField=new JPasswordField(10);
        passwordTextField.setToolTipText("Поле для ввода пароля");
    }

    // Добавление слушателей
    private void addListeners(){
        // Анонимный класс слушателя нажатия enter в текстовых полях и нажатия кнопки ok
        ActionListener textFieldListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Сбросить границы
                userTextField.setBorder(BorderFactory.createLineBorder(Color.black,1));
                passwordTextField.setBorder(BorderFactory.createLineBorder(Color.black,1));

                String user=userTextField.getText();
                String pass=passwordTextField.getText();
                if(user.equals("")){
                    // Установить красную границу
                    userTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                }
                else {
                    if(pass.equals("")){
                        // Установить красную границу
                        passwordTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                    }
                    else {
                        try {
                            // Передать данные для входа в БД панелям
                            definitionPanel.setDao(new DefinitionsDAO(user,pass));
                            wordPanel.setDao(new WordsDAO(user,pass));

                            dispose(); // Закрыть окно, если произошло подключение к БД
                            // Разрешить работать с основным окном
                            owner.setEnabled(true);
                            // Костыль, что б окошко один раз показалось выше всех окон
                            owner.setAlwaysOnTop(true);
                            owner.setAlwaysOnTop(false);

                        } catch (SQLException throwables) {
                            // Установить красную границу
                            userTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                            passwordTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));

                            userTextField.setText("Некорректыне данные");
                            passwordTextField.setText("Некорректыне данные");

                            System.out.println("Ошибка БД: "+throwables);
                        }
                    }
                }
            }
        };
        userTextField.addActionListener(textFieldListener);
        passwordTextField.addActionListener(textFieldListener);
        okButton.addActionListener(textFieldListener);
        // Завершить программу по нажатии кнопки cancel
        cancelButton.addActionListener((e)->System.exit(0));
    }

    private void addElementsToDialog(){
        // Создать объект диспетчера сеточно-контейнерной компановки
        GridBagLayout form=new  GridBagLayout();
        // Создать объект ограничений диспетчера сеточно-контейнерной компановки
        GridBagConstraints settings=new GridBagConstraints();
        // Настройка ограничений для первой строки
        settings.weighty=0.0; // Вес столбца
        settings.weightx=1.0; // Вес строки
        settings.ipadx=200;// Заполнение на 200 единиц
        // Добавить всавку относительно левого верхнего угла
        settings.insets=new Insets(0,6,0,0);
        settings.anchor=GridBagConstraints.NORTHEAST;
        // Обозначить данный элемент концом текущей строки
        settings.gridwidth=GridBagConstraints.REMAINDER;
        // Установить описанные выше ограничения для элемента mainLabel
        form.setConstraints(mainLabel,settings);

        // Обозначить данный элемент продолжением текущей строки
        settings.gridwidth=GridBagConstraints.RELATIVE;
        // Установить описанные выше ограничения для элемента userLabel
        form.setConstraints(userLabel,settings);
        // Обозначить данный элемент концом текущей строки
        settings.gridwidth=GridBagConstraints.REMAINDER;
        // Установить описанные выше ограничения для элемента userTextField
        form.setConstraints(userTextField,settings);


        // Обозначить данный элемент продолжением текущей строки
        settings.gridwidth=GridBagConstraints.RELATIVE;
        // Установить описанные выше ограничения для элемента passwordLabel
        form.setConstraints(passwordLabel,settings);
        // Обозначить данный элемент концом текущей строки
        settings.gridwidth=GridBagConstraints.REMAINDER;
        // Установить описанные выше ограничения для элемента passwordTextField
        form.setConstraints(passwordTextField,settings);

        // Обозначить данный элемент продолжением текущей строки
        settings.gridwidth=GridBagConstraints.RELATIVE;
        // Установить описанные выше ограничения для элемента cancelButton
        form.setConstraints(cancelButton,settings);
        // Обозначить данный элемент концом текущей строки
        settings.gridwidth=GridBagConstraints.REMAINDER;
        // Установить описанные выше ограничения для элемента okButton
        form.setConstraints(okButton,settings);

        // Использовать диспетчер сеточно-контейнерной компановки
        setLayout(form);
        // Добавить элементы в сеточно-контейнерную компановку
        add(mainLabel);
        add(userLabel);
        add(userTextField);
        add(passwordLabel);
        add(passwordTextField);
        add(cancelButton);
        add(okButton);
    }
}
