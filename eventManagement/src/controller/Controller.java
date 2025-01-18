package controller;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import model.*;
import view.View;

public class Controller  {
    private final Scanner input = new Scanner(System.in);
    private Model model;
    private View view;
    private final SaveData saveData;
    private User user;
    private Admin admin;
    private Events event;
    private Tickets ticket;
    private boolean running = true;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";

    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
        this.saveData = new SaveData();
        this.user = new User("");
        this.admin = new Admin();
        this.event = new Events(0, "", "", "", "", "", user);
    }

    public Scanner getInput(){
        return this.input;
    }

    private String userString(String prompt){
        System.out.print(prompt);
        return this.input.nextLine();
    }

    private int userInt(String prompt){
        System.out.print(prompt);
        return this.input.nextInt();
    }

    //start app
    public void start() throws IOException{
        try(var connect = saveData.connectToDatabase()) { //code block in the wrong place, should open and close connection for event, user and ticket saving
            System.out.println("Connection to database established.");
        } catch (SQLException sqle) {
            System.err.println("Could not establish a connection to the database.");
        }
        System.out.println();
        loadUsers(); //store user list to from file
        mainMenu(); 
    }

    //menu methods
    public void mainMenu() throws IOException{
         while(running){
            view.printStart();
            System.out.print("\n>> ");
            try {
                int choice = getInput().nextInt();
                switch (choice) {
                    case 1 -> userMenu(); 
                    case 2 -> loggingIn();
                    case 3 -> { running = false; getInput().close(); view.closingMessage(); } //exit
                    default -> { System.err.println(outOfRange); }
                }
            } catch(InputMismatchException ime) {
                System.err.println(invalidInput);
                getInput().nextLine();
            }
            
        }
    }

    public void userMenu() throws IOException{ //main user menu
        view.firstUserMenu();
        System.out.print("\n>> ");
        try {
            int choice = input.nextInt();
            switch (choice) {
                case 1 -> checkCurrentUser();
                case 2 -> addUserToFile();
                case 3 -> { System.out.println(); return; }
                default -> { System.err.println(outOfRange); }
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
            userMenu();
        }
    }

    public void adminMenu() throws IOException{ //main admin menu
        System.out.print("\nWelcome Admin.");
        view.firstAdminMenu();
        System.out.print("\n>> ");
        try {
            int choice = input.nextInt();
            switch (choice) {
                case 1 -> { printUsersFromFile(); adminMenu(); }
                case 2 -> { System.out.println("\nNothing"); }
                case 3 -> { System.out.println(); return; }
                default -> { System.err.println(outOfRange); }
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
            adminMenu();
        }
    }

    public void addUserToFile(){ //adds user to list of users file for admin to see 
        System.out.print("\nUsername: ");
        this.user.setUserName(input.next());
        if (model.isUserCreated(getUser().getUserName())) {
            System.out.println("Cannot create user. Username already exist.\n");
        }else{
            System.out.println(saveData.saveUser(getUser().getUserName()));
        }
    }

    public void loggingIn(){ //runs login method for admin
        view.loginForAdmin();
        System.out.print("Username: ");
        String adminName = input.next();
        System.out.print("Password: ");
        String adminPass = input.next();
        if(adminLogin(adminName, adminPass)){
            try {
                adminMenu(); 
            } catch (IOException e) {
                System.err.println("System error.");
            }
        }else{
            System.out.println("Incorrect login. Redirecting to Main Menu.\n");
            try {
                mainMenu();
            } catch (IOException ioe) {
                System.err.println("System error.");
                System.exit(0);
            }
        }
    }

    /*
     * in events menu --> option 2: edit events --> 1. See Events 2. Back to Events Menu
     */

    public void subUserMenu(){ //sub user menu after current user option
        System.out.print("\nWelcome " + getUser().getUserName());
        view.secondUserMenu();
        System.out.print("\n>> ");
        try{
            switch(input.nextInt()){
                case 1 -> userEventsMenu();
                case 2 -> {}
                case 3 -> { System.out.println(); return; }
                default -> { System.out.println(outOfRange); }
        }
        }catch(InputMismatchException iome){
            System.err.println(invalidInput);
            getInput().nextLine();
            subUserMenu();
        }
    }

    public void userEventsMenu(){
        view.eventsMenu();
        System.out.print("\n>> ");
        try {
            switch(input.nextInt()){
                case 1 -> createEvent();
                case 2 -> {}
                case 3 -> { System.out.println(); return; }
                default -> { System.out.println(outOfRange); }
            }
        } catch (InputMismatchException iome) {
            System.err.println(invalidInput);
            input.nextLine();
            userEventsMenu();
        }
    }

    public void setUserName(String userName){ //sets current user 
        this.user.setUserName(userName);
    }

    public User getUser(){ //gets current user (not admin)
        return this.user;
    }

    public List<User>getUsers(){ //gets user from file
        return this.model.getUsers();
    }

    public List<Events>getEvents(){ //gets events made by the user
        return this.model.getEvents();
    }

    public List<Events>getUnapprovedEvents(){ //gets unapproved events for the admin to approve
        return this.model.getUnapprovedEvents();
    }

    public void loadUsers()throws IOException{ //gets list of users from the file 
        for(User users : this.saveData.listOfUserNames()){
            model.addUser(users);
        }
    }

    public void loadEvents(){

    }

    public void printUsersFromFile() throws IOException{ //print list of users from list
        for(User auser : model.getUsers()){
            System.out.println(auser.getUserName());
        }
    }

    public boolean adminLogin(String name, String password){ //login for admin option
        return this.admin.login(name, password);
    }

    // check for user file meth -- list of users in a file gets read into an arraylist. Each user has their own file to store their events and tickets

    public void checkCurrentUser(){ //check if current user is in list of Users list
        System.out.print("Enter username: ");
        this.user.setUserName(input.next());
        if(model.isUserCreated(getUser().getUserName())){
            subUserMenu();
        }else{
            System.out.println("Create a new user.\n");
        }
    }

    public Events getEvent(){ //gets current event
        return this.event;
    }

    public void createEvent(){ //method for creating an event --> id, name, description, date, time, location
        try {
            System.out.print("\nEnter details below. ");
            int eventID = userInt("\nEvent ID (4 digit) : ");
            input.nextLine();
            String eventName = userString("Event Name: ");
            String eventDescription = userString("Event Description: ");
            String eventDate = userString("Event Date (DD-MM-YYYY): ");
            String eventTime = userString("Event Time (HH:MM): ");
            String eventLocation = userString("Event Location: ");
            System.out.println();

            this.event.setEventID(eventID);
            this.event.setEvent(eventName);
            this.event.setEventDescription(eventDescription);
            this.event.setEventDate(eventDate);
            this.event.setEventTime(eventTime);
            this.event.setEventLocation(eventLocation);
            
            if(event.invalidFields()){
                System.out.println("Errors above. Try again.");
                createEvent();
            }else{
                model.addEvent(this.event);
                System.out.println(saveData.saveUserEvent(this.event, this.user.getUserName()));
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            input.nextLine();
            subUserMenu();
        }
    }
}
