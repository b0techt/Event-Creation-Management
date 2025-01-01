package model;

public class User {
    private String name;
    private String password;

    public User(String userName, String userPassword){
        this.name = userName;
        this.password = userPassword;
    }

    public String getUserName(){
        return this.name;
    }

    public String getUserPassword(){
        return this.password;
    }

    public void setUserName(String newUser){
        this.name = newUser;
    }

    public void setUserPassword(String newUserPass){
        this.password = newUserPass;
    }

}
