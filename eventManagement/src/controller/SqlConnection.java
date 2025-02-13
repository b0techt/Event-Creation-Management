package controller;
import java.sql.Connection;
import model.Events;
import model.Tickets;

public interface SqlConnection { //sql connection interface which has method that SaveData.java uses and overrides  
    public Connection conToDB(); //establish connection to SQL db --> throws a SQL Exception
    public void userIntoDB(String userName);
    public void saveUserEvent(Events event, String userName);
    public void adminApprove(Events event, String userName, String coninfo, int status);
    public void updateEvent(Events event, String eventChange, String userName, int choice);
    public String adminFeedback(String eventName, String userName);
    public void saveTicket(Tickets ticket, Events event, String userName);
    public void editTicket(Tickets ticket, Events event, String userName, int col);
    public void deleteTicket(Events event, Tickets ticket, String userName);
}
