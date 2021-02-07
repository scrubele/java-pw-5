package fr.isen.java2.db.database;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseConnectorTest {

    private static final String TEST_QUERY = "select idfilm, title from film";

    @Test
    public void shouldConnect() {
        try (
                Connection connection = DatabaseConnector.getConnectionFromProps();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(TEST_QUERY)
        ) {
            assertThat(statement).isNotNull();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}