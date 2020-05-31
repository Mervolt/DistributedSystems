package actors;

import akka.actor.AbstractActor;
import persistence.PersistenceManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpdateWorker extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {
                    Connection connection = PersistenceManager.getConnection();
                    updateRecord(connection, request);
                })
                .build();
    }

    private void updateRecord(Connection connection, String productName) {
        int targetTimes = -1;
        try {
            String query = prepareQuery(productName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int queried = resultSet.getInt("timesQueried");
            targetTimes = queried + 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (targetTimes != -1) {
            String sql = "UPDATE products SET timesQueried = " + targetTimes
                    + " WHERE name = " + "'" + productName + "'";
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private String prepareQuery(String productName) {
        return "SELECT * FROM products WHERE products.name = " + "'" + productName + "'";
    }
}
