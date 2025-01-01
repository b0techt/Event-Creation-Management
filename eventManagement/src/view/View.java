package view;
import java.sql.Time;

import controller.*;
public class View {
    public void printStart(){
        System.out.println("Hello, User and Welcome to Tailored Events.");
		System.out.print("""
                                 Are you a User or Admin.
                                 1. User
                                 2. Admin
								 0. Exit	""");

    }

    public void areYouUser(){
        System.out.print("""
                    1. Current User
                    2. New User
                    3. Back to Main Menu
                    """);
    }

    public void printUserMenu(){
        System.out.print("""
                1. Create Event
                2. Edit Event Tickets
                0. Exit """);
    }

    public void closingMessage(){
        System.out.print("""
                Thank you for your time. 
                Exiting application.
                """);
    }
}
