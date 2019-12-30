package com.github.thoughtliuw;

import com.github.thoughtliuw.mybatisDao.MybatisDao;

public class Main {
    public static void main(String[] args) {
        Dao dao = new MybatisDao();

        for (int i = 0; i < 10; i++) {
            new Thread(new Crawler(dao)).start();
        }
    }
}
