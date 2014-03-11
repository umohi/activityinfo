package org.activityinfo.store.sql.server;

import org.activityinfo.api2.shared.form.FormInstance;

import java.sql.*;

/**
 * Created by alex on 3/9/14.
 */
public class SqliteJdbcStore {

    private final Connection connection;

    public SqliteJdbcStore(String name) throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try(Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("create table if not exists instance (id text, class text, parent text, values text)");
        }
    }

    public void persist(FormInstance instance) throws SQLException {
        String insertStatement = "insert or replace into instance (id, class, parent, values) values (?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(insertStatement)) {
            stmt.setString(0, instance.getId().asString());
            stmt.setString(1, instance.getClassId().asString());
            stmt.setString(2, instance.getParentId().asString());
        }
    }

}
