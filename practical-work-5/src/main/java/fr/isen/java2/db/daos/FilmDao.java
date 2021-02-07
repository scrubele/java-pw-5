package fr.isen.java2.db.daos;

import fr.isen.java2.db.database.Database;
import fr.isen.java2.db.entities.Film;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmDao {

    private final Database database = new Database();
    private final String tableName = "film";
    private final Object[] columnNames = {"title", "release_date", "genre_id", "duration", "director", "summary"};
    private final GenreDao genreDao = new GenreDao();
    private List<Film> films = new ArrayList<>();

    public List<Film> listFilms() {
        films = new ArrayList<>();
        try (
                ResultSet results = database.select(tableName, null, "genre",
                        "film.genre_id = genre.idgenre")
        ) {
            while (results.next()) {
                Film film = this.getFilmFromResultSet(results);
                films.add(film);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return films;
    }

    public List<Film> listFilmsByGenre(String genreName) {
        films = new ArrayList<>();
        Object[] params = {genreName};
        try (
                ResultSet results = database.select(tableName, null, "genre",
                        "film.genre_id = genre.idgenre", "genre.name=?", params)
        ) {
            while (results.next()) {
                Film film = this.getFilmFromResultSet(results);
                films.add(film);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return films;
    }

    public Film addFilm(Film film) {
        Object[] values = {
                film.getTitle(), Date.valueOf(film.getReleaseDate()), film.getGenre().getId(), film.getDuration(),
                film.getDirector(), film.getSummary()
        };
        try (
                ResultSet ids = database.insert(tableName, columnNames, values)
        ) {
            if (ids.next()) {
                return this.getFilm(ids.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

    public Film getFilm(Integer id) {
        Object[] columnNames = null;
        Object[] params = {id};
        try (
                ResultSet results = database.select(tableName, columnNames, "idfilm=?", params)
        ) {

            if (results.next()) {
                return this.getFilmFromResultSet(results);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Film getFilmFromResultSet(ResultSet results) throws SQLException {
        return new Film(
                results.getInt("idfilm"),
                results.getString(String.valueOf(columnNames[0])),
                results.getDate(String.valueOf(columnNames[1])).toLocalDate(),
                genreDao.getById(results.getInt(String.valueOf(columnNames[2]))),
                results.getInt(String.valueOf(columnNames[3])),
                results.getString(String.valueOf(columnNames[4])),
                results.getString(String.valueOf(columnNames[5]))
        );
    }
}
