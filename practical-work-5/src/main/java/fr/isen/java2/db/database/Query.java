package fr.isen.java2.db.database;

public class Query {

    StringBuilder query;

    public String getQuery() {
        return query.toString();
    }

    public Query insert(String table, Object[] columns) {
        query = new StringBuilder();
        query.append(String.format("INSERT INTO %s(", table));
        if (columns != null) {
            for (Object column : columns) {
                query.append(column).append(",");
            }
            query.deleteCharAt(query.lastIndexOf(","));
            query.append(") ");
        }
        return this;
    }

    public Query select(Object[] columns) {
        query = new StringBuilder();
        query.append("SELECT ");
        if (columns != null) {
            for (Object column : columns) {
                query.append(column).append(",");
            }
            query.deleteCharAt(query.lastIndexOf(","));
        } else
            query.append("*");

        return this;
    }

    public Query delete(String table) {
        query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(table);
        return this;
    }


    public Query values(Object[] params) {
        query.append(" VALUES(");

        int count = params.length;

        if (count == 0)
            throw new IllegalArgumentException("Invalid parameter count");

        for (int i = 0; i < count; i++) {
            query.append("?,");
        }
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(");");
        return this;
    }

    public Query from(String table) {
        query.append(" FROM ");
        query.append(table);
        return this;
    }

    public Query where(String requirement) {
        query.append(" WHERE ");
        query.append(requirement);
        return this;
    }

    public Query end() {
        query.append(";");
        return this;
    }

    public Query joinOn(String tableName, String statement) {
        query.append(" JOIN ");
        query.append(tableName);
        query.append(" ON ");
        query.append(statement);
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(query);
    }
}