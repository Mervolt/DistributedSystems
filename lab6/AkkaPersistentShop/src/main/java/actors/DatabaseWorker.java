package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.DatabaseResponse;
import persistence.PersistenceManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseWorker extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request ->{
                    Connection connection = PersistenceManager.getConnection();
                    ResultSet resultSet = queryDatabase(connection, prepareQuery(request));

                    assert resultSet != null;
                    if(resultSet.isClosed()) {
                        createRecord(connection, request);

                    }
                    else {
                        DatabaseResponse response = new DatabaseResponse(resultSet.getString("name"),
                                resultSet.getInt("timesQueried"));
                        getSender().tell(response, null);
                        context().child("databaseUpdateWorker").get().tell(request, getSelf());
                    }
                })
                .build();
    }

    private String prepareQuery(String request) {
        return "SELECT * FROM products WHERE products.name = " + "'" + request + "'";
    }

    private ResultSet queryDatabase(Connection connection, String query){
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            return null;
        }
    }

    private void createRecord(Connection connection, String productName) {
        String sql = "INSERT INTO products(name, timesQueried) VALUES (" + "'" + productName + "'" + ", 1)";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        context()
                .actorOf(Props.create(DatabaseUpdateWorker.class), "databaseUpdateWorker");

    }
}
