package world;

import rescueagents.RobotControl;
import rescueframework.AbstractRobotControl;
import rescueframework.RescueFramework;

/**
 * Robot on the map
 */
public class Ambulance {
    // Location of the robot
    private Cell location;
    private boolean sirenRed = false;
    
    // The injured being carried by the robot
    private Injured injured = null;
    private boolean allocated = false;
    private Station station = null;
    // The control object of th robot
    private AbstractRobotControl control;
    private static int nextID = 1;
    private String id;
    private static int speed;
    
    public static void setSpeed(int i) {
    	speed = i;
    }
    public static int getSpeed() {
    	return speed;
    }
    public static void resetNextId() {
    	nextID = 1;
    }
    
    /**
     * Default constructor
     * 
     * @param startCell     The start cell of the robot
     * @param percepcion    Percepcion of the robot
     */
    public Ambulance(Cell startCell, RobotPercepcion percepcion) {
        location = startCell;
        control = new RobotControl(this, percepcion);
     // Generate unique ID
        id = "ambulance" + nextID;
        nextID++;
    }
    public String getId() {
 	   return id;
    }
    public boolean isAllocated() {
    	return allocated;
    }
    public void setAllocated(boolean a) {
    	allocated = a;
    }
    public void setStation(Station s) {
    	station = s;
    }
    /**
     * Return the robot location
     * @return      The robot location
     */
    public Cell getLocation() {
        return location;
    }
    public boolean getSirenRed() {
    	return sirenRed;
    }
    /**
     * Set the location of the robot
     * @param newLocation       The new location of the robot
     */
    public void setCell(Cell newLocation) {
    	sirenRed = !sirenRed;
        location = newLocation;
    }
    
    /**
     * Return true if the robot is currently carrying an injured
     * 
     * @return      True if the robot is carrying an injured
     */
    public boolean hasInjured() {
        return injured != null;
    }
    
    /**
     * Pick up an injured if the robot does not carries any
     */
    public void pickupInjured() {
        if (injured == null) {
            injured = location.getInjured();
            RescueFramework.log("Picking up injured: "+injured.toString());
            location.setInjured(null);
        } else {
            RescueFramework.log("Unable to pick up inured: already has one.");
        }
    }
    
    /**
     * Drop the injured if the robot carries one
     * 
     * @return      Return the injured that is dropped
     */
    public Injured dropInjured() {
        RescueFramework.log("Dropping injured "+injured.toString()+" at "+location.toString());
        Injured result = injured;
        allocated = false;
        injured = null;
        return result;
    }
    
    /**
     * Return the injured being carried by the robot
     * 
     * @return  The injured being carried by the robot
     */
    public Injured getInjured() {
        return injured;
    }
    
    /**
     * Call the AbstractRobotControl to decide the next step of the robot
     * 
     * @return      Stepping direction of the robot or NULL to stay in place
     */
    public Integer step() {    	
        if (control == null) return null;
        return control.step();
    }
}
