package com.nimble.data.jdbc;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Date: 5/8/13
 * Time: 1:52 PM
 */
public class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

    private final String sql;

    public SimplePreparedStatementCreator(String sql) {
        Assert.notNull(sql, "SQL must not be null");
        this.sql = sql;
    }

    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        return con.prepareStatement(this.sql);
    }

    public String getSql() {
        return this.sql;
    }
}
