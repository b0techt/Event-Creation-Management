package controller;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Events;
import model.User;

public class SaveData implements SqlConnection {
    private final String ul = "eventManagement\\db\\listOfUsers.txt";
    private final ConfigSQL sql = new ConfigSQL();
    private BufferedWriter bw;
    private boolean append;
    
    @Override
    public Connection conToDB(){
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(sql.getUrl(), sql.getDBuser(), sql.getDBpassword());
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error occurred: "+e.getMessage());            
            System.err.println(System.getProperty("java.class.path"));
            System.exit(0);
            return null;
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
    public void userIntoDB(String userName){
        try(Connection connect = conToDB()){
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

    @Override
    public void saveUserEvent(Events event, String userData){ //insert event data to database
        try(Connection connect = conToDB()){
           String query = "INSERT INTO schema_events.eventsinfo(Username, EventName, EventDesc, EventDate, EventTime, EventLocation, Status)" 
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
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Could not save event to User file.");
        }
    }

    public List<Events>getAllEvents(){
        List<Events>unapprovedEvents = new ArrayList<>();
        try(Connection conn = conToDB()){
            String query = "SELECT * FROM schema_events.eventsinfo";
            try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                ResultSet set = ps.executeQuery();
                while(set.next()){
                    Events ev = new Events(set.getString("eventname"),
                    set.getString("eventdesc"),
                    set.getString("eventdate"),
                    set.getString("eventtime"),
                    set.getString("eventlocation"), new User(set.getString("username")));

                    switch (set.getString("status")) {
                        case "Unapproved" -> ev.setEventStatus(0);
                        case "Approved" -> ev.setEventStatus(1);
                        case "Rejected" -> ev.setEventStatus(2);
                        default -> {}
                    }

                    unapprovedEvents.add(ev);
                }
            }
        }catch(SQLException e ){
            System.err.println("Could not connec to database. Error occurred: "+e.getMessage());
        }
        return unapprovedEvents;
    }

    public List<Events>getUserEvents(String userName){
        List<Events>userEvents = new ArrayList<>();
        try(Connection connect = conToDB()){
            String query = "SELECT * FROM schema_events.eventsinfo WHERE username = ?";
            try(PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                stmt.setString(1, userName);
                ResultSet set = stmt.executeQuery();
                while(set.next()){
                    Events ev = new Events(set.getString("eventname"),
                    set.getString("eventdesc"),
                    set.getString("eventdate"),
                    set.getString("eventtime"),
                    set.getString("eventlocation"), new User(set.getString("username")));

                    switch (set.getString("status")) {
                        case "Unapproved" -> ev.setEventStatus(0);
                        case "Approved" -> ev.setEventStatus(1);
                        case "Rejected" -> ev.setEventStatus(2);
                        default -> {}
                    }
                    
                    userEvents.add(ev);
                }
            }
        }catch(SQLException e){
            System.err.println("Could not fetch data. Error occurred: "+e.getMessage());
        }
        return userEvents;
    }

    @Override
    public void adminApprove(Events event, String userName, String coninfo, int status){ //admin control to either approve or reject event.
        try(Connection conn = conToDB()){
            String query = "UPDATE schema_events.eventsinfo SET status = ?, statusinfo = ? WHERE username = ? AND eventname = ?";
            try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                switch(status){
                    case 1 -> ps.setString(1,"Approved");
                    case 2 -> ps.setString(1,"Rejected");
                }
                ps.setString(2, coninfo);
                ps.setString(3, userName);
                ps.setString(4, event.getEventName());
                ps.executeUpdate();

                event.setEventStatus(status);
                System.out.println("Event has been "+event.statusString());
            }
        }catch(SQLException e){
            System.err.println("Was not able to update data. Error occurred: "+e.getMessage());
        }
    }

    @Override
    public String adminFeedback(String eventName, String userName){ //gets admin feedback from the database
        String adminInfo = "";
        try(Connection conn = conToDB()){
            String query = "SELECT statusinfo FROM schema_events.eventsinfo WHERE username = ? AND eventname = ?";
            try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, userName);
                ps.setString(2, eventName);
                ResultSet set = ps.executeQuery();
                while(set.next()){
                    adminInfo = set.getString("statusinfo");
                }
            }
        }catch(SQLException e){
            System.err.println("Information could not be retrieved. Error occurred: "+e.getMessage());
        }
        return adminInfo;
    } 
    
    @Override
    public void updateEvent(Events event, String userName, String eventChange, int choice){ //update user event using switch statement
        try(Connection conn = conToDB()){
            switch (choice) {
                case 1 -> { String query = "UPDATE schema_events.eventsinfo SET status=?, eventname=? WHERE username=? AND eventname=?"; prepareStatement(conn, query, eventChange, userName, event.getEventName());}
                case 2 -> { String query = "UPDATE schema_events.eventsinfo SET status=?, eventdesc=? WHERE username=? AND eventname=?"; prepareStatement(conn, query, eventChange, userName, event.getEventName());}
                case 3 -> { String query = "UPDATE schema_events.eventsinfo SET status=?, eventdate=? WHERE username=? AND eventname=?"; prepareStatement(conn, query, eventChange, userName, event.getEventName());}
                case 4 -> { String query = "UPDATE schema_events.eventsinfo SET status=?, eventtime=? WHERE username=? AND eventname=?"; prepareStatement(conn, query, eventChange, userName, event.getEventName());}
                case 5 -> { String query = "UPDATE schema_events.eventsinfo SET status=?, eventlocation=? WHERE username=? AND eventname=?"; prepareStatement(conn, query, eventChange, userName, event.getEventName());}
            }
            //event.setEventStatus(0);
        }catch(SQLException e){
            System.err.println("Was not able to update data. Error occurred: "+e.getMessage());
        }
    }

    private void prepareStatement(Connection connect, String query, String eventCol, String userName, String evName){ //statement method to reduce multiple methods
        try(PreparedStatement ps = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){ //update set st? evd? where un?
            ps.setString(1, "Unapproved");
            ps.setString(2, eventCol);
            ps.setString(3, userName);
            ps.setString(4, evName);
            ps.executeUpdate();
            System.out.println("Event details has been updated.");
        }catch(SQLException e){
            System.err.println("Was not able to update data. Error occurred: "+e.getMessage());
        }
    }
}
