package fr.isen.java2.db.database;


import java.sql.*;


public class Database {

    protected Connection connection;

    protected Query query;


    public Database() {
        connection = DatabaseConnector.getConnectionFromProps();
    }

    private ResultSet query(String query, Object[] parameters) throws SQLException {
        PreparedStatement statement = this.formPreparedStatement(query, parameters);
        statement.executeUpdate();
        return statement.getGeneratedKeys();
    }

    public ResultSet insert(String table, Object[] params, Object[] values) throws SQLException {
        query = new Query();
        query.insert(table, params)
                .values(values);
        return query(query.getQuery(), values);
    }

    public ResultSet delete(String table, String requirement, Object[] param) throws SQLException {
        query = new Query();
        query.delete(table)
                .where(requirement);
        return query(query.getQuery(), param);
    }

    public ResultSet select(String table, Object[] columns) throws SQLException {
        query = new Query();
        query.select(columns)
                .from(table)
                .end();
        System.out.println(query);
        PreparedStatement statement = this.formPreparedStatement(query.getQuery(), null);
        return statement.executeQuery();
    }

    public ResultSet select(String table,
                            Object[] columns,
                            String tableName,
                            String joinStatement) throws SQLException {
        query = new Query();
        query.select(columns)
                .from(table)
                .joinOn(tableName, joinStatement);
        PreparedStatement statement = this.formPreparedStatement(query.getQuery(), null);
        return statement.executeQuery();
    }

    public ResultSet select(String table, Object[] columns, String requirement, Object[] params) throws SQLException {
        query = new Query();
        query.select(columns)
                .from(table)
                .where(requirement);
        PreparedStatement statement = this.formPreparedStatement(query.getQuery(), params);
        return statement.executeQuery();
    }

    public ResultSet select(String table, Object[] columns, String tableName, String joinStatement, String requirement,
                            Object[] params) throws SQLException {
        query = new Query();
        query.select(columns)
                .from(table)
                .joinOn(tableName, joinStatement)
                .where(requirement);
        PreparedStatement statement = this.formPreparedStatement(query.getQuery(), params);
        return statement.executeQuery();
    }

    public PreparedStatement formPreparedStatement(String query, Object[] params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        if (params != null) {
            int index = 1;
            for (Object param : params) {
                statement.setObject(index, param);
                index++;
            }
        }
        return statement;
    }
}
