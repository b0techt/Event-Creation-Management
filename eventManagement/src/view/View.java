package view;
import controller.Controller;
import java.io.IOException;
import java.util.InputMismatchException;
import model.Events;
import model.User;
import model.Tickets;

public class View {
    private Controller c;
    private boolean running = true;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";

    public void setController(Controller c){ //sets controller object
        this.c = c;
    }

    public void printStart(){ //start message
        System.out.println("Hello, User and Welcome to Tailored Events. Use the numbers as directions.");
		System.out.print("1: User \n2: Admin \n3: Exit");
    }

    public void closingMessage() { //closing message after application exit
        System.out.print("""
                Thank you for your time. 
                Exiting application.
                """);
    }

    private void loginForAdmin(){ //admin login 
        System.out.println("\nPlease login for admin controls.");
    }

    private void firstUserMenu(){ //first set of user menu after selecting user
        System.out.print("\n1: Current User \n2: New User \n3: Back to Main Menu");
    }

    private void firstAdminMenu(){ //admin menu after login
        System.out.print("\n1: See Users \n2: Confirm Events \n3: Back to Main Menu");
    }

    private  void secondUserMenu(){ //next menu after confirming user name
        System.out.print("\n1: Events Menu \n2: Tickets Menu \n3: Back to Main Menu");
    }

    private void eventsMenu(){ //menu for events configuration and creation
       System.out.print("\n1: Create Event \n2: Edit Event \n3: Back to Main Menu");
    }

    private void ticketsMenu(){ //first tickets menu
        System.out.print("\n1: Add Tickets \n2: Edit Ticket Admissions \n3: Back to Main Menu");
    }

    private void adminConfirmation() { // options for admin confirmation on an event
        System.out.print("\n1: Approve. \n2: Reject. \n3: Cancel.");
    }
    
    private void displayEditEventPrompt(){ //print
        System.out.print("\nChoose which detail to edit. \n1: Event Name. \n2: Event Description. \n3: Event Date.\n4: Event Time.\n5: Event Location.\n6: Cancel.");
    }

    private void displayEditTicketPrompt(){ //print
        System.out.print("\nChoose which detail to edit. \n1: Ticket Type. \n2: Ticket Price. \n3: Ticket Current Availability. \n4: Ticket Maximum Availability. \n5: Delete Ticket. \n6: Cancel.");
    }

    private void eventsInfoView(){ //sub menu for events menu option 2
        System.out.print("\n1: Edit details. \n2: See Admin Feedback \n3: Cancel");
        int option  = c.userInt("\n>> ");
        if(option == 1){
            displayEditEventPrompt();
            c.editEvent(c.getEvent());
        }else if(option == 2){
            c.callAdminFeedback(c.getEvent());
        }else if(option > 3){
            System.out.println(outOfRange);
        }
    }

    private void addTicketsView(){ //view for option 1 of tickets menu
        addTicketPrompt();
        printUserEvents();
        c.ticketAdmission();
        System.out.print("\n1: Continue with Ticket admission. \n2: Go back.");
        int option = c.userInt("\n>> ");
        if(option == 1){
            c.callCT(c.getEvent());
        }else if(option == 2){
            return;
        }
    }

    public void editTicketsView(){ //view for option 2 of tickets menu
        editTicketPrompt();
        printUserEvents();
        c.ticketAdmission();
        c.loadTickets();
        if(c.getTickets().isEmpty()){
            System.out.println("No available tickets.");
            return;
        }
        printTickets(); 
        continueEditTicketView();
    }

    private void continueEditTicketView(){
        System.out.print("\n1: Continue with editing Ticket admission. \n2: Go back.");
        int option = c.userInt("\n>> ");
        if(option == 1){
            System.out.print("\nChoose a Ticket to edit.");
            c.editAdmission();
            displayEditTicketPrompt();
            c.callET(c.getEvent(), c.getTicket());
        }else if(option == 2){
            return;
        }
    }

    private void adminLogin(){ //admin login view
        loginForAdmin();
        try {
            if(c.loggingIn()){
                adminMenu();
            }else{
                System.out.println("Incorrect login. Redirecting to Main Menu.\n");
                mainMenu();
            }
        } catch (IOException e) { System.out.println("Operation cancelled. Restart application. "+e.getMessage()+"\n");}
    }

    private void userLogin(){ //user login view
        if(c.checkCurrentUser()){
            subUserMenu();
        }else{
            System.out.println("Incorrect login. Redirecting to Main Menu.\n");
        }
    }

    private void createNewUserView(){ //create new user view
        c.getInput().nextLine(); //clear input
        String userName = c.userString("\nUsername (No spaces): ");
        c.addUserToFile(userName);
    }

    public void showEditPrompt(){ //user edit prompt
        System.out.println("\nChoose which event you would like to edit. Edited events will need to be reapproved.\n");
    }

    public void showConfirmPrompt(){ //admin confirmation prompt
        System.out.println("Choose what event you would like to confirm.\n");
    }

    public void showCreateEventPrompt(){ //user event creation prompt
        System.out.print("\nEnter details below. ");
        c.createEvent();
    }

    public void addTicketPrompt(){ //add ticket prompt
        System.out.println("\nChoose what event you would like to add tickets to. Edited tickets will reset the events to Unapproved.");
    }

    public void editTicketPrompt(){ //edit ticket prompt
        System.out.println("\nChoose what event you would like to edit tickets for.");
    }

    public void showEvent(Events event){ //show current event
        if (c.getUser().getUserName().isEmpty()) {
            System.out.println("\nYou have chosen "+event.getEventName()+", Status: "+event.statusString()
            +", Created by: "+event.userName);
        }else{
            System.out.println("\nChosen: "+event.getEventName()+" event. Status: "+event.statusString());
        }
    }

    public void userEventOps(){ //user event options view 
        showEditPrompt(); 
        printUserEvents(); 
        if(c.getEvents().isEmpty()){
            System.out.println("No Events available");
            return;
        }
        c.seeEvents();
        eventsInfoView();
    }

    public void adminEventOps(){ //admin event options view after confirm events
        showConfirmPrompt();
        printEvents();
        if(c.getEvents().isEmpty()){
            System.out.println("No Events available");
            return;
        }
        c.confirmEvents();
    }

    public void displayAdminFeedback(String feedback){ //print admin feedback
        System.out.println("Admin feedback: "+ feedback);
    }

    public int getAdminConfirmChoice(Events event){ //admin confirmation view
        showEvent(event);
        adminConfirmation();
        return c.userInt("\n>> ");
    }

    // print methods
    public void printChangedEvDeets(Events event, int choice){ //print edited event detail
        switch(choice){
            case 1 -> { System.out.println("Event Name has been changed to "+event.getEventName()); }
            case 2 -> { System.out.println("Event Description has been changed to "+event.getEventDescription()); }
            case 3 -> { System.out.println("Event Date has been changed to "+event.getEventDate()); }
            case 4 -> { System.out.println("Event Time has been changed to "+event.getEventTime()); }
            case 5 -> { System.out.println("Event Location has been changed to "+event.getEventLocation()); }
            default -> { return; }
        }
    }
    
    public void printUserEvents(){ //prints events linked to user name
        int i = 0;
        for(Events ev : c.getEvents()){
            System.out.print(i + " Event Name: "+ev.getEventName() 
            +", Event Description: " + ev.getEventDescription()
            +", Event Date: "+ev.getEventDate()
            +", Event Time: "+ev.getEventTime()
            +", Event Location: "+ev.getEventLocation()
            +", Status: "+ev.statusString());
            i++;
            System.out.println(); //new line
        }

    }

    public void printEvents(){ //ignore the method name, prints all events
        c.loadEventsAdmin();
        int i = 0;
        for(Events ev : c.getEvents()){
            System.out.print(i+" Created by: "+ev.userName + ", Event Name: "+ev.getEventName() 
            +", Event Description: " + ev.getEventDescription()
            +", Event Date: "+ev.getEventDate()
            +", Event Time: "+ev.getEventTime()
            +", Event Location: "+ev.getEventLocation()
            +", Status: "+ev.statusString());
            i++;
            System.out.println(); //new line
        }
    }

    public void printTickets(){
        int i = 0;
        for(Tickets ticket : c.getTickets()){
            System.out.print(i+" Ticket type: "+ticket.getTicketType()
            +", Ticket Price (£): "+ticket.getTicketPrice()
            +", Ticket Current Availability: "+ticket.getAvailability()
            +", Ticket Maxmimum Availability: "+ticket.getMaxAvailability());
            i++;
            System.out.println(); //new line
        }
    }

    public void printUsersFromFile() throws IOException{ //print list of users from list
        for(User auser : c.getUsers()){
            System.out.println(auser.getUserName());
        }
    }

     //menu methods
    public void mainMenu() throws IOException{
         while(running){
            printStart();
            System.out.print("\n>> ");
            try {
                int choice = c.getInput().nextInt();
                switch (choice) {
                    case 1 -> userMenu(); 
                    case 2 -> adminLogin();
                    case 3 -> { running = false; c.getInput().close(); closingMessage(); } //exit
                    default -> { System.err.println(outOfRange); }
                }
            } catch(InputMismatchException ime) {
                System.err.println(invalidInput);
                c.getInput().nextLine();
            }
            
        }
    }

    public void userMenu() throws IOException{ //main user menu
        while(running){
            firstUserMenu();
            System.out.print("\n>> ");
            try {
                int choice = c.getInput().nextInt();
                switch (choice) {
                    case 1 -> userLogin();
                    case 2 -> createNewUserView();
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.err.println(outOfRange); }
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                c.getInput().nextLine();
            }
        }
    }

    public void adminMenu() throws IOException{ //main admin menu
        while(running){
            System.out.print("\nWelcome Admin.");
            firstAdminMenu();
            System.out.print("\n>> ");
            try {
                int choice = c.getInput().nextInt();
                switch (choice) {
                    case 1 -> printUsersFromFile();
                    case 2 -> adminEventOps();
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.err.println(outOfRange); }
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                c.getInput().nextLine();
            }
        }
    }

    public void subUserMenu(){ //sub user menu after current user option
        c.loadEvents();
        while(running){
            System.out.print("\nWelcome " + c.getUser().getUserName());
            secondUserMenu();
            System.out.print("\n>> ");
            try{
                switch(c.getInput().nextInt()){
                    case 1 -> userEventsMenu();
                    case 2 -> userTicketsMenu();
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> { System.out.println(outOfRange); }
            }
            }catch(InputMismatchException iome){
                System.err.println(invalidInput);
                c.getInput().nextLine();
            }catch(IOException e){
                System.err.println("Unable to execute process. Error occurred: " + e.getMessage());
            }
        }
    }

    public void userEventsMenu(){ //sub menu for events menu option
        eventsMenu();
        System.out.print("\n>> ");
        try {
            switch(c.getInput().nextInt()){
                case 1 -> showCreateEventPrompt();
                case 2 -> userEventOps();
                case 3 -> { System.out.println(); subUserMenu(); }
                default -> { System.out.println(outOfRange); }
            }
        } catch (InputMismatchException iome) {
            System.err.println(invalidInput);
            c.getInput().nextLine();
        }
    }

    public void userTicketsMenu(){ //sub menu for tickets menu option
        if(c.getEvents().isEmpty()){
            System.out.println("No available events. Ticket admission cannot be accessed.");
            return;
        }
        ticketsMenu();
        System.out.print("\n>> ");
        try{
            switch (c.getInput().nextInt()) {
                case 1 -> addTicketsView();
                case 2 -> editTicketsView();
                case 3 -> { System.out.println(); subUserMenu(); }
                default -> { System.out.println(outOfRange); }
            }
        }catch(InputMismatchException e){
            System.err.println(invalidInput);
            c.getInput().nextLine();
        }
    }
}