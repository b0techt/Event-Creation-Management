package model;
import controller.*;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Model {
    private List<Tickets> tickets;
    private Admin admins;
    private List<User> users;
    private List<Events> events;

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

    public String getCurrentUser(String userName)throws FileNotFoundException, IOException{ //get current user if not create new user
        for(User user : users){
            if (user.getUserName().equals(userName)) {
                try (BufferedReader br = new BufferedReader(new FileReader(userName + ".txt"))) {
                    String line = br.readLine();
                    while(line != null){
                        System.out.println(line);
                    }
                }
            }
        }
        return "Please create a new user";
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

    public List<Tickets>getTickets(){
        return this.tickets;
    }

    public void addTicket(Tickets ticket){
        tickets.add(ticket);
    }

    public void addEvent(Events event){
        events.add(event);
    }

}
