package world;

import java.util.ArrayList;

import rescueframework.RescueFramework;

public class Station {



   private ArrayList<Ambulance> ambulances = new ArrayList<>();

   private Cell location = null;
   // Static ID value of the next injured
   private static int nextID = 1;
   // ID of the injured object
   private int id;

   public Cell getLocation() {
       return location;
   }

   public void setLocation(Cell location) {
       this.location = location;
   }
   

   public Station(Cell cell,ArrayList<Ambulance> a) {
      for(Ambulance am : a) {
    	  am.setStation(this);
      }
	   ambulances = a;
    	  location = cell;
       
       // Generate unique ID
       id = nextID;
       nextID++;
   }

}
