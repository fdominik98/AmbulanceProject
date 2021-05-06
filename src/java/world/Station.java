package world;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import rescueframework.RescueFramework;

public class Station {



   private CopyOnWriteArrayList<Ambulance> ambulances = new CopyOnWriteArrayList<Ambulance>();

   private Cell location = null;
   // Static ID value of the next injured
   private static int nextID = 1;
   // ID of the injured object
   private String id;

   public Cell getLocation() {
       return location;
   }
   public static void resetNextId() {
   	nextID = 1;
   }
   public void setLocation(Cell location) {
       this.location = location;
   }
   

   public Station(Cell cell,CopyOnWriteArrayList<Ambulance> a) {
      for(Ambulance am : a) {
    	  am.setStation(this);
      }
	   ambulances = a;
    	  location = cell;
       
       // Generate unique ID
       id = "station" + nextID;
       nextID++;
   }
   public String getId() {
	   return id;
   }
   public Ambulance getAmbulanceById(String id) {
   	for(Ambulance a: ambulances) {
   		if(a.getId().equals(id))
   			return a;
   	}
   	return null;
   }
   public CopyOnWriteArrayList<Ambulance> getAmbulances(){
	   return ambulances;
   }

}
