package controller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Events;
import model.Model;
import model.User;

public class SaveData {
    private Model users;
    private final String ul = "eventManagement\\db\\listOfUsers.txt";
    private final String userPath = "eventManagement\\db\\dbUser";
    private BufferedWriter bw;
    private boolean append;

    public String getCurrentUserForEdits(String userName)throws FileNotFoundException, IOException{ //get current user if not create new user
        for(User user : users.getUsers()){
            if (user.getUserName().equals(userName)) {
                File userFile = new File(userName+".txt");
                if (userFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(userName + ".txt"))) {
                        String line = br.readLine();
                        while (line != null) {
                            System.out.println(line);
                        }
                        return "Welcome " + userName;
                    }
                }
                return "Please create a new user";
                
            }
        }
        return "Please create a new user";
    }

    public String saveUser(String userName){
        try {
            append = false;
            try(BufferedReader br = new BufferedReader(new FileReader(ul))){
                append = br.readLine() != null;
            }

            bw = new BufferedWriter(new FileWriter(ul, append));
            if(append){
                bw.newLine();
            }
            bw.write(userName);
            bw.close();
        } catch (IOException ioe) {
            System.err.println("Cannot save user. Will attempt again on application exit.\n");
        }
        return "Username saved.";
    }

    public List<User>listOfUserNames()throws IOException{
        List<User>usernames = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(ul))){
            String line;
            while((line = br.readLine()) != null){
                usernames.add(new User(line));
            }
            return usernames;
        }
    }

    public String saveUserEvent(Events event, String userData){
        try{
           File userFile = new File(userPath,userData+".txt");
           if(userFile.exists()){
            bw = new BufferedWriter(new FileWriter(userFile, true));
            bw.newLine();
           }else{
            userFile.createNewFile();
            bw = new BufferedWriter(new FileWriter(userFile, false));
           }

           bw.write("Status: "+event.statusString()+" Event ID: "+event.getEventID()+" Event Name: "+event.getEventName()+" Event Description: "+event.getEventDescription()+
           " Event Date: "+event.getEventDate()+" Event Time: "+event.getEventTime()+" Event Location: "+event.getEventLocation());
           bw.close();
        }catch(IOException ioe){
            System.err.println("Could not save event to User file.");
        }
        return "Event has been saved.";
    }
}
