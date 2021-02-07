package fr.isen.java2.db.daos;

import fr.isen.java2.db.entities.Film;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class FilmDao {

    private final String tableName = "film";
    private final GenreDao genreDao = new GenreDao();
    private List<Film> films = new ArrayList<>();


    public List<Film> listFilms() {
        films = new ArrayList<>();
        String sqlQuery = String.format("select * from %s JOIN genre ON film.genre_id = genre.idgenre", tableName);
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Film film = this.getFilmFromResultSet(results);
                    films.add(film);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public List<Film> listFilmsByGenre(String genreName) {
        films = new ArrayList<>();
        String sqlQuery = String.format("SELECT * FROM %s JOIN genre ON film.genre_id = genre.idgenre " +
                "WHERE genre.name =?", tableName);
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, genreName);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Film film = this.getFilmFromResultSet(results);
                    films.add(film);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public Film addFilm(Film film) {
        String sqlQuery = String.format("insert into %s(title, release_date, genre_id, duration, director, " +
                "summary) VALUES(?,?,?,?,?,?)", tableName);
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, film.getTitle());
            statement.setDate(2, Date.valueOf(film.getReleaseDate()));
            statement.setInt(3, film.getGenre().getId());
            statement.setInt(4, film.getDuration());
            statement.setString(5, film.getDirector());
            statement.setString(6, film.getSummary());
            statement.executeUpdate();
            ResultSet ids = statement.getGeneratedKeys();
            if (ids.next()) {
                return this.getById(ids.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Film> addFilms(List<Film> films) {
        List<Film> resultList = new ArrayList<>();
        for (Film film : films) {
            resultList.add(this.addFilm(film));
        }
        return resultList;
    }

    public Film getById(Integer genreId) {
        String sqlQuery = String.format("select * from %s where idfilm=?", tableName);
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, genreId);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return this.getFilmFromResultSet(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Film getFilmFromResultSet(ResultSet results) throws SQLException {
        return new Film(
                results.getInt("idfilm"),
                results.getString("title"),
                results.getDate("release_date").toLocalDate(),
                genreDao.getById(results.getInt("genre_id")),
                results.getInt("duration"),
                results.getString("director"),
                results.getString("summary")
        );
    }
}
