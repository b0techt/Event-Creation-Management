package controller;
import java.io.IOException;
import java.util.*;
import model.*;
import view.View;

public class Controller  {
    private final Scanner input = new Scanner(System.in);
    private final Model model;
    private final View view;
    private final SaveDataDB saveData;
    private final LocalData ld;
    private final User user;
    private final Admin admin;
    private Events event;
    private Tickets ticket;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";
    
    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
        this.saveData = new SaveDataDB();
        this.ld = new LocalData();
        this.user = new User("");
        this.admin = new Admin();
        this.event = null;
        this.ticket = null;
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

    public double userDouble(String prompt){
        System.out.print(prompt);
        return this.input.nextDouble();
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
        for(User users : this.ld.listOfUserNames()){
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

    public boolean emptyFieldsCheck(){
        return this.event.invalidFields();
    }

    public boolean ticketEmptyFieldsCheck(){
        return this.ticket.emptyTicket();
    }
    
    public boolean checkCurrentUser(){ //check if current user is in list of Users list
        getInput().nextLine(); //clear input
        String userName = userString("Enter username (No spaces): ");
        this.user.setUserName(userName);
        //System.out.println("User does not exist. Create a new user.\n");
        return model.isUserCreated(getUser().getUserName());
    }
    
    public void addUserToFile(){ //adds user to list of users file for admin to see 
        getInput().nextLine(); //clear input
        String userName = userString("\nUsername (No spaces): ");
        this.user.setUserName(userName);

        if (model.isUserCreated(getUser().getUserName())) {
            System.out.println("Cannot create user. Username already exist.\n");
        }else{
            saveData.userIntoDB(getUser().getUserName()); //save user to user db
            model.addUser(getUser()); //update list of users for admin to see
            System.out.println(ld.saveUser(getUser().getUserName())+"\n");
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

    public void ticketAdmission(){ //this and private method below are used to add tickets to event
        int addOps = userInt(">> ");

        if(addOps < 0 || addOps >= getEvents().size()){
            System.err.println(outOfRange);
        }

        setCurrEvent(addOps);
        createTicket(this.event);
    }

    private void createTicket(Events event){ //create ticket --> ticket type, price, max avail and curr avail
        getInput().nextLine(); //clears input
        try{
            String ticketType = userString("\nTicket type: ");
            double ticketPrice = userDouble("Ticket Price (£): ");
            int ticketMaxAvail = userInt("Maximum Admission: ");
            int ticketCurrAvail = userInt("Current Admissions: ");

            this.ticket = new Tickets(ticketType, ticketPrice, ticketCurrAvail, ticketMaxAvail);

            if(ticketEmptyFieldsCheck()){
                System.out.println("Errors above. Try again");
            }else{
                saveData.saveTicket(ticket, event, getUser().getUserName());
            }
        }catch(InputMismatchException e){
            System.err.println(invalidInput);
            getInput().nextLine();
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

            this.event = new Events(eventName, eventDescription, eventDate, eventTime, eventLocation, user);

            if(emptyFieldsCheck()){
                System.out.println("Errors above. Try again.");
            }else{
                saveData.saveUserEvent(getEvent(), getUser().getUserName());
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
        }
    }

    public void editEvent(Events event) { // Edit chosen user event
        int choice = userInt("\n>> ");
        getInput().nextLine(); // Consume newline
        
        String newDetail;
        switch (choice) {
            case 1 -> {
                newDetail = userString("\nNew Event Name: ");
                event.setEvent(newDetail);
                if (emptyFieldsCheck()) {
                    System.out.println("Error: Please try again.");
                    return;
                }
            }
            case 2 -> {
                newDetail = userString("\nNew Event Description: ");
                event.setEventDescription(newDetail);
                if (emptyFieldsCheck()) {
                    System.out.println("Error: PLease try again.");
                    return;
                }
            }
            case 3 -> {
                newDetail = userString("\nNew Event Date (DD-MM-YYYY): ");
                event.setEventDate(newDetail);
                if (emptyFieldsCheck()) {
                    System.out.println("Error: Please try again.");
                    return;
                }
            }
            case 4 -> {
                newDetail = userString("\nNew Event Time (HH:MM): ");
                event.setEventTime(newDetail);
                if (emptyFieldsCheck()) {
                    System.out.println("Error: Please try again.");
                    return;
                }
            }
            case 5 -> {
                newDetail = userString("\nNew Event Location: ");
                event.setEventLocation(newDetail);
                if (emptyFieldsCheck()) {
                    System.out.println("Error: Please try again.");
                    return;
                }
            }
            default -> { return; }
        }
        this.saveData.updateEvent(event, event.userName, newDetail, choice);
        view.printChangedEvDeets(event, choice);
    }
}