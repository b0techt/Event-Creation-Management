package model;

/*
 * Ticket holds the type of ticket the users will issue for their events and the price of the tickets 
 */

public class Tickets {
    private String ticketType;      //type of ticket
    private double ticketPrice;     //price of ticket
    private int ticketAvailability; //availability of ticket
    private int maxTicketAvailability; //maximum availability of a ticket


    public Tickets(String typeOfTicket, double priceOfTicket, int maximumAvailability){
        this.ticketType = typeOfTicket;
        this.ticketPrice = priceOfTicket;
        this.maxTicketAvailability = maximumAvailability;
    }

    public String getTicketType(){
        return this.ticketType;
    }

    public double getTicketPrice(){
        return this.ticketPrice;
    }

    public int getAvailability(){
        return this.ticketAvailability;
    }

    public int getMaxAvailability(){
        return this.maxTicketAvailability;
    }

    public void setTicketType(String newTicketType){
        this.ticketType = newTicketType;
    }

    public void setTicketPrice(double newTicketPrice){
        this.ticketPrice = newTicketPrice;
    }

    public void setTicketAvailability(int newTicketAvailability){
        if(ticketAvailability <= maxTicketAvailability){
            this.ticketAvailability = newTicketAvailability;
        }
        System.err.println("Cannot exceed maximum availability.");
    }

    public void setMaxAvailability(int newMaxAvailability){     //set the max availability of tickets
        this.maxTicketAvailability = newMaxAvailability;
    }
}
