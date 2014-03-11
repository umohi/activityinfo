package org.activityinfo.store.sql.server;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class JdbcStoreTest {


    @Test
    public void test() throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("create table if not exists instance (id text, class text, parent text, values text);");




    }

}
