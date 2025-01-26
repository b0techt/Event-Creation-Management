package controller;
import java.io.IOException;
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
        this.event = new Events("", "", "", "", "", user);
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
        loadEventsAdmin();
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
        while(running){
            view.firstUserMenu();
            System.out.print("\n>> ");
            try {
                int choice = getInput().nextInt();
                switch (choice) {
                    case 1 -> checkCurrentUser();
                    case 2 -> addUserToFile();
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.err.println(outOfRange); }
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                getInput().nextLine();
            }
        }
    }

    public void adminMenu() throws IOException{ //main admin menu
        
        while(running){
            System.out.print("\nWelcome Admin.");
            view.firstAdminMenu();
            System.out.print("\n>> ");
            try {
                int choice = getInput().nextInt();
                switch (choice) {
                    case 1 -> { printUsersFromFile(); adminMenu(); }
                    case 2 -> { confirmEvents();  }
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.err.println(outOfRange); }
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                getInput().nextLine();
            }
        }
    }

    /*
     * in events menu --> option 2: edit events --> 1. See Events 2. Back to Events Menu
     */

    public void subUserMenu(){ //sub user menu after current user option
        loadEvents();
       
        while(running){
            System.out.print("\nWelcome " + getUser().getUserName());
            view.secondUserMenu();
            System.out.print("\n>> ");
            try{
                switch(getInput().nextInt()){
                    case 1 -> userEventsMenu();
                    case 2 -> {}
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.out.println(outOfRange); }
            }
            }catch(InputMismatchException iome){
                System.err.println(invalidInput);
                getInput().nextLine();
            }catch(IOException e){
                System.err.println("Unable to execute process. Error occurred: " + e.getMessage());
            }
        }
    }

    public void userEventsMenu(){
        view.eventsMenu();
        System.out.print("\n>> ");
        try {
            switch(getInput().nextInt()){
                case 1 -> createEvent();
                case 2 -> { printUserEvents(); editEvent();}
                case 3 -> { System.out.println(); subUserMenu(); }
                default -> { System.out.println(outOfRange); }
            }
        } catch (InputMismatchException iome) {
            System.err.println(invalidInput);
            getInput().nextLine();
            subUserMenu();
        }
    }

    public void editEvent(){
        System.out.println("\nChoose which event you would like to edit.\n");
        //userEventsMenu();
    }
    
    public void checkCurrentUser(){ //check if current user is in list of Users list
        System.out.print("Enter username (No spaces): ");
        this.user.setUserName(getInput().next());
        if(model.isUserCreated(getUser().getUserName())){
            subUserMenu();
        }else{
            System.out.println("User does not exist. Create a new user.\n");
        }
    }
    
    public void addUserToFile(){ //adds user to list of users file for admin to see 
        System.out.print("\nUsername (No spaces): ");
        this.user.setUserName(getInput().next());
        if (model.isUserCreated(getUser().getUserName())) {
            System.out.println("Cannot create user. Username already exist.\n");
        }else{
            saveData.userIntoDB(getUser().getUserName()); //save user to user db
            model.addUser(getUser()); //update list of users for admin to see
            System.out.println(saveData.saveUser(getUser().getUserName())+"\n");
        }
    }

    public void loggingIn(){ //runs login method for admin
        view.loginForAdmin();
        System.out.print("Username: ");
        String adminName = getInput().next();
        System.out.print("Password: ");
        String adminPass = getInput().next();
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

    public void loadEvents(){ //loads users events from databasae
        for(Events events : this.saveData.getUserEvents(getUser().getUserName())){
            model.addEvent(events);
        }
    }

    public void loadEventsAdmin(){
        for(Events events : this.saveData.getUnapprovedEvents()){
            model.addEvent(events);
        }
    }

    public void printUsersFromFile() throws IOException{ //print list of users from list
        for(User auser : getUsers()){
            System.out.println(auser.getUserName());
        }
    }

    public void printUserEvents(){
        int i = 1;
        for(Events ev : getEvents()){
            System.out.print(i + " Event Name: "+ev.getEventName() 
            +" Event Description: " + ev.getEventDescription()
            +" Event Date: "+ev.getEventDate()
            +" Event Time: "+ev.getEventTime()
            +" Event Location: "+ev.getEventLocation()
            +" Status: "+ev.statusString());
            i++;
            System.out.println(); //new line
        }
    }

    public void printUnapprovedEvents(){
        int i = 0;
        for(Events ev : getUnapprovedEvents()){
            System.out.print(i+" Created by: "+ev.userName + " Event Name: "+ev.getEventName() 
            +" Event Description: " + ev.getEventDescription()
            +" Event Date: "+ev.getEventDate()
            +" Event Time: "+ev.getEventTime()
            +" Event Location: "+ev.getEventLocation()
            +" Status: "+ev.statusString());
            i++;
            System.out.println(); //new line
        }
    }

    public boolean adminLogin(String name, String password){ //login for admin option
        return this.admin.login(name, password);
    }

    public Events getEvent(){ //gets current event
        return this.event;
    }

    public void createEvent(){ //method for creating an event --> id, name, description, date, time, location
        try {
            System.out.print("\nEnter details below. ");
            getInput().nextLine();
            String eventName = userString("\nEvent Name: ");
            String eventDescription = userString("Event Description: ");
            String eventDate = userString("Event Date (DD-MM-YYYY): ");
            String eventTime = userString("Event Time (HH:MM): ");
            String eventLocation = userString("Event Location: ");
            System.out.println();

            this.event.setEvent(eventName);
            this.event.setEventDescription(eventDescription);
            this.event.setEventDate(eventDate);
            this.event.setEventTime(eventTime);
            this.event.setEventLocation(eventLocation);
            
            if(getEvent().invalidFields()){
                System.out.println("Errors above. Try again.");
                createEvent();
            }else{
                //model.addEvent(this.event);
                saveData.saveUserEvent(getEvent(), getUser().getUserName());
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
            subUserMenu();
        }
    }

    public void confirmEvents(){
        System.out.println("Choose what event you would like to confirm.\n");
        printUnapprovedEvents();
        try{
            System.out.print("\n>> ");
            int evn = getInput().nextInt();
            System.out.println(getUnapprovedEvents().get(evn));
            /*
            switch(getInput().nextInt()){

            }*/
        }catch(IndexOutOfBoundsException e){
            System.err.println("Error processing input. Error occurred: "+e.getMessage());
            getInput().nextLine(); //clears input
        }catch(InputMismatchException e){
            System.err.println(invalidInput);
            getInput().nextLine();
        }

    }
}
