package persistence;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.sql.*;

public class PersistenceManager {
    private static String dbUrl = "jdbc:sqlite:C:/SQLite/AkkaShop.db";
    private static String productsTable = "products";
    private static Connection connection;

    public static Connection getConnection(){
        return connection;
    }

    public static void setConnection(){
        try{
            SQLiteConfig config = new SQLiteConfig();
            config.setOpenMode(SQLiteOpenMode.FULLMUTEX);
            connection = DriverManager.getConnection(dbUrl, config.toProperties());
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void createDatabaseIfNotExist() {
        try{
            Connection connection = DriverManager.getConnection(dbUrl);
            if(connection != null){
                createProductsTableIfNotExist(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createProductsTableIfNotExist(Connection connection) {
        String sql = "CREATE TABLE IF NOT EXISTS " + productsTable + "(\n"
                + "name varchar(255) PRIMARY KEY, \n"
                + "timesQueried integer)";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
