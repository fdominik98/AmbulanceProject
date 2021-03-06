package world;

/**
 * Class representing injured people on the map
 */
public class Injured {
    // Healt level
    private int health = 1000;
    // True if the injured have already been transported to an exit cell
    private boolean saved = false;
    private boolean beingSaved = false;
    // Cell location of the injured
    private Cell location = null;
    // Static ID value of the next injured
    private static int nextID = 1;
    // ID of the injured object
    private int id;

    public void beingSaved() {
    	beingSaved = true;
    }
    public boolean getBeingSaved() {
    	return beingSaved;
    }
    /**
     * Returns the location cell of the injured
     * @return      The location cell of the injured
     */
    public Cell getLocation() {
        return location;
    }

    /**
     * Set the location cell of the injured
     * @param location      The location cell of the injured
     */
    public void setLocation(Cell location) {
        this.location = location;
    }
    
    
    /**
     * Default constructor
     * 
     * @param health        Health level of the new injured
     */
    public Injured(int health) {
        // Save health
        this.health = health;
        
        // Generate unique ID
        id = nextID;
        nextID++;
    }
    public void changeId() {
    	id = nextID;
    	nextID++;
    }
    
    /**
     * Return the health value of the injured
     * @return      Health value of the injured
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Set the health value of the injured
     * @param health        Health value of the injured
     */
    public void setHealth(int health) {
        this.health = health;
    }
    
    /**
     * Mark the injured as saved
     */
    public void setSaved() {
        saved = true;
    }
    
    /**
     * Returns true if the injured is already saved
     * 
     * @return      True if the injured is already saved
     */
    public boolean isSaved() {
        return saved;
    }
    
    /**
     * Returns the health level in percents
     * @return      The health level in percents
     */
    public float getHealthRatio() {
        return (float)health/1000F;
    }    
  
    
    /**
     * Returns true if the injured is still alive
     * @return      True if the injured is still alive
     */
    public boolean isAlive() {
        return health>0;
    }
    
    /**
     * Return the injured ID and health level in a string
     * @return      The injured ID and health level in a string 
     */
    public String toString() {
        return "ID="+id+" with health "+health;
    } 
    public int getId() {
    	return id;
    }

    
}
