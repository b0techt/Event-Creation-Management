package model;
/*
 * unique id, name, location, description, date/time and created by user
 */
public class Events {
    private int eventID;
    private String eventName;
    private String eventLocation;  
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    public String userName;

    public Events(int eventIDs, String eventNameString, String eventLocationString, String eventDescString, String eventDateString, String eventTimeString, User user){
        this.eventID = eventIDs;
        this.eventName = eventNameString;
        this.eventLocation = eventLocationString;
        this.eventDescription = eventDescString;
        this.eventDate = eventDateString;
        this.eventTime = eventTimeString;
        this.userName = user.getUserName();
    }

    public int getEventID(){ //get new event id
        return this.eventID;
    }

     public String getEventName(){         //get new event name
        return this.eventName;
    }

    public String getEventLocation(){   //get new event location
        return this.eventLocation;
    }

    public String getEventDescription(){    //get new event description
        return this.eventDescription;
    }

    public String getEventDate(){       //get new event date
        return this.eventDate;
    }

    public String getEventTime(){       //get new event time
        return this.eventTime;
    }

    public void setEventID(int newEventID){ //set new unique event id
        this.eventID = newEventID;
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
        this.eventDate = newEventDate;
    }

    public void setEventTime(String newEventTime){  //set new Event time upon new creation
        this.eventTime = newEventTime;
    }
}
