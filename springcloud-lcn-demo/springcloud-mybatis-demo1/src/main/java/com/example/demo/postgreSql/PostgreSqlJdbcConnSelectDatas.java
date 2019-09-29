package com.example.demo.postgreSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class PostgreSqlJdbcConnSelectDatas {
    public static void main(String args[]) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/test", "postgres",
                    "123456");
            c.setAutoCommit(false);

            System.out.println("连接数据库成功！");
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("select * from company02");
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                float salary = rs.getFloat("salary");
                System.out.println(id + "," + name + "," + age + "," + address.trim() + "," + salary);
            }

            rs.close();
            stmt.close();

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("查询数据成功！");
    }
}
