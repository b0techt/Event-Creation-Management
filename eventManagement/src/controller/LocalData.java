package controller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.User;

/*
 * This class uses the relative file system for storing and reading usernames for quicker actions
 */

public class LocalData {
    private final String ul = "eventManagement\\db\\listOfUsers.txt";
    private BufferedWriter bw;
    private boolean append;

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
    
    public List<User>listOfUserNames()throws IOException{ //helps load users for the admin to see
        List<User>usernames = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(ul))){
            String line;
            while((line = br.readLine()) != null){
                usernames.add(new User(line));
            }
            return usernames;
        }
    }
}
