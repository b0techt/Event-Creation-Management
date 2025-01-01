package model;

public class Admin {
    private String adminName;
    private String adminPass;

    public Admin(String name, String pass){
        this.adminName = name;
        this.adminPass = pass;
    }

    public String getAdminName(){
        return this.adminName;
    }

    public String getAdminPass(){
        return this.adminPass;
    }

    public void setAdminName(String newAdminName){
        this.adminName = newAdminName;
    }

    public void setAdminPass(String newAdminPass){
        this.adminPass = newAdminPass;
    }

    //methods
    public boolean approveEvents(String approve){
        return approve.equalsIgnoreCase("yes");
    }
    
}
