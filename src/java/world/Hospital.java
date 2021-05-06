package world;

import java.util.ArrayList;

public class Hospital {

	 // Healt level
    private int capacity;
    private ArrayList<Injured> injureds = new ArrayList<>();

    private Cell location = null;
    // Static ID value of the next injured
    private static int nextID = 1;
    // ID of the injured object
    private String id;

    public static void resetNextId() {
    	nextID = 1;
    }
    public Cell getLocation() {
        return location;
    }

    public void setLocation(Cell location) {
        this.location = location;
    }
    

    public Hospital(Cell cell, int capacity) {
        location = cell;
        this.capacity = capacity;
        
        // Generate unique ID
        id = "hospital" + nextID;
        nextID++;
    }
    

    public int getCapacity() {
        return capacity;
    }
    

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }  

  
    public void addInjured(Injured i) {
    	injureds.add(i);
    }
    public void healInjureds() {
    	for(int i = 0; i < injureds.size();i++) {
    		Injured injured = injureds.get(i);
    		injured.setHealth(injured.getHealth()+50);
    		if(injured.getHealth() >= 1000) {
    			injured.setHealth(1000);
    			injureds.remove(i);
    			i--;
    		}
    	}
    }
    public boolean isFull() {
    	return injureds.size() >= capacity;
    }
    public String getId() {
 	   return id;
    }
    
    
}
