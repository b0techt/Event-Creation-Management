package controller;
import java.util.*;
import model.Model;
import view.View;

public class Controller {
    private final Scanner input = new Scanner(System.in);
    private Model model;
    private View view;
    private boolean running = true;

    public Controller(Model m, View v){
        this.model = m;
        this.view = v;
    }

    public Scanner getInput(){
        return this.input;
    }

    //start app -- make this shorter
    public void start(){
        view.printStart();
        int choice = getInput().nextInt();
        while(running){
            switch(choice){
                case 1 -> { userMenu(); }
                case 2 -> { adminMenu(); }
                case 3 -> { running = false; getInput().close(); view.closingMessage(); } //exit
                default -> { System.err.println("Invalid option choosen."); }
            }
        }
    }

    //menu methods
    public void userMenu(){

    }

    public void adminMenu(){

    }

    // check for user file meth
    
}
