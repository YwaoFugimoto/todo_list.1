package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.book.ChoreBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLChoreRepository implements ChoreRepository {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public List<Chore> load() {
        if (!connectToMySQL()) {
            return new ArrayList<>();
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ChoreBook.FIND_ALL_CHORES);

            List<Chore> chores = new ArrayList<>();
            while(resultSet.next()) {
                Chore chore = Chore.builder()
                        .description(resultSet.getString("description"))
                        .isCompleted(resultSet.getBoolean("isCompleted"))
                        .deadline(resultSet.getDate("deadline").toLocalDate())
                        .build();
                chores.add(chore);
            }
            return chores;
        } catch (SQLException exception) {
            System.out.println("Error when connecting to database.");
        } finally {
            closeConnections();
        }
        return null;
    }

    @Override
    public boolean save(List<Chore> chores) {
        return false;
    }

    private boolean connectToMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3307/db?"
                            + "user=rey&password=senha");
            return Boolean.TRUE;
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println("Error when connecting to database. Try again later");
        }
        return Boolean.FALSE;
    }

    private void closeConnections() {
        try {
            connection.close();
            statement.close();
//            preparedStatement.close();
            resultSet.close();
        } catch (SQLException exception) {
            System.out.println("Error when closing database connections.");
        }
    }

}
