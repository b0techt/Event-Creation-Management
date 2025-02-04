package controller;
import java.sql.Connection;
import model.Events;

public interface SqlConnection {
    public Connection conToDB(); //establish connection to SQL db --> throws a SQL Exception
    public void userIntoDB(String userName);
    public void saveUserEvent(Events event, String userName);
    public void adminApprove(Events event, String userName, String coninfo, int status);
    public void updateEvent(Events event, String eventChange, String userName, int choice);
    public String adminFeedback(String eventName, String userName);
}
