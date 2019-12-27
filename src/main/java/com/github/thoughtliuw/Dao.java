package com.github.thoughtliuw;

import java.sql.SQLException;

public interface Dao {
    void insertLinksTobeProcessed(String url) throws SQLException;

    void insertLinksAlreadyProcessed(String url) throws SQLException;

    boolean checkIfUrlIsParsed(String sql) throws SQLException;

    String getNextLinkAndDelete() throws SQLException;

    void storeNewsIntoDataBase(String url, String title, String content) throws SQLException;
}
