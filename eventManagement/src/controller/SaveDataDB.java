package controller;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;
import model.Events;
import model.Tickets;
/*
 * This calls implements an interface which uses methods to query the Postgres DB
 */

public class SaveDataDB implements SqlConnection {
    private final ConfigSQL sql = new ConfigSQL();
    
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
    public void saveUserEvent(Events event, String userName){ //insert event data to database
        try(Connection connect = conToDB()){
           String query = "INSERT INTO schema_events.eventsinfo(EventName, EventDesc, EventDate, EventTime, EventLocation, Status, User_Id) " 
           + "SELECT ?,?,?,?,?,?, u.id "
           +"FROM schema_events.users u WHERE u.username=?";

           try(PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
               stmt.setString(1, event.getEventName());
               stmt.setString(2, event.getEventDescription());
               stmt.setString(3, event.getEventDate());
               stmt.setString(4, event.getEventTime());
               stmt.setString(5, event.getEventLocation());
               stmt.setString(6, event.statusString());
               stmt.setString(7, userName);

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
            String query = "SELECT * FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id";
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
            String query = "SELECT * FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE username = ?";
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
            String query = "UPDATE schema_events.eventsinfo e SET status = ?, statusinfo = ? "  
            +"FROM schema_events.users u " 
            +"WHERE e.user_id = u.id AND u.username = ? AND e.eventname = ?";
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
            String query = "SELECT statusinfo FROM schema_events.eventsinfo e " 
            +"JOIN schema_events.users u ON e.user_id = u.id WHERE u.username = ? AND e.eventname = ?";
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
        if(adminInfo == null){
            return "Admin has not approved event yet. No feedback available.";
        }
        return adminInfo;
    } 
    
    @Override
    public void updateEvent(Events event, String userName, String eventChange, int choice){ //update user event using switch statement
        try(Connection conn = conToDB()){
            switch (choice) {
                case 1 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventname=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; prepareStatement(conn, query, event.getEventName(), userName, event.getEventName());}
                case 2 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventdesc=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; prepareStatement(conn, query, event.getEventDescription(), userName, event.getEventName());}
                case 3 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventdate=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; prepareStatement(conn, query, event.getEventDate(), userName, event.getEventName());}
                case 4 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventtime=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; prepareStatement(conn, query, event.getEventTime(), userName, event.getEventName());}
                case 5 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventlocation=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; prepareStatement(conn, query, event.getEventLocation(), userName, event.getEventName());}
            }
        }catch(SQLException e){
            System.err.println("Was not able to update data. Error occurred: "+e.getMessage());
        }
    }

    private void prepareStatement(Connection connect, String query, String eventCol, String userName, String evName){ //prepared statement method to reduce multiple methods
        try(PreparedStatement ps = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){ //update set st? evd? where un? & en?
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

    @Override
    public void saveTicket(Tickets ticket, Events event, String userName){
        try(Connection conn = conToDB()){
            String query = "INSERT INTO schema_events.tickets(ticktype, ticketprice, curr_aval, max_aval, event_id, user_id) " +
            "SELECT ?,?,?,?, e.id, u.id "+
            "FROM schema_events.users u JOIN schema_events.eventsinfo e ON e.user_id = u.id WHERE e.eventname=? AND u.username=?";
            try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1,ticket.getTicketType());
                ps.setDouble(2, ticket.getTicketPrice());
                ps.setInt(3, ticket.getAvailability());
                ps.setInt(4, ticket.getMaxAvailability());
                ps.setString(5, event.getEventName());
                ps.setString(6, userName);
                ps.executeUpdate();
                System.out.println("Ticket admission saved.");
            }
        }catch(SQLException e){
            System.err.println("Was not able to save Ticket rollout. Error occurred: "+e.getMessage());
        }
    }
}
