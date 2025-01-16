package model;

import java.util.*;

public class Model {
    private List<Tickets> tickets;
    private Admin admins;
    private List<User> users;
    private List<Events> events;
    private List<Events>unapprovedEvents;

    public Model(){
        this.admins = new Admin("", "");
        this.users = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.events = new ArrayList<>();
        this.unapprovedEvents = new ArrayList<>();
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
        users.add(newUser);
    }
    
    public void addAdmin(String adminName, String adminPass){ //set new admin
        admins.setAdminName(adminName);
        admins.setAdminPass(adminPass);
    }

    //tickets and events meths

    public List<Events>getEvents(){ //get list of events available to users, only see events created by other users
        return this.events;
    }

    public List<Events>getUnapprovedEvents(){
        for(Events e : getEvents()){
            if(e.getEventStatus() == 0){
                unapprovedEvents.add(e);
            }
        }
        return unapprovedEvents;
    }

    public List<Tickets>getTickets(){ //get list of tickets available in the events
        return this.tickets;
    }

    public void addTicket(Tickets ticket){ //add tickets
        tickets.add(ticket);
    }

    public void addEvent(Events event){ //add new events
        events.add(event);
    }

}
