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
    private boolean running = true;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";
    private final String userData = "eventManagement\\db\\dbUser";

    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
        this.saveData = new SaveData();
        this.user = new User("");
        this.admin = new Admin();
    }

    public Scanner getInput(){
        return this.input;
    }

    //start app
    public void start() throws IOException{
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
                case 3 -> { System.out.println(); mainMenu(); }
                default -> { System.err.println(outOfRange); }
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
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
                case 3 -> { System.out.println(); mainMenu(); }
                default -> { System.err.println(outOfRange); }
            }
        } catch (InputMismatchException ime) {
            System.err.println(invalidInput);
            getInput().nextLine();
        }
    }

    public void addUserToFile(){ //adds user to list of users file for admin to see 
        System.out.print("\nUsername: ");
        this.user.setUserName(input.next());
        if (model.isUserCreated(this.user.getUserName())) {
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

    public void subUserMenu(){ //sub user menu after current user option
        System.out.print("\nWelcome " + this.user.getUserName());
        view.eventsMenu();
        System.out.print("\n>> ");
        try{
            switch(input.nextInt()){
                case 1 -> {}
                case 2 -> {}
                case 3 -> { System.out.println(); mainMenu(); }
                default -> { System.out.println(outOfRange); }
        }
        }catch(IOException | InputMismatchException iome){
            System.err.println(invalidInput);
            getInput().nextLine();
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

    public void loadUsers()throws IOException{ //gets list of users from the file 
        for(User users : this.saveData.listOfUserNames()){
            model.addUser(users);
        }
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
        if(model.isUserCreated(this.user.getUserName())){
            subUserMenu();
        }else{
            System.out.println("Create a new user.\n");
        }
    }
}
