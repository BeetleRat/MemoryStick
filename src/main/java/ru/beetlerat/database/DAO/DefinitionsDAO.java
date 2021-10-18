package ru.beetlerat.database.DAO;

import ru.beetlerat.database.model.Definition;

import java.io.PrintWriter;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DefinitionsDAO {
    // Объект подключения к БД
    private Connection connection;

    private PrintWriter consoleOutput;
    // Имя таблицы, с которой в данный момент работает DAO
    private String tableName;
    // ID, с которой в данный момент работает DAO
    private int tableID;

    public DefinitionsDAO(String user, String password) throws SQLException{
        // Подключение к БД
        connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/memorystick",user,password);
        consoleOutput=new PrintWriter(System.out,true);
    }

    // Создание новой таблицы
    public boolean createNewTable(String name){
        try {
            // Создать шаблон запроса поиска таблиц с именем создаваемой
            PreparedStatement dataBaseRequest=connection.prepareStatement("SELECT * FROM definition_table WHERE table_name = ?;");
            dataBaseRequest.setString(1,name);

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            // КОСТЫЛЬ ПРОВЕРКИ КОЛИЧЕСТВА ОТВЕТОВ
            int sch=0;
            while (dataBaseAnswer.next()){
                sch++;
            }
            // Если уже существует таблица с таким именем
            if(sch>1){
                return false;
            }
            else {
                // Создать шаблон поиска максимального ID таблиц
                dataBaseRequest=connection.prepareStatement("SELECT MAX(table_id) FROM definition_table;");

                // Выполнить SQL запрос возвращающий результат
                // ResultSet - инкапсулирует результать запроса к БД
                dataBaseAnswer= dataBaseRequest.executeQuery();
                dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
                // Установить значение из ответа на запрос, определяемое по номеру столбца
                // Установить id новой таблицы на 1 больше максимального
                tableID=dataBaseAnswer.getInt(1)+1;
                tableName=name; // Установить имя создаваемой таблицы

                // Создать шаблон добавления новой таблицы в БД
                dataBaseRequest=connection.prepareStatement("INSERT INTO definition_table(table_id,table_name,theorem_id,name,definition) VALUES(?,?,?,?,?);");
                dataBaseRequest.setInt(1,tableID);
                dataBaseRequest.setString(2,tableName);
                dataBaseRequest.setInt(3,0);
                dataBaseRequest.setString(4,"Новая таблица с именем "+tableName);
                dataBaseRequest.setString(5,"");

                // Выполнить SQL запрос обнавляющий БД
                dataBaseRequest.executeUpdate();
                return true;
            }

        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return false;
    }

    // Добавление объекта в БД
    public boolean addDefinitionToTable(Definition dataObject){
        try {
            // Создать шаблон запроса поиска в текущей таблице определения с таким же именем
            PreparedStatement dataBaseRequest=connection.prepareStatement("SELECT * FROM definition_table WHERE table_name = ? AND name = ?;");
            dataBaseRequest.setString(1,tableName);
            dataBaseRequest.setString(2,dataObject.getName());

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            // КОСТЫЛЬ ПРОВЕРКИ КОЛИЧЕСТВА ОТВЕТОВ
            int sch=0;
            while (dataBaseAnswer.next()){
                sch++;
            }
            // Если в текущей таблице уже есть добавляемое определение
            if(sch>1){
                return false;
            }
            else {
                // Создать шаблон поиска максимального ID определений
                dataBaseRequest=connection.prepareStatement("SELECT MAX(theorem_id) FROM definition_table WHERE table_name = ?;");
                dataBaseRequest.setString(1,tableName);
                // Выполнить SQL запрос возвращающий результат
                // ResultSet - инкапсулирует результать запроса к БД
                dataBaseAnswer= dataBaseRequest.executeQuery();
                dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
                // Установить значение из ответа на запрос, определяемое по номеру столбца
                // Установить id новго определения на 1 больше максимального
                int theoremID=dataBaseAnswer.getInt(1)+1;

                // Создать шаблон добавления нового определения в БД
                dataBaseRequest=connection.prepareStatement("INSERT INTO definition_table(table_id,table_name,theorem_id,name,definition) VALUES(?,?,?,?,?);");
                // Заполнить шаблон запроса, те места где стоит ?
                dataBaseRequest.setInt(1,tableID);
                dataBaseRequest.setString(2,tableName);
                dataBaseRequest.setInt(3,theoremID);
                dataBaseRequest.setString(4,dataObject.getName());
                dataBaseRequest.setString(5,dataObject.getDefinition());

                // Выполнить SQL запрос обнавляющий БД
                dataBaseRequest.executeUpdate();
                return true;
            }
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return false;
    }

    public Definition getDefinitionFromTable(int id){
        Definition answer=null;
        try {
            // Создать шаблон запроса поиска определения по id в текущей таблице
            PreparedStatement dataBaseRequest=connection.prepareStatement("SELECT name,definition FROM definition_table WHERE table_name=? AND theorem_id=?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,tableName);
            dataBaseRequest.setInt(2,id);

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
            // Создать из ответа на запрос объект Definition
            answer=new Definition(dataBaseAnswer.getString("name"),dataBaseAnswer.getString("definition"));
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return answer;
    }

    // Обновление данных в БД
    public void updateDefinitionFromTable(int id,Definition definition){
        try {
            // Создать шаблон запроса обновления данных в текущей таблице
            PreparedStatement dataBaseRequest=connection.prepareStatement("UPDATE definition_table SET name = ?, definition = ? WHERE table_name=? AND theorem_id=?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,definition.getName());
            dataBaseRequest.setString(2,definition.getDefinition());
            dataBaseRequest.setString(3,tableName);
            dataBaseRequest.setInt(4,id);

            // Выполнить SQL запрос обнавляющий БД
            dataBaseRequest.executeUpdate();
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
    }

    public void deleteDefinitionFromTable(int id){
        try {
            // Создать шаблон запроса удаление данных по id в текущей таблице
            PreparedStatement dataBaseRequest=connection.prepareStatement("DELETE FROM definition_table WHERE table_name=? AND theorem_id=?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,tableName);
            dataBaseRequest.setInt(2,id);

            // Выполнить SQL запрос обнавляющий БД
            dataBaseRequest.executeUpdate();
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
    }

    // Геттер текущей таблицы
    public String getTableName() {
        return tableName;
    }

    // Установить таблицу с которой будет работать DAO
    public boolean setTable(String tableName) {
        try {
            // Создать шаблон запроса поиска id таблицы по имени
            PreparedStatement dataBaseRequest = connection.prepareStatement("SELECT table_id FROM definition_table WHERE table_name = ?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,tableName);

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
            // Установить значение из ответа на запрос, определяемое по номеру столбца
            Integer id=dataBaseAnswer.getInt(1);
            // Если нашлось совпадение по имени в БД
            if(id!=null){
                //  Установить id таблицы с которой сейчас работает DAO
                this.tableID=id;
                // Установить имя таблицы с которой сейчас работает DAO
                this.tableName = tableName;
                return true;
            }
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }

        return false;
    }

    // Получить количество записей в таблице с которой сейчас работает DAO
    public int getSize(){
        try {
            // Создать шаблон запроса подсчета количества записей в текущей таблице
            PreparedStatement dataBaseRequest=connection.prepareStatement("SELECT COUNT(theorem_id) FROM definition_table WHERE table_name = ?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,tableName);

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
            // Вернуть значение первого столбца
            return dataBaseAnswer.getInt(1);
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return -1;
    }

    // Вернуть список всех таблиц хранящихся в БД
    public List<String> getTablesNames(){
        List<String> tablesNames = null;
        try {
            // Создать запрос поиска разных имен таблиц в БД
            Statement dataBaseRequest=connection.createStatement();
            ResultSet dataBaseAnswer=dataBaseRequest.executeQuery("SELECT DISTINCT table_name FROM definition_table;");
            // Записать результаты запроса в список
            tablesNames=new LinkedList<String>();
            while (dataBaseAnswer.next()){
                tablesNames.add(dataBaseAnswer.getString(1));
            }
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return tablesNames;
    }

    // Получить ID определения по имени
    public int getDefinitionID(String name){

        try {
            // Создать шаблон запроса поиск id по имени в текущей таблице
            PreparedStatement dataBaseRequest=connection.prepareStatement("SELECT theorem_id FROM definition_table WHERE table_name=? AND name =?;");
            // Заполнить шаблон запроса, те места где стоит ?
            dataBaseRequest.setString(1,tableName);
            dataBaseRequest.setString(2,name);

            // Выполнить SQL запрос возвращающий результат
            // ResultSet - инкапсулирует результать запроса к БД
            ResultSet dataBaseAnswer= dataBaseRequest.executeQuery();
            dataBaseAnswer.next(); // Выбрать первый элемент из результата запроса
            // Установить значение из ответа на запрос, определяемое по номеру столбца
            Integer answer=dataBaseAnswer.getInt(1);
            // Если искомое определение найдено
            if(answer!=null){
                return answer;
            }
        } catch (SQLException throwables) {
            consoleOutput.println("Ошибка бд: "+throwables);
        }
        return -1;
    }
}
