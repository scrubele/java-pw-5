package fr.isen.java2.db.daos;

import fr.isen.java2.db.entities.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

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
        String sqlQuery = "select * from genre where name=?";
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Genre addGenre(String name) throws SQLException {
        String sqlQuery = "insert into genre(name) VALUES(?)";
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, name);
            statement.executeUpdate();
            ResultSet ids = statement.getGeneratedKeys();
            if (ids.next()) {
                return new Genre(ids.getInt(1), name);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Genre getById(Integer genreId) {
        String sqlQuery = "select * from genre where idgenre=?";
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, genreId);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new Genre(results.getInt("idgenre"), results.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

