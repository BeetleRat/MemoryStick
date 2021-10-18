package ru.beetlerat.database.UI.panels;

import ru.beetlerat.database.DAO.DefinitionsDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CreateTablePanel extends JDialog {
    JButton okButton;
    JButton cancelButton;

    JLabel mainLabel;

    JTextField nameTextField;
    // Объект работы с БД
    DefinitionsDAO definitionsDAO;
    // Родительское окно
    JFrame owner;

    public CreateTablePanel(JFrame owner, DefinitionsDAO definitionsDAO) {
        // Создание диалогового окна от родительского окна owner
        // с заголовком "Добавление таблицы в БД"
        super(owner, "Добавление таблицы в БД");// Вызов конструктора суперкласса
        this.owner=owner;// Сохранение родительского окна
        this.definitionsDAO=definitionsDAO; // Сохранение объекта работы с БД
        // Слушатель закрытия диалогового окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
        // Установка размеров диалогового окна
        setSize(450,200);
        setResizable(false);// Запрет пользователю изменять размеры диалогового окна


        createElements();// Создание элементов GPU
        addListeners();// Добавление слушателей
        addElementsToDialog();// Добавление элементов на панель
        // Запрет работы с родительским окном
        this.owner.setEnabled(false);
    }

    // Создание элементов GPU
    private void createElements(){
        okButton=new JButton("OK");
        cancelButton=new JButton("Cancel");

        mainLabel=new JLabel("Введите название новой таблицы теорем");

        nameTextField =new JTextField(40);
    }

    // Добавление слушателей
    private void addListeners(){
        // Анонимный класс слушателя нажатия enter в текстовых полях и нажатия кнопки ok
        ActionListener textFieldListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Сбросить границы
                nameTextField.setBorder(BorderFactory.createLineBorder(Color.black,1));

                String user= nameTextField.getText();

                if(user.equals("")){
                    // Установить красную границу
                    nameTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                }
                else {
                    // Если получилось создать новую таблицу в БД
                    if(definitionsDAO.createNewTable(nameTextField.getText())){
                        closeDialog(); // Закрыть диалоговое окно
                    }
                    else {
                        // Установить красную границу
                        nameTextField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
                        nameTextField.setText("Некорректыне данные");
                    }
                }
            }
        };
        nameTextField.addActionListener(textFieldListener);
        okButton.addActionListener(textFieldListener);
        // Закрыть диалоговое окно по нажатии cancelButton
        cancelButton.addActionListener((e)->closeDialog());
    }

    // Добавление элементов на панель
    private void addElementsToDialog(){

        // Использовать диспетчер сеточно-контейнерной компановки
        setLayout(new FlowLayout());
        // Добавить элементы в сеточно-контейнерную компановку
        add(mainLabel);
        add(nameTextField);
        add(cancelButton);
        add(okButton);
    }

    // Закрытие диалогового окна
    private void closeDialog(){
        dispose(); // Закрыть окно
        owner.setEnabled(true);
        // Костыль, что б окошко один раз показалось выше всех окон
        owner.setAlwaysOnTop(true);
        owner.setAlwaysOnTop(false);
    }
}
