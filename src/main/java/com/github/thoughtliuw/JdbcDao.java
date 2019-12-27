package com.github.thoughtliuw;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

public class JdbcDao implements Dao {

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private final Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcDao() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:file:./news", USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDatabase(String url, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();
        }
    }

    public String getNextLink() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from LINKS_TO_BE_PROCESSED limit 1");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("link");
            }
        }
        return null;
    }

    @Override
    public void insertLinksTobeProcessed(String url) throws SQLException {
        updateDatabase(url, "insert into LINKS_TO_BE_PROCESSED values(?)");
    }

    @Override
    public void insertLinksAlreadyProcessed(String url) throws SQLException {
        updateDatabase(url, "insert into LINKS_ALREADY_PROCESSED values(?)");
    }

    @Override
    public boolean checkIfUrlIsParsed(String sql) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, sql);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }

    @Override
    public String getNextLinkAndDelete() throws SQLException {
        String url = getNextLink();
        if (url != null) {
            updateDatabase(url, "delete from LINKS_TO_BE_PROCESSED where link = ?");
        }
        return url;
    }

    @Override
    public void storeNewsIntoDataBase(String url, String title, String content) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into news(title,content,url,createAt,updateAt) values (?,?,?,now(),now())")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, url);
            preparedStatement.executeUpdate();
        }
    }
}
