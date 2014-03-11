package org.activityinfo.store.sql.server;

import org.activityinfo.api2.shared.form.FormInstance;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Instance store backed by a JDBC-accessible SQL database
 */
public class JdbcStore {

    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }


}
