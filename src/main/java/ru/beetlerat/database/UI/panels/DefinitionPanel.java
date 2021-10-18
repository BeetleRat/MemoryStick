package ru.beetlerat.database.UI.panels;

import ru.beetlerat.database.DAO.DefinitionsDAO;
import ru.beetlerat.database.model.Definition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DefinitionPanel extends JPanel {
    // Основное окно программы
    private JFrame mainFrame;

    private JTextField theorem;
    private JTextArea definition;

    private JButton nextButton;
    private JButton showButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton createTableButton;

    private JLabel theoremLabel;
    private JLabel definitionLabel;
    // Список существующих таблиц в БД
    private JComboBox<String> selectedTable;
    // Объект взаимодействия с БД
    private DefinitionsDAO definitionsDAO;

    public DefinitionPanel(JFrame owner) {
        mainFrame=owner; // Сохранить основное окно программы
        setSize(new Dimension(400,500));
        // По умолчанию объект взаимодействия с БД = null
        // после завершения работы окна PasswordPanel через сеттер будет создан новый объект
        definitionsDAO=null;

        createElements(); // Создать элементы GPU
        addListeners();// Добавить слушателей
        addElementsToPanel();// Добавить элементы на панель
    }

    // Создать элементы GPU
    private void createElements(){
        selectedTable=new JComboBox<String>();
        selectedTable.setPreferredSize(new Dimension(180,25));

        theoremLabel=new JLabel("Название теоремы:");

        theorem=new JTextField(25);
        theorem.setEnabled(false);
        theorem.setDisabledTextColor(Color.BLACK);


        definitionLabel=new JLabel("Теорема:");

        definition=new JTextArea();
        definition.setEnabled(false);
        definition.setDisabledTextColor(Color.BLACK);


        showButton=new JButton("Show");
        showButton.setPreferredSize(new Dimension(65,25));
        nextButton=new JButton("Next");
        nextButton.setPreferredSize(new Dimension(65,25));

        addButton=new JButton("Добавить теорему");
        addButton.setPreferredSize(new Dimension(180,25));
        updateButton=new JButton("Изменить теорему");
        updateButton.setPreferredSize(new Dimension(180,25));
        deleteButton = new JButton("Удалить теорему");
        deleteButton.setPreferredSize(new Dimension(180,25));
        createTableButton=new JButton("Создать новую таблицу");
        createTableButton.setPreferredSize(new Dimension(180,25));
    }

    // Добавить слушателей
    private void addListeners(){

        // Добавление теоремы в таблицу
        addButton.addActionListener((e)->{
            // Вызов диалогового окна обновления данных
            UpdateDefinitionDatabase dialogPanel = new UpdateDefinitionDatabase(mainFrame,definitionsDAO,"Добавление записи",true);
            dialogPanel.setVisible(true);
        });

        // Создание новой таблицы
        createTableButton.addActionListener((e)->{
            // Вызов диалогового окна добавления таблицы
            CreateTablePanel dialogPanel= new CreateTablePanel(mainFrame,definitionsDAO);
            dialogPanel.setVisible(true);
            // Добавление слушателя закрытия диалогового окна
            dialogPanel.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Заполнить comboBox с именами всех таблиц
                    fillTablesNames();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    // Заполнить comboBox с именами всех таблиц
                    fillTablesNames();
                }
            } );
        } );

        // Вызов случайной теоремы из БД
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int size = definitionsDAO.getSize();
                if(size>1){
                    // В табличке идет индексация с 0
                    size--;
                    Random random=new Random(System.currentTimeMillis());
                    int id=random.nextInt(size);
                    // Под нулевым id всегда находится шапка таблицы, потому увеличиваем на 1
                    id++;
                    Definition data=definitionsDAO.getDefinitionFromTable(id);

                    theorem.setText(data.getName());
                    definition.setText("");

                    nextButton.setEnabled(false);
                    showButton.setEnabled(true);
                }
                else {
                    theorem.setText("Выбранная таблица пуста");
                    definition.setText("");
                }
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });

        // Обновление выбранной теоремы
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Получение id обновляемой теоремы
                int id=definitionsDAO.getDefinitionID(theorem.getText());
                // Создание диалогового окна обновления данных
                UpdateDefinitionDatabase dialogPanel = new UpdateDefinitionDatabase(mainFrame,definitionsDAO,"Добавление записи",false);
                dialogPanel.setVisible(true);
                // Установка данных в диалоговом окне
                dialogPanel.setTheorem(theorem.getText(),definition.getText());
                // Добавление слушателя закрытия диалогового окна
                dialogPanel.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Получить обновленные данные из БД
                        Definition data=definitionsDAO.getDefinitionFromTable(id);
                        // Записать обнавленные данные в элементы
                        theorem.setText(data.getName());
                        definition.setText(data.getDefinition());
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Получить обновленные данные из БД
                        Definition data=definitionsDAO.getDefinitionFromTable(id);
                        // Записать обнавленные данные в элементы
                        theorem.setText(data.getName());
                        definition.setText(data.getDefinition());
                    }
                });
            }
        });

        // Удаление выбранной теоремы
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Получить id выбранной теоремы
                int id=definitionsDAO.getDefinitionID(theorem.getText());
                // Удалить теорему из БД
                definitionsDAO.deleteDefinitionFromTable(id);
                // Вызвать следующую теорему
                nextButton.doClick();
            }
        });

        // Показать определение для теоремы
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextButton.setEnabled(true);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                showButton.setEnabled(false);
                // Получить id выбранной теоремы
                int id=definitionsDAO.getDefinitionID(theorem.getText());
                // Получить данные из БД
                Definition data=definitionsDAO.getDefinitionFromTable(id);
                // Записать данные в TextArea
                definition.setText(data.getDefinition());
            }
        });

        // Выбор новой таблицы в ComboBox
        selectedTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextButton.setEnabled(true);
                showButton.setEnabled(false);
                // Указать объекту работы с БД,
                // что теперь работа производится в другой таблице
                definitionsDAO.setTable((String) selectedTable.getSelectedItem());
            }
        });
    }

    // Добавление элементов на панель
    private void addElementsToPanel(){
        // Установка поточной компановки
        setLayout(new FlowLayout());

        add(theoremLabel);
        add(theorem);
        add(definitionLabel);

        // Обернуть текстовую область definition в JScrollPane
        JScrollPane definitionScrollPane = new JScrollPane(definition);
        definitionScrollPane.setPreferredSize(new Dimension(340,250));
        add(definitionScrollPane);

        // Групперуем кнопки в панель dataPanel
        JPanel dataPanel=new JPanel();
        dataPanel.setLayout(new FlowLayout());
        dataPanel.setPreferredSize(new Dimension(180,150));
        dataPanel.add(createTableButton);
        dataPanel.add(addButton);
        dataPanel.add(updateButton);
        dataPanel.add(deleteButton);

        // Групперуем кнопки и панель dataPanel в панель dataPanel1
        JPanel dataPanel1=new JPanel();
        dataPanel1.setLayout(new FlowLayout());
        dataPanel1.setPreferredSize(new Dimension(325,150));
        dataPanel1.add(showButton);
        dataPanel1.add(dataPanel);
        dataPanel1.add(nextButton);

        // Групперуем кнопки и панель dataPanel1 в панель dataPanel2
        JPanel dataPanel2=new JPanel();
        dataPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        dataPanel2.setPreferredSize(new Dimension(325,200));
        dataPanel2.add(selectedTable);
        dataPanel2.add(dataPanel1);

        // Добавляем dataPanel2 на панель
        add(dataPanel2);
    }

    // Установка объекта работы с БД
    public void setDao(DefinitionsDAO dao){
        this.definitionsDAO=dao;
    }

    // Заполнить comboBox с именами всех таблиц
    public void fillTablesNames(){
        // Очистить comboBox
        selectedTable.removeAllItems();
        // Получить список имен таблиц БД
        List<String> names=definitionsDAO.getTablesNames();
        // Заполнить comboBox из списка
        for(String name:names){
            selectedTable.addItem(name);
        }
        // Если в БД вообще есть таблицы
        if(names.size()!=0){
            // Устанавливаем текущей таблицей первую выбранную в comboBox
            // Если definitionsDAO не смогла найти таблицу с таким именем
            if(!definitionsDAO.setTable(names.get(0))){
                theorem.setText("Выбрана не существующая таблица");
                definition.setText("");
            }
        }

    }



}
