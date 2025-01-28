package model;
/*
 * unique id, name, location, description, date/time and created by user
 */
public class Events {
    private String eventName;
    private String eventLocation;  
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    public String userName;
    private int status;

    public Events(String eventNameString, String eventDescString, String eventDateString, String eventTimeString, String eventLocationString, User user){
        this.eventName = eventNameString;
        this.eventLocation = eventLocationString;
        this.eventDescription = eventDescString;
        this.eventDate = eventDateString;
        this.eventTime = eventTimeString;
        this.userName = user.getUserName();
        this.status = 0;
    }

     public String getEventName(){ //get new event name
        return this.eventName;
    }

    public String getEventLocation(){ //get new event location
        return this.eventLocation;
    }

    public String getEventDescription(){ //get new event description
        return this.eventDescription;
    }

    public String getEventDate(){ //get new event date
        return this.eventDate;
    }

    public String getEventTime(){ //get new event time
        return this.eventTime;
    }

    public int getEventStatus(){
        return this.status;
    }

    public String statusString(){
        if(this.status == 1){
            return "Approved";
        }else if(this.status == 2){
            return "Rejected";
        }
        return "Unapproved";
    }

    public void setEvent(String newEvent){  //set new Event name upon new creation
        this.eventName = newEvent;
    }

    public void setEventLocation(String newEventLocation){ //set new Event location upon new creation
        this.eventLocation = newEventLocation;
    }

    public void setEventDescription(String newEventDescription){    //set new Event description upon new creation
        this.eventDescription = newEventDescription;
    }

    public void setEventDate(String newEventDate){  //set new Event date upon new creation
        if(!newEventDate.matches("\\d{2}-\\d{2}-\\d{4}")){
            System.out.println("Please match the Date format in brackets.\n");
        }else{
            this.eventDate = newEventDate;
        }
    }

    public void setEventTime(String newEventTime){  //set new Event time upon new creation
        if(!newEventTime.contains(":")){
            System.out.println("Please match the Time format in brackets.\n");
        }else{
            this.eventTime = newEventTime;
        }
    }

    public void setEventStatus(int newStatus){ //set new status only for admin to access
        this.status = newStatus;
    }

    public boolean invalidFields(){
        return eventName.isEmpty() || eventDescription.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty() || eventLocation.isEmpty();
    }
}
