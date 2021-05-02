package world;

import java.util.ArrayList;

/**
 * Interface defining the access function of the robot towards the map
 */
public interface RobotPercepcion {
    // Return the simulation time
    public int getTime();

    // Return all exit cells
    public ArrayList<Cell> getExitCells();

    // Return all discovered injured 
    public ArrayList<Injured> getInjureds();
    // Return all robots
    public ArrayList<Ambulance> getRobots();

    // Returns the shortest path to an exit cell
    public Path getShortestExitPath(Cell start);

    // Returns the shortest path to an injured
    public Path getShortestInjuredPath(Cell start);
}
