package database;

public class DBTester{

    public static void main(String[] args) {

        try (var connection = database.DBConnector.getConnection()) {
            if (connection != null) {
                System.out.println("Connection to the database was successful!");
            }
        } catch (Exception e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());

        }}}
