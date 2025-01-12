package model;

public class User {
    private String name;

    public User(String userName){
        this.name = userName;
    }

    public String getUserName(){
        return this.name;
    }

    public void setUserName(String newUser){
        this.name = newUser;
    }
}
