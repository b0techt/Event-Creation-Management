package model;

import java.util.*;

public class Model {
    private final List<Tickets> tickets;
    private final Admin admins;
    private final List<User> users;
    private final List<Events> events;

    public Model(){
        this.admins = new Admin("", "");
        this.users = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    //users and admin meths 

    public List<User>getUsers(){ //only print for admin to see
        return this.users;
    }

    public Admin getAdmin(){ //get admin
        return this.admins;
    }

    public boolean isUserCreated(String userName){
        for(User use : getUsers()){
            if(use.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }

    public void addUser(User newUser){ //set new user
        this.users.add(newUser);
    }
    
    public void addAdmin(String adminName, String adminPass){ //set new admin
        this.admins.setAdminName(adminName);
        this.admins.setAdminPass(adminPass);
    }

    //tickets and events meths

    public List<Events>getEvents(){ //get list of events available to users, only see events created by other users
        return this.events;
    }

    public List<Tickets>getTickets(){ //get list of tickets available in the events
        return this.tickets;
    }

    public void addTicket(Tickets ticket){ //add tickets
        this.tickets.add(ticket);
    }

    public void addEvent(Events event){ //add new events
        this.events.add(event);
    }

    public void clearEvents(){ //clear events list to avoid duplication when printing
        this.events.clear();
    }
}
