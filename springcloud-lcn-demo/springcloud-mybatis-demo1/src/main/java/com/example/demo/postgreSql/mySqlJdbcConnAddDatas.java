package com.example.demo.postgreSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class mySqlJdbcConnAddDatas {
    public static void main(String args[]) {
        Long startTime = System.currentTimeMillis();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/test?useSSL=false", "root",
                    "123456");
            c.setAutoCommit(false);

            System.out.println("连接数据库成功！");
            stmt = c.createStatement();

            for (int i = 0; i < 10000; i++) {
                String sql = "INSERT INTO COMPANY02 (NAME,AGE,ADDRESS,SALARY) "
                        + "VALUES ('Paul', 32, 'California', 20000.00 );";
                stmt.executeUpdate(sql);

                sql = "INSERT INTO COMPANY02 (NAME,AGE,ADDRESS,SALARY) "
                        + "VALUES ('Allen', 25, 'Texas', 15000.00 );";
                stmt.executeUpdate(sql);

                sql = "INSERT INTO COMPANY02 (NAME,AGE,ADDRESS,SALARY) "
                        + "VALUES ( 'Teddy', 23, 'Norway', 20000.00 );";
                stmt.executeUpdate(sql);

                sql = "INSERT INTO COMPANY02 (NAME,AGE,ADDRESS,SALARY) "
                        + "VALUES ('Mark', 25, 'Rich-Mond ', 65000.00 );";
                stmt.executeUpdate(sql);
            }
            stmt.close();
            c.commit();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(" 方法执行耗时：" + (endTime - startTime) + " ms");
    }
}
