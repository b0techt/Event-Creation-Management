package view;
public class View {
    public void printStart(){
        System.out.println("Hello, User and Welcome to Tailored Events.");
		System.out.print("1. User\n2. Admin\n3. Exit");
    }

    public void loginForAdmin(){
        System.out.println("\nPlease login for admin controls.");
    }

    public void firstUserMenu(){
        System.out.print("\n1. Current User\n2. New User\n3. Back to Main Menu");
    }

    public void firstAdminMenu(){
        System.out.print("\n1. See Users\n2. Confirm Events\n3. Back to Main Menu");
    }

    public void eventsMenu(){
       System.out.print("\n1. Create Event\n2. Edit Event\n3. Back to Main Menu");
    }

    public void ticketsMenu(){
        System.out.print("\n1. View Available Ticket Types\n2. Edit Tickets\n3. Back to Main Menu");
    }

    public void closingMessage(){
        System.out.print("""
                Thank you for your time. 
                Exiting application.
                """);
    }
}
