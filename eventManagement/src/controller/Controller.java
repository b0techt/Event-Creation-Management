package controller;
import java.io.IOException;
import java.util.*;
import model.*;
import view.View;

public class Controller  {
    private final Scanner input = new Scanner(System.in);
    private final Model model;
    private final View view;
    private final SaveData saveData;
    private final User user;
    private final Admin admin;
    private Events event;
    //private Tickets ticket;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";
    
    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
        this.saveData = new SaveData();
        this.user = new User("");
        this.admin = new Admin();
        this.event = null;
    }

    public Scanner getInput(){
        return this.input;
    }

    public String userString(String prompt){
        System.out.print(prompt);
        return this.input.nextLine();
    }

    public int userInt(String prompt){
        System.out.print(prompt);
        return this.input.nextInt();
    }

    //start app
    public void start() throws IOException{
        loadUsers(); //store user list to from file
        view.mainMenu(); 
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

    public void loadUsers()throws IOException{ //gets list of users from the file 
        for(User users : this.saveData.listOfUserNames()){
            model.addUser(users);
        }
    }

    public void loadEvents(){ //loads users events from databasae
        getEvents().clear();
        for(Events events : this.saveData.getUserEvents(getUser().getUserName())){
            model.addEvent(events);
        }
    }

    public void loadEventsAdmin(){ //loads all events from eventsinfo table
        getEvents().clear();
        for(Events events : this.saveData.getAllEvents()){
            model.addEvent(events);
        }
    }

    public boolean adminLogin(String name, String password){ //login for admin option
        return this.admin.login(name, password);
    }

    public void setCurrEvent(int n){
        this.event = getEvents().get(n);
    }

    public Events getEvent(){ //gets current event
        return this.event;
    }
    
    public boolean checkCurrentUser(){ //check if current user is in list of Users list
        getInput().nextLine(); //clear input
        String userName = userString("Enter username (No spaces): ");
        this.user.setUserName(userName);
        //System.out.println("User does not exist. Create a new user.\n");
        return model.isUserCreated(getUser().getUserName());
    }
    
    public void addUserToFile(){ //adds user to list of users file for admin to see 
        String userName = userString("\nUsername (No spaces): ");
        this.user.setUserName(userName);

        if (model.isUserCreated(getUser().getUserName())) {
            System.out.println("Cannot create user. Username already exist.\n");
        }else{
            saveData.userIntoDB(getUser().getUserName()); //save user to user db
            model.addUser(getUser()); //update list of users for admin to see
            System.out.println(saveData.saveUser(getUser().getUserName())+"\n");
        }
    }

    public boolean loggingIn(){ //runs login method for admin
        getInput().nextLine(); //clear input from nextInt() 
        String adminName = userString("Username: ");
        String adminPass = userString("Password: ");
        return checkAdminLogin(adminName, adminPass);
    }

    private boolean checkAdminLogin(String name, String password){ //admin view updated based on input
        if(adminLogin(name, password)){
            this.user.setUserName("");
            return true;
        }
        return false;
    }

    public void eventSeeOrEdit(Events chosenEvent){ //sends data to view for event display
        editChoice(chosenEvent);
    }
    
    public void seeEvents(){ //method to see events and edit if needed.
        int editOps = userInt(">> ");

        if(editOps < 0 || editOps >= getEvents().size()){
            System.err.println(outOfRange);
        }

        setCurrEvent(editOps);
    }

    private void editChoice(Events selectedEvent){ //method for option 2 on events menu, updates the view
        try {
            String feedback = this.saveData.adminFeedback(selectedEvent.getEventName(), getUser().getUserName()); 
            view.displayAdminFeedback(feedback); //get rid of view call
                
        }catch(InputMismatchException e){
            System.err.println(invalidInput);
            System.err.println("Error processing input. Error occurred: "+e.getMessage());
            getInput().nextLine(); //clears input
        }
    }

    public void createEvent(){ //method for creating an event --> id, name, description, date, time, location
        try {
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
                saveData.saveUserEvent(getEvent(), getUser().getUserName());
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
        }
    }

    public void editEvent(Events event){ //edit chosen user event
        //go over this when I get home
        int choice = userInt("\n>> ");
        switch(choice){
            case 1 -> { getInput().nextLine(); String newDetail = userString("\nNew Event Name: "); this.saveData.updateEvent(event, event.userName, newDetail, choice); event.setEvent(newDetail);}
            case 2 -> { getInput().nextLine(); String newDetail = userString("\nNew Event Description: "); this.saveData.updateEvent(event, event.userName, newDetail, choice); event.setEventDescription(newDetail);}
            case 3 -> { getInput().nextLine(); String newDetail = userString("\nNew Event Date: "); this.saveData.updateEvent(event, event.userName, newDetail, choice); event.setEventDate(newDetail);}
            case 4 -> { getInput().nextLine(); String newDetail = userString("\nNew Event Time: "); this.saveData.updateEvent(event, event.userName, newDetail, choice); event.setEventTime(newDetail);}
            case 5 -> { getInput().nextLine(); String newDetail = userString("\nNew Event Location: "); this.saveData.updateEvent(event, event.userName, newDetail, choice); event.setEventLocation(newDetail);}
            default -> { return; }
        }
    }

    public void confirmEvents(){ //admin control to confirm events
        int evn = userInt(">> ");
        if(evn < 0 || evn >= getEvents().size()){
            System.err.println(outOfRange);
        }

        setCurrEvent(evn);
        adminConfirmChoice(this.event);
    }

    private void adminConfirmChoice(Events selectedEvent){ //confirm choice from admin
        try{
            int update = view.getAdminConfirmChoice(event);

            switch(update){ /* getInput().nextLine() clears the nextInt() input */
                case 1, 2 -> { getInput().nextLine(); String stinfo = userString("\nReason for descision: "); saveData.adminApprove(selectedEvent, selectedEvent.userName, stinfo, update); }
                default -> { return; }
            }
        }catch(IndexOutOfBoundsException e){
            System.err.println("Error processing input. Error occurred: "+e.getMessage());
            getInput().nextLine(); //clears input
        }catch(InputMismatchException e){
            System.err.println(invalidInput);
            getInput().nextLine();
        }
    }
}