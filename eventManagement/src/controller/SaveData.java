package controller;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Events;
import model.Model;
import model.User;

public class SaveData implements SqlConnection {
    private Model users;
    private final String ul = "eventManagement\\db\\listOfUsers.txt";
    private final ConfigSQL sql = new ConfigSQL();
    private BufferedWriter bw;
    private boolean append;
    
    @Override
    public Connection connectToDatabase(){
        try {
            return DriverManager.getConnection(sql.getUrl(), sql.getDBuser(), sql.getDBpassword());
        } catch (SQLException sqle) {
            return null;
        }
    }

    public void getCurrentUserForEdits(String userName)throws FileNotFoundException, IOException{ //get current user if not create new user
        for(User user : users.getUsers()){
            if (user.getUserName().equals(userName)) {
                
            }
        }
    }

    public String saveUser(String userName){
        try {
            append = false;
            try(BufferedReader br = new BufferedReader(new FileReader(ul))){
                append = br.readLine() != null;
            }

            bw = new BufferedWriter(new FileWriter(ul, append));
            if(append){
                bw.newLine();
            }
            bw.write(userName);
            bw.close();
        } catch (IOException ioe) {
            System.err.println("Cannot save user. Will attempt again on application exit.\n");
        }
        return "Username saved.";
    }

    @Override
    public void userIntoDB(String userName){
        try(Connection connect = connectToDatabase()){
            System.out.println("Connection established.");
            String query = "INSERT INTO schema_events.users(username) VALUES (?)";
            try(PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                stmt.setString(1,userName);
                stmt.executeUpdate();
                System.out.println("Username saved to the database.");
            }
        }catch(SQLException | NullPointerException e){
            System.err.println("Could not save User account. " + e.getMessage());
        }
    }

    public List<User>listOfUserNames()throws IOException{ //helps load users for the admin to see
        List<User>usernames = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(ul))){
            String line;
            while((line = br.readLine()) != null){
                usernames.add(new User(line));
            }
            return usernames;
        }
    }

    @Override
    public void saveUserEvent(Events event, String userData){ //insert event data to database
        try(Connection connect = connectToDatabase()){
           String query = "INSERT INTO Schema_Events.EventsInfo(Usernames, EventName, EventDesc, EventDate, EventTime, EventLocation, Status)" 
           + "VALUES(?,?,?,?,?,?,?)";

           try(PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
               stmt.setString(1, userData);
               stmt.setString(2, event.getEventName());
               stmt.setString(3, event.getEventDescription());
               stmt.setString(4, event.getEventDate());
               stmt.setString(5, event.getEventTime());
               stmt.setString(6, event.getEventLocation());
               stmt.setString(7, event.statusString());

               stmt.executeUpdate();
               System.out.println("Event has been saved.");
           }
        }catch(SQLException | NullPointerException e){
            System.err.println("Could not save event to User file.");
        }
    }
}
