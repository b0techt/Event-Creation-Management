package model;

public class Admin {
    private String adminName;
    private String adminPass;
    
    public Admin(){
        this.adminName = "admin";
        this.adminPass = "password";
        
    }

    public Admin(String name, String pass){ //here if I want to customise the admin login creds
        this.adminName = name;
        this.adminPass = pass;
    }

    public String getAdminName(){ //gets admin name
        return this.adminName;
    }

    public String getAdminPass(){ //gets admin password
        return this.adminPass;
    }

    public void setAdminName(String newAdminName){ //sets new name if I want to customise admin
        this.adminName = newAdminName;
    }

    public void setAdminPass(String newAdminPass){ //sets new password 
        this.adminPass = newAdminPass;
    }

    //methods
    public boolean login(String name, String pass){ //login checks
        return name.equals("admin") && pass.equals("password");
    }

    public void setEventStatus(Events event, int status){
        event.setEventStatus(status);
    }
    
}
