package controller;
import java.sql.Connection;

public interface SqlConnection {
    public Connection connectToDatabase(); //establish connection to SQL db --> throws a SQL Exception
}
