package controller;
import java.io.IOException;
import java.util.*;
import model.*;
import view.View;

public class Controller {
    private final Scanner input = new Scanner(System.in);
    private Model model;
    private View view;
    private final SaveData sd;
    private User user;
    private Admin admin;
    private boolean running = true;
    private final String invalidInput = "Invalid input. Try again.\n";
    private final String outOfRange = "Option out of range. Try again.\n";

    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
        this.sd = new SaveData();
        this.user = new User("");
        this.admin = new Admin();
    }

    public Scanner getInput(){
        return this.input;
    }

    //start app
    public void start() throws IOException{
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

    public void userMenu() throws IOException{
        while(running){
            view.firstUserMenu();
            System.out.print("\n>> ");
            try {
                int choice = input.nextInt();
                switch(choice){
                    case 1 -> {}
                    case 2 -> addUserToFile();
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> {System.err.println(outOfRange);}
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                getInput().nextLine();
            }
        }
    }

    public void adminMenu() throws IOException{
        while(running){
            view.firstAdminMenu();
            System.out.print("\n>> ");
            try {
                int choice = input.nextInt();
                switch(choice){
                    case 1 -> printUsersFromFile();
                    case 2 -> {System.out.println("\nNothing");}
                    case 3 -> { System.out.println(); mainMenu(); }
                    default -> {System.err.println(outOfRange);}
                }
            } catch (InputMismatchException ime) {
                System.err.println(invalidInput);
                getInput().nextLine();
            }
        }
    }

    public void addUserToFile(){ //adds user to list of users file for admin to see 
        System.out.print("\nUsername: ");
        this.user.setUserName(input.next());
        System.out.println(sd.saveUser(getUser().getUserName()));
    }

    public void loggingIn(){ //runs login method
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

    public void setUserName(String userName){ //sets current user 
        this.user.setUserName(userName);
    }

    public User getUser(){ //gets current user (not admin)
        return this.user;
    }

    public List<String>getUsersFromFile()throws IOException{ //gets list of users from the file 
        return this.sd.listOfUserNames();
    }

    public void printUsersFromFile() throws IOException{
        for(String s : getUsersFromFile()){
            System.out.println(s);
        }
    }

    public boolean adminLogin(String name, String password){ //login for admin option
        return this.admin.login(name, password);
    }

    // check for user file meth -- list of users in a file gets read into an arraylist. Each user has their own file to store their events and tickets
    
}
