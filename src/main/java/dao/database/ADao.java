package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nullable;

import com.zaxxer.hikari.HikariDataSource;

/**
 * AutoCloseable declares to throw {@code Exception} that require to handle this broad exception
 * type. <br>
 * <br>
 * The goal with interface {@code SQLCloseable} is to override the {@code close} method (and throw
 * {@code SQLException} instead of {@code Exception}) to roll back the connection if necessary.
 */
@FunctionalInterface
interface SQLCloseable extends AutoCloseable {
    @Override
    public void close() throws SQLException;
}

public abstract class ADao<T> {

    protected HikariDataSource datasource = null;

    protected ADao(HikariDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Executes an INSERT statement.
     * 
     * @param obj The object to insert.
     * 
     * @return True if no errors occurs, otherwise false.
     */
    public abstract boolean create(T obj);

    /**
     * Executes a DELETE statement.
     * 
     * @param obj The object to delete.
     * 
     * @return True if no errors occurs, otherwise false.
     */
    public abstract boolean delete(T obj);

    /**
     * Executes an UPDATE statement.
     * 
     * @param obj The object to update.
     * 
     * @return True if no errors occurs, otherwise false.
     */
    public abstract boolean update(T obj);

    /**
     * Executes a SELECT statement.
     * 
     * @param id The id used as a filter.
     * 
     * @return An object filled by the values of the result set.
     */
    public abstract T find(Long... id);

    /**
     * If the value is SQL NULL, the value returned is 0. This method is used to replace 0 by null.
     * 
     * @param rs          The current result set.
     * @param columnLabel The name of the column.
     * 
     * @return A long value or null if the long value is equal to 0.
     * 
     * @throws SQLException If the columnLabel is not valid, if a database access error occurs or this
     *                      method is called on a closed result set.
     */
    @Nullable
    protected Long rsGetLong(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getLong(columnLabel) == 0 ? null : rs.getLong(columnLabel);
    }

    /**
     * Send a long value or null. <br>
     * {@code setLong} method only accepts a primitive long value, null is not allowed. This method fix
     * that behavior by swapping between {@code setLong} and {@code setNull} methods.
     * 
     * @param parameterIndex The first parameter is 1, the second is 2, etc.
     * @param pst            The current prepared statement.
     * @param id             The id.
     * 
     * @throws SQLException If parameterIndex does not correspond to a parameter marker in the SQL
     *                      statement, if a database access error occurs or this method is called on a
     *                      closed PreparedStatement.
     */
    protected void setNLong(int parameterIndex, PreparedStatement pst, Long id) throws SQLException {
        if (id == null) {
            pst.setNull(parameterIndex, java.sql.Types.BIGINT);
        } else {
            pst.setLong(parameterIndex, id);
        }
    }

}