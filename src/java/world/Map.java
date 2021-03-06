package world;

import world_debug.ViewLineBreakPoint;
import world_debug.ViewLine;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import rescueframework.RescueFramework;

/**
 * The map representing the state of the world
 */
public class Map implements RobotPercepcion{

    // Cell matrix of the map
    private Cell cells[][];
    // Dimensions of the map
    private int height = 0, width = 0;
    // Image cache for loading every image only once
    private HashMap<String,BufferedImage> imageCache = new HashMap<>();
    // Injured people on the map
    private ArrayList<Injured> injureds = new ArrayList<>();
    // Injureds already transported outside by the robots
    private ArrayList<Injured> savedInjureds = new ArrayList<>();
    // Exit cell of the map to transfer injureds to
    private ArrayList<Cell> exitCells = new ArrayList<>();
    // Robots operating on the map
    private ArrayList<Ambulance> ambulances = new ArrayList<>();
    private ArrayList<Hospital> hospitals = new ArrayList<>();
    private ArrayList<Station> stations = new ArrayList<>();
   
    // Viewline break points of the robots (for robot view debug)
    private ArrayList<ViewLineBreakPoint> viewLineBreakPoints = new ArrayList<>();
    // Path to be displayed on the GUI
    private ArrayList<Path> displayPaths = new ArrayList<>();
    // Simulation time
    private int time = 0;
    // Start cell specified for the robots

  
    private ArrayList<Cell> startCells = new ArrayList<>();
    
    /**
     * Default constructor
     * 
     * @param fileName          Text file to load the map from
     * @param robotCount        Robots to generate after loading of the map
     */
    public Map(String fileName, int ambulanceCount, int hospitalCapacity) {
        String line;
        String[] array;
        int mode = 0;
        int row = 0;
        ArrayList<Floor> floorList = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader (fileName));           
            while((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments
                if (line.startsWith("#") || line.startsWith("//") || line.isEmpty()) continue;
                
                if (mode == 0) {
                    // First line specifies map size
                    array = line.split(" ");
                    width = Integer.valueOf(array[0]);
                    height = Integer.valueOf(array[1]);
                    
                    cells = new Cell[width][height];
                    
                    mode = 1;
                } else if (mode == 1) {
                    // Process row definitions
                    array = line.split(" ");
                    if (array.length != width) {
                        throw new Exception("Invalid row specificaion, row width differs: "+width+" =/= "+array.length+" on line :"+line);
                    } else {
                        for (int i=0; i<width; i++) {
                            cells[i][row] = new Cell(i,row, array[i]);
                            if (array[i].equals("S")) startCells.add(cells[i][row]);
                            else if (array[i].equals("X")) exitCells.add(cells[i][row]);
                        }
                    }

                    row++;
                    if (row>=height) mode = 2;

                } else if (mode == 2) {
                    // Process other objects on the map (obstacles, injured, floor definitions)
                    array = line.split(" ");
                    if(array.length>=4 && array[0].startsWith("Station")) {
                    	int x = Integer.valueOf(array[1]);
                        int y = Integer.valueOf(array[2]);                        
                        Cell cell = getCell(x, y);
                        CopyOnWriteArrayList<Ambulance> a = new CopyOnWriteArrayList<Ambulance>();
                    	for(int i = 0; i < ambulanceCount;i++)
                    		a.add(new Ambulance(cell,this));
                    	ambulances.addAll(a);
                    	Station s = new Station(cell,a);                    	
                    	stations.add(s);
                    	cell.setStation(s);
                    	
                    }
                    else if(array.length>=4 && array[0].startsWith("Hospital")) {
                    	int x = Integer.valueOf(array[1]);
                        int y = Integer.valueOf(array[2]);                        
                        Cell cell = getCell(x, y);                  
                    	Hospital h = new Hospital(cell,hospitalCapacity);
                    	hospitals.add(h);
                    	cell.setHospital(h);
                    	
                    }
                    else if (array.length>=4 && array[0].startsWith("Floor")) {
                        // Floor definition found
                        floorList.add(new Floor(Integer.valueOf(array[1]),Integer.valueOf(array[2]),Integer.valueOf(array[3])));
                    } else if (array.length>=4 && array[0].startsWith("Obstacle")) {
                        // Obstacle defined
                        crateObstacle(Integer.valueOf(array[1]),Integer.valueOf(array[2]), array[3]);
                    } else if (array.length>=3 && array[0].startsWith("Injured")) {
                        // Injured defined
                        int injuries;
                        if (array.length>=4) {
                            // Load health level from file
                            injuries = Integer.valueOf(array[3])*ambulances.size();
                        } else {
                            // Generate random health level
                            injuries = (int)((float)Math.random()*1000F);
                        }
                        
                        // Find affected cell
                        int x = Integer.valueOf(array[1]);
                        int y = Integer.valueOf(array[2]);
                        Cell cell = getCell(x, y);
                        
                        // Create new injured and add to cell
                        Injured inj = new Injured(injuries);
                        cell.setInjured(inj);
                        inj.setLocation(cell);
                        injureds.add(inj);
                    } else {
                        RescueFramework.log("Unknown object definition skipped: "+line);
                    }
                }
            }
            
            reader.close();
        } catch (Exception e) {
            RescueFramework.log("Failed to load map from file: "+fileName);
            e.printStackTrace();
        }
        
        // Share walls between cells
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                cells[x][y].shareWalls(getCell(x,y-1), getCell(x+1,y), getCell(x,y+1), getCell(x-1,y));
            }
        }
        
        // Update cell neighbours
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                cells[x][y].updateAccessibleNeighbours();
            }
        }
        
        // Color floor for rooms
        for (int i=0; i<floorList.size(); i++) {
            floodFillFloor(floorList.get(i));
        }
        
        // Init agents       
        
        // Update agent visibility and repaint GUI
 
        RescueFramework.refresh();
    }
      
    /**
     * Get cached image identified by the string definition
     * @param image         String image definition
     * @return              Cached image
     */
    public BufferedImage getCachedImage(String image) {
        if (!imageCache.containsKey(image)) {
            // Image not yet cached
            try {
                BufferedImage img = ImageIO.read(new File("images/"+image+".png"));
                imageCache.put(image, img);
                return img;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }  
        } else {
            // Image found in the cache
            return imageCache.get(image);
        }
    }
    
    /**
     * Create new obstacle on the map
     * @param x         X coordinate
     * @param y         Y coordinate
     * @param image     The obstacle image
     */
    public void crateObstacle(int x, int y, String image) {
        cells[x][y].setObstacleImage(image);
    }
    
    /**
     * Return the height of the map
     * @return          The height of the map
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Return the width of the map
     * @return          The width of the map
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Return cell at a given position
     * @param x         X coordinate
     * @param y         Y coordinate
     * @return          The cell or null if the coordinates are invalid
     */
    public Cell getCell(int x, int y) {
        if (x>=0 && x<width && y>=0 && y<height)
            return cells[x][y];
        else
            return null;
    }
    
    /**
     * Flood fill block of cells
     * @param floor     The floor object to start filling from
     */
    private void floodFillFloor(Floor floor) {
        int index = 0;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        cells.add(getCell(floor.getX(), floor.getY()));
        
        // Loop through all acessible cells from the floor definition
        while (index<cells.size()) {
            Cell cell = cells.get(index);
            if (cell.isDoor()) {
                // Stop at doors
                index++;
                continue;
            }
            
            for (int direction = 0; direction<4; direction++) {
                Cell neighbour = cell.getAccessibleNeigbour(direction);
                if ( neighbour != null && cells.indexOf(neighbour) == -1) {
                    // Add all accessible neighbour
                    cells.add(neighbour);
                }
            }
            
            index++;
        }
        
        // Apply floor coloring to all cells found
        for (index=0; index<cells.size(); index++) {
            cells.get(index).setFloorColorIndex(floor.getColorCode());
        }
    }
    
    /**
     * Change the robot loation
     * @param robot         The robot to move
     * @param dir           The direction to move to
     * @return              True if the robot is able to move to the specified direction
     */
    public boolean moveRobot(Ambulance robot, Integer dir) {
        if (dir == null) {
            RescueFramework.log("Robot staying in place.");
            return false;
        }
        
        if (robot.getLocation().accessNeigbours[dir] != null) {
            return moveRobot(robot,robot.getLocation().accessNeigbours[dir]);
        } else {
            RescueFramework.log("Move failed: "+dir+" is inaccessible.");
            return false;
        }
    }
    
    /**
     * Change the robot location
     * @param robot         The robot to move
     * @param cell          The target cell to move the robot to
     * @return              True if the robot is able to move to the specified cell
     */
    public boolean moveRobot(Ambulance robot, Cell cell) { 
        // Avoid obstacles
        if (cell.hasObstacle()) {
            RescueFramework.log("Move failed: "+cell.getX()+" x "+cell.getY()+" is occupied by an obstacle.");
            return false;
        }       
       
        
        // Change location
        robot.setCell(cell);        
  
        
        // Update robot visibility and GUI       
        RescueFramework.refresh();
        return true;
    }  
    public ArrayList<Hospital> getHospitals(){
    	return hospitals;
    }
   

    
    /**
     * Make one step in time
     * 
     * @param stepRobots            If true robots are requetsed to step
     */
    public void stepTime(boolean incTime) {
    	if(incTime) {
    		for(Hospital h : hospitals)
    			h.healInjureds();
    		
    		time++;        
    		// Calculate injured states
    		for (int i=0; i<injureds.size(); i++) {
    			Injured injured = injureds.get(i);
    			if (!injured.isSaved()) {
    				int prevHealth = injured.getHealth();
    				if (prevHealth>0) {
    					prevHealth--;
    					injured.setHealth(prevHealth);
    				}
    			}
    		}
    		
    	}
        
        // Display robot paths
        displayPaths.clear();  

        RescueFramework.refresh();
    }
    
    public int getTime() {
        return time;
    }
    
    public Cell getPathFirstCell(Cell from, Cell to) {
        
        return null;
    }
    
    public ArrayList<Cell> getExitCells() {
        return exitCells;
    }
    
    public ArrayList<Injured> getInjureds() {        
        return injureds;
    }    
   
    public Path getShortestPath(Cell start, ArrayList<Cell> targetCells) {
        if (targetCells.size() == 0) {
            return null;
        }
        
        int bestLength = -1;
        AStarSearch asc = new AStarSearch();
        Path bestPath = asc.search(start, targetCells.get(0),-1);
        if (bestPath != null) bestLength = bestPath.getLength();
        
        for (int i=1; i<targetCells.size(); i++) {
            
            Path thisPath = asc.search(start, targetCells.get(i), bestLength);
            if (thisPath != null && (bestPath == null || thisPath.getLength()<bestPath.getLength())) {
                bestPath = thisPath;
                bestLength = bestPath.getLength();
            }
        }
        
        return bestPath;
    }
    
    
    public Path getShortestExitPath(Cell start) {
        return getShortestPath(start, exitCells);
    }

    
    public Path getShortestInjuredPath(Cell start) {
        ArrayList<Injured> knownInjuredList = getInjureds();
        ArrayList<Cell> cellList = new ArrayList<>();
        for (int i=0; i<knownInjuredList.size(); i++) {
            Cell location = knownInjuredList.get(i).getLocation();
            if (location != null) {
                cellList.add(location);
            }
        }
        return getShortestPath(start, cellList);
    }
    
    public int getScore() {
        int score = 0;
        for (int i=0; i<savedInjureds.size(); i++) {
            if (savedInjureds.get(i).isAlive()) score++;
        }
        return score;
    }
    
    public int getMaxScore() {
        return injureds.size();
    }
    
    public ArrayList<Ambulance> getRobots() {
        return ambulances;
    }
    
    public ArrayList<Injured> getSavedInjureds() {
        return savedInjureds;
    }
    
    public ArrayList<Path> getDisplayPaths() {
        return displayPaths;
    }
    public void addInjured(Injured i) {
    	injureds.add(i);
    }
    public Ambulance getAmbulanceById(String id) {
    	for(Ambulance a: ambulances) {
    		if(a.getId().equals(id))
    			return a;
    	}
    	return null;
    }
    public int getAmbulanceNumber() {
    	return ambulances.size();
    }
    public Station getStationById(String id) {
    	for(Station s: stations) {
    		if(s.getId().equals(id))
    			return s;
    	}
    	return null;
    }
    public Injured getInjuredById(int id) {
    	for(Injured i: injureds) {
    		if(i.getId() == id)
    			return i;
    	}
    	return null;
    }
    public ArrayList<Station> getStations(){
    	return stations;
    }
    public Hospital getHospitalById(String id) {
    	for(Hospital h: hospitals) {
    		if(h.getId().equals(id))
    			return h;
    	}
    	return null;
    }

    
}
