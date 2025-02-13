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
        try(Connection connect = conToDB()){
            String query = "SELECT * FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id";
            try(PreparedStatement ps = connect.prepareStatement(query)){
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
            try(PreparedStatement stmt = connect.prepareStatement(query)){
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

    public List<Tickets>getUserTickets(String userName, Events event){
        List<Tickets>userTickets = new ArrayList<>();
        try(Connection connect = conToDB()){
            String query = "SELECT * FROM schema_events.tickets t JOIN schema_events.users u ON t.user_id = u.id JOIN schema_events.eventsinfo e ON t.event_id = e.id WHERE u.username = ? AND e.eventname = ?";
            try(PreparedStatement ps = connect.prepareStatement(query)){
                ps.setString(1, userName);
                ps.setString(2, event.getEventName());
                ResultSet set = ps.executeQuery();
                while(set.next()){
                    Tickets ticket = new Tickets(set.getString("ticktype"), set.getDouble("ticketprice"), set.getInt("curr_aval"), set.getInt("max_aval"));
                    userTickets.add(ticket);
                }
            }
        }catch(SQLException e){
            System.err.println("Could not fetch data. Error occurred: "+e.getMessage());
        }
        return userTickets;
    }

    @Override
    public void adminApprove(Events event, String userName, String coninfo, int status){ //admin control to either approve or reject event.
        try(Connection connect = conToDB()){
            String query = "UPDATE schema_events.eventsinfo e SET status = ?, statusinfo = ? "  
            +"FROM schema_events.users u " 
            +"WHERE e.user_id = u.id AND u.username = ? AND e.eventname = ?";
            try(PreparedStatement ps = connect.prepareStatement(query)){
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
        try(Connection connect = conToDB()){
            String query = "SELECT statusinfo FROM schema_events.eventsinfo e " 
            +"JOIN schema_events.users u ON e.user_id = u.id WHERE u.username = ? AND e.eventname = ?";
            try(PreparedStatement ps = connect.prepareStatement(query)){
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
        try(Connection connect = conToDB()){
            switch (choice) {
                case 1 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventname=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; eventUpdateMethod(connect, query, event.getEventName(), userName, event.getEventName());}
                case 2 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventdesc=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; eventUpdateMethod(connect, query, event.getEventDescription(), userName, event.getEventName());}
                case 3 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventdate=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; eventUpdateMethod(connect, query, event.getEventDate(), userName, event.getEventName());}
                case 4 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventtime=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; eventUpdateMethod(connect, query, event.getEventTime(), userName, event.getEventName());}
                case 5 -> { String query = "UPDATE schema_events.eventsinfo e SET status=?, eventlocation=? FROM schema_events.users u WHERE e.user_id = u.id AND u.username=? AND e.eventname=?"; eventUpdateMethod(connect, query, event.getEventLocation(), userName, event.getEventName());}
            }
        }catch(SQLException e){
            System.err.println("Was not able to update data. Error occurred: "+e.getMessage());
        }
    }

    private void eventUpdateMethod(Connection connect, String query, String eventCol, String userName, String evName){ //prepared statement method to reduce multiple methods
        try(PreparedStatement ps = connect.prepareStatement(query)){ //update set st? evd? where un? & en?
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
        try(Connection connect = conToDB()){
            String query = "INSERT INTO schema_events.tickets(ticktype, ticketprice, curr_aval, max_aval, event_id, user_id) " +
            "SELECT ?,?,?,?, e.id, u.id "+
            "FROM schema_events.users u JOIN schema_events.eventsinfo e ON e.user_id = u.id WHERE e.eventname=? AND u.username=?";
            try(PreparedStatement ps = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
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

    @Override
    public void editTicket(Tickets ticket, Events event, String userName, int col){ //had to split query into two statement because Postgres does not support update of multiple tables at once.
        String mainQuery="";
        String subQuery="";
        try(Connection connect = conToDB()){
            connect.setAutoCommit(false);
            switch(col){
                case 1 -> { mainQuery = "UPDATE schema_events.eventsinfo e SET status=? FROM schema_events.users u WHERE e.user_id = u.id AND e.eventname=? AND u.username=?"; 
                            subQuery = "UPDATE schema_events.tickets t SET ticktype=? FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE t.event_id = e.id AND e.eventname=? AND u.username=? "; }
                case 2 -> { mainQuery = "UPDATE schema_events.eventsinfo e SET status=? FROM schema_events.users u WHERE e.user_id = u.id AND e.eventname=? AND u.username=?"; 
                            subQuery = "UPDATE schema_events.tickets t SET ticketprice=? FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE t.event_id = e.id AND e.eventname=? AND u.username=? "; }
                case 3 -> { mainQuery = "UPDATE schema_events.eventsinfo e SET status=? FROM schema_events.users u WHERE e.user_id = u.id AND e.eventname=? AND u.username=?"; 
                            subQuery = "UPDATE schema_events.tickets t SET curr_aval=? FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE t.event_id = e.id AND e.eventname=? AND u.username=? "; }
                case 4 -> { mainQuery = "UPDATE schema_events.eventsinfo e SET status=? FROM schema_events.users u WHERE e.user_id = u.id AND e.eventname=? AND u.username=?"; 
                            subQuery = "UPDATE schema_events.tickets t SET max_aval=? FROM schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE t.event_id = e.id AND e.eventname=? AND u.username=? "; }
            }
            ticketUpdateMethod(connect, mainQuery, subQuery, ticket, event, userName, col);
        }catch(SQLException e){
            System.err.println("Was not able to edit Ticket admission.");
        }
    }

    private void ticketUpdateMethod(Connection connect, String mainQuery, String subQuery, Tickets ticket, Events event, String userName, int column){
        eventUpdateFromTicket(connect, mainQuery, event, userName); //update eventsinfo first

        try(PreparedStatement ps = connect.prepareStatement(subQuery)){
            switch(column){
                case 1 -> { ps.setString(1, ticket.getTicketType());}
                case 2 -> { ps.setDouble(1, ticket.getTicketPrice()); }
                case 3 -> { ps.setInt(1, ticket.getAvailability()); }
                case 4 -> { ps.setInt(1, ticket.getMaxAvailability()); }
            }

            ps.setString(2, event.getEventName());
            ps.setString(3, userName);
            ps.executeUpdate();

            connect.commit();

            System.out.println("Ticket has been edited.");
        }catch(SQLException e){
            System.err.println("Was not able to edit Ticket. Error occurred: "+e.getMessage());
            try{
                connect.rollback();
            }catch(SQLException roll){
                System.err.println("Reverting changes. Error occurred: "+roll.getMessage());
            }
            
        }
    }

    private void eventUpdateFromTicket(Connection connect, String query, Events event, String userName){
        try(PreparedStatement ps = connect.prepareStatement(query)){
            ps.setString(1, "Unapproved");
            ps.setString(2, event.getEventName());
            ps.setString(3, userName);
            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println("Could not update Ticketed Event. Error occurred: "+e.getMessage());
        }
    }

    @Override
    public void deleteTicket(Events event, Tickets ticket, String userName){
        Connection connect = conToDB();
        try{
            String query = "DELETE FROM schema_events.tickets t USING schema_events.eventsinfo e JOIN schema_events.users u ON e.user_id = u.id WHERE t.event_id = e.id AND e.eventname=? AND u.username=? AND t.ticktype=?";
            try(PreparedStatement ps = connect.prepareStatement(query)){
                ps.setString(1, event.getEventName());
                ps.setString(2, userName);
                ps.setString(3, ticket.getTicketType());
                connect.setAutoCommit(false);

                int deletedRow = ps.executeUpdate();
                if(deletedRow > 0){
                    System.out.println("Ticket has been deleted.");
                }

                connect.commit();
                connect.close();
            }
        }catch(SQLException e){
            System.err.println("Could not delete ticket. Error occurred: "+e.getMessage());
            try{
                connect.rollback();
            }catch(SQLException roll){
                System.err.println("Reverting changes. Error occurred: "+roll.getMessage());
            }
        }
    }
}