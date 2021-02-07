package fr.isen.java2.db.daos;

import fr.isen.java2.db.database.Database;
import fr.isen.java2.db.entities.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

    private final Database database = new Database();
    private final String tableName = "genre";
    private final Object[] columnNames = {"name"};
    List<Genre> genres = new ArrayList<>();

    public List<Genre> listGenres() {
        String sqlQuery = "select * from genre";
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
                    genres.add(genre);
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return genres;
    }

    public Genre getGenre(String name) {
        Object[] columnNames = null;
        Object[] params = {name};
        try (
                ResultSet results = database.select(tableName, columnNames, "name=?", params)
        ) {
            if (results.next()) {
                return this.getGenreFromResultSet(results);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Genre addGenre(String name) throws SQLException {
        Object[] values = {name};
        try (
                ResultSet ids = database.insert(tableName, columnNames, values)
        ) {
            if (ids.next()) {
                return this.getById(ids.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Genre getById(Integer genreId) {
        Object[] columnNames = null;
        Object[] params = {genreId};
        try (
                ResultSet results = database.select(tableName, columnNames, "idgenre=?", params)
        ) {
            if (results.next()) {
                return this.getGenreFromResultSet(results);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Genre getGenreFromResultSet(ResultSet results) throws SQLException {
        return new Genre(
                results.getInt("idgenre"),
                results.getString(String.valueOf(columnNames[0]))
        );
    }
}

