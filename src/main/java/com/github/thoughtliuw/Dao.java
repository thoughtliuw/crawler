package com.github.thoughtliuw;

import java.sql.SQLException;

public interface Dao {
    void updateDatabase(String param1, String s) throws SQLException;

    String getNextLink() throws SQLException;

    boolean checkIfUrlIsParsed(String sql) throws SQLException;

    String getNextLinkAndDelete() throws SQLException;

    void storeNewsIntoDataBase(String url, String title, String content) throws SQLException;
}
