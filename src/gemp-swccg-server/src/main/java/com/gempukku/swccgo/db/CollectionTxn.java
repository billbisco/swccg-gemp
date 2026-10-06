package com.gempukku.swccgo.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * One JDBC transaction for a collection save plus its transfer rows.
 * Nested calls join the current connection.
 */
public final class CollectionTxn {
    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<Connection>();

    private CollectionTxn() {
    }

    public static void run(DbAccess dbAccess, Runnable work) {
        if (CURRENT.get() != null) {
            work.run();
            return;
        }
        Connection connection;
        try {
            connection = dbAccess.getDataSource().getConnection();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to open collection transaction", exp);
        }
        CURRENT.set(connection);
        try {
            connection.setAutoCommit(false);
            work.run();
            connection.commit();
        } catch (RuntimeException exp) {
            rollbackQuietly(connection);
            throw exp;
        } catch (SQLException exp) {
            rollbackQuietly(connection);
            throw new RuntimeException("Unable to commit collection transaction", exp);
        } catch (Error exp) {
            rollbackQuietly(connection);
            throw exp;
        } finally {
            CURRENT.remove();
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static Connection currentOrNew(DbAccess dbAccess) throws SQLException {
        Connection current = CURRENT.get();
        if (current != null)
            return current;
        return dbAccess.getDataSource().getConnection();
    }

    public static boolean isCurrent(Connection connection) {
        return connection != null && connection == CURRENT.get();
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }
}
