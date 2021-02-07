package fr.isen.java2.db.daos;

import fr.isen.java2.db.database.DatabaseConnector;
import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


public class FilmDaoTestCase {

    private FilmDao filmDao = new FilmDao();
    private GenreDao genreDao = new GenreDao();

    @Before
    public void initDb() throws Exception {
//        Connection connection = DataSourceFactory.getDataSource().getConnection();
        Connection connection = DatabaseConnector.getConnectionFromProps();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS film (\r\n"
                        + "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
                        + "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
                        + "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
                        + "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
        stmt.executeUpdate("DELETE FROM film");
        stmt.executeUpdate("DELETE FROM genre");
        stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
        stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
        stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
                + "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
        stmt.close();
        connection.close();
    }

    @Test
    public void shouldListFilms() throws SQLException {
        List<Film> films = filmDao.listFilms();
        assertThat(films).hasSize(3);
        assertThat(films).extracting("id", "title").containsOnly(
                tuple(1, "Title 1"),
                tuple(2, "My Title 2"),
                tuple(3, "Third title")
        );
    }

    @Test
    public void shouldListFilmsByGenre() throws SQLException {
        Genre genre = genreDao.addGenre("New genre");
        List<Film> fIlms = new ArrayList<>(Arrays.asList(
                new Film(3, "Title 2", LocalDate.now(), genre, 120, "new director 1",
                        "summary of the first film"),
                new Film(5, "Title 3", LocalDate.now(), genre, 120, "new director 2",
                        "summary of the second film")
        ));
        List<Film> addedFilms = filmDao.addFilms(fIlms);
        System.out.println(addedFilms);
        assertThat(addedFilms.size()).isEqualTo(2);
        List<Film> receivedFilms = filmDao.listFilmsByGenre(genre.getName());
        System.out.println(receivedFilms);
        System.out.println(addedFilms);
        assertThat(receivedFilms.equals(addedFilms)).isTrue();
    }

    @Test
    public void shouldAddFilm() throws Exception {
        // WHEN
        Genre genre = new Genre(25, "New genre");
        Film fIlm = new Film(4, "Title 2", LocalDate.now(), genre, 120, "director 1", "summary of the first film");
        filmDao.addFilm(fIlm);
        // THEN
        Connection connection = DataSourceFactory.getDataSource().getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("SELECT * FROM film WHERE title='%s'", fIlm.getTitle());
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getInt("idfilm")).isNotNull();
        assertThat(resultSet.getString("title")).isEqualTo(fIlm.getTitle());
        assertThat(resultSet.next()).isFalse();
        resultSet.close();
        statement.close();
        connection.close();
    }

    @Test
    public void shouldGetById(){
        List<Film> films = filmDao.listFilms();
//        System.out.println(films);
        Film film = filmDao.getFilm(1);
//        System.out.println(film);
    }
}
