package com.github.thoughtliuw;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            new Crawler().run();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
