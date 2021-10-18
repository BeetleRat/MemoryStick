package ru.beetlerat.database.UI.panels;

import ru.beetlerat.database.DAO.DefinitionsDAO;

import ru.beetlerat.database.model.Definition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class UpdateDefinitionDatabase extends JDialog {
    // Тип обновления данных
    // true-добавление нового объекта
    // false - обновление уже существующего
    private boolean newTheorem;

    private JButton okButton;
    private JButton cancelButton;

    private JLabel mainLabel;
    private JLabel nameLabel;
    private JLabel definitionLabel;

    private JTextField nameTextField;
    private JTextArea definitionTextArea;
    // Объект работы с БД
    private DefinitionsDAO definitionsDAO;
    // Родительское окно
    private JFrame owner;
    // id обновляемого объекта
    private int id;

    public UpdateDefinitionDatabase(JFrame owner, DefinitionsDAO definitionsDAO, String windowName, boolean newTheorem) {
        // Создание диалогового окна от родительского окна owner
        // с заголовком windowName
        super(owner, windowName);// Вызов конструктора суперкласса
        this.owner=owner; // Сохранение родительского окна
        this.newTheorem=newTheorem; // Сохранение типа обновления данных
        id=-1;
        // Слушатель закрытия диалогового окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
        // Установка размеров диалогового окна
        setSize(450,450);
        setResizable(false); // Запрет пользователю изменять размеры диалогового окна

        // Сохранение объекта работы с БД
        this.definitionsDAO=definitionsDAO;


        createElements();// Создание элементов GPU
        addListeners(); // Добавление слушателей
        addElementsToDialog(); // Добавление элементов на панель
        // Запрет работы с родительским окном
        this.owner.setEnabled(false);
    }

    // Создание элементов GPU
    private void createElements(){
        okButton=new JButton("OK");
        cancelButton=new JButton("Cancel");

        nameLabel=new JLabel("Название теоремы: ");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        definitionLabel=new JLabel("Теорема: ");
        definitionLabel.setHorizontalAlignment(JLabel.RIGHT);
        mainLabel=new JLabel("Введите данные");

        nameTextField =new JTextField(25);
        definitionTextArea =new JTextArea();
    }

    // Добавление слушателей
    private void addListeners(){
        // Анонимный класс слушателя нажатия enter в текстовых полях и нажатия кнопки ok
        ActionListener textFieldListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Сбросить границы
                nameTextField.setBorder(BorderFactory.createLineBorder(Color.black,1));
                definitionTextArea.setBorder(BorderFactory.createLineBorder(Color.black,1));

                String user= nameTextField.getText();
                String pass= definitionTextArea.getText();
                if(user.equals("")){
                    // Установить красную границу
                    nameTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                }
                else {
                    if(pass.equals("")){
                        // Установить красную границу
                        definitionTextArea.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                    }
                    else {
                        // Сохранить данные с формы в объект updatingData
                        Definition updatingData=new Definition(nameTextField.getText(),definitionTextArea.getText());
                        // Если производится добавление нового элемента
                        if(newTheorem){
                            // Если получилось добавить данные в БД
                            if(definitionsDAO.addDefinitionToTable(updatingData)){
                                closeDialog();// Закрыть диалоговое окно
                            }
                            else {
                                // Вывести на панель сообщение об ошибке
                                showDBException();
                            }
                        }
                        else {
                            // Если производится обнавление уже существующего элемена
                            if(id!=-1){ // Если получилось опознать изменяемый элемент в БД
                                // Внести изменения в БД
                                definitionsDAO.updateDefinitionFromTable(id,updatingData);
                                closeDialog();// Закрыть диалоговое окно
                            }
                            else {
                                // Вывести на панель сообщение об ошибке
                               showDBException();
                            }
                        }
                    }
                }
            }
        };
        nameTextField.addActionListener(textFieldListener);
        okButton.addActionListener(textFieldListener);
        // Закрыть диалоговое окно по нажатию cancelButton
        cancelButton.addActionListener((e)->{
            closeDialog();
        });
    }

    // Добавление элементов на панель
    private void addElementsToDialog(){

        // Использовать диспетчер сеточно-контейнерной компановки
        setLayout(new FlowLayout());
        // Добавить элементы в сеточно-контейнерную компановку
        add(mainLabel);

        // Групперуем nameLabel и nameTextField в dataPanel
        JPanel dataPanel=new JPanel();
        dataPanel.setLayout(new FlowLayout());
        dataPanel.setPreferredSize(new Dimension(400,30));
        dataPanel.add(nameLabel);
        dataPanel.add(nameTextField);
        // Добавляем dataPanel на форму
        add(dataPanel);

        add(definitionLabel);
        // Обернуть текстовую область definition в JScrollPane
        JScrollPane definitionScrollPane = new JScrollPane(definitionTextArea);
        definitionScrollPane.setPreferredSize(new Dimension(400,250));
        add(definitionScrollPane);

        add(cancelButton);
        add(okButton);
    }

    // Заполнить форму данными
    public void setTheorem(String theorem, String definition){
        nameTextField.setText(theorem);
        definitionTextArea.setText(definition);
        // Получить id заполненных данных в БД
        id=definitionsDAO.getDefinitionID(theorem);
    }

    // Действия при ошибке в БД
    private void showDBException(){
        // Установить красную границу
        nameTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
        // Установить красную границу
        definitionTextArea.setBorder(BorderFactory.createLineBorder(Color.RED,2));
        nameTextField.setText("Ошибка БД");
        definitionTextArea.setText("Ошибка БД");
    }

    // Закрытие окна
    private void closeDialog(){
        dispose(); // Закрыть окно
        owner.setEnabled(true);
        // Костыль, что б окошко один раз показалось выше всех окон
        owner.setAlwaysOnTop(true);
        owner.setAlwaysOnTop(false);
    }
}
