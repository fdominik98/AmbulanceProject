// Environment code for project proba2

import jason.NoValueException;
import jason.asSyntax.*;
import jason.environment.*;
import rescueframework.RescueFramework;
import world.AStarSearch;
import world.Ambulance;
import world.Cell;
import world.Hospital;
import world.Injured;
import world.Map;
import world.Path;
import world.Station;
import jason.asSyntax.parser.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.*;


public class AmbulanceEnv extends Environment { 
	
	
    private Logger logger = Logger.getLogger("ambulanceProject."+AmbulanceEnv.class.getName());
    private Map map;   
    public AmbulanceEnv() {    	
    	RescueFramework.start();    	
    	
    }

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);  
        
       
    }

    @Override
    public boolean executeAction(String agName, Structure action) {   	
    	
    	map = RescueFramework.getMap(); 
    	
        if (action.getFunctor().equals("checkIfHas")) { // you may improve this condition        	
        	callCheck(agName);
        }
        else if(action.getFunctor().equals("countStation")) {          	
        	try {	
				int id = (int)((NumberTerm)action.getTerm(0)).solve();
				countStationBid(agName,id );          	
			} catch (NoValueException e) {
				e.printStackTrace();
			}         	
        }
        else if(action.getFunctor().equals("countAmbulance")) {       	
			try {
				int id = (int)((NumberTerm)action.getTerm(0)).solve();				
				countAmbulanceBid(agName,id);          	
			} catch (NoValueException e) {
				e.printStackTrace();
			}
        }
        else if(action.getFunctor().equals("countHospital")) {   
		
				String id = ((Atom)action.getTerm(0)).toString();		
				countHospitalBid(agName,id);          	
		
        }
        else if(action.getFunctor().equals("goToHospital")) {         			
  				String h = ((Atom)action.getTerm(0)).toString(); 
  				takeInjuredToHospital(agName,h);
          }
        else if(action.getFunctor().equals("saveInjured")) {       	
  			try {
  				Literal l = (Literal)action.getTerm(0);
  				int id = (int)((NumberTerm)l.getTerm(0)).solve();  				
  				saveInjured(agName,id); 				
  			} catch (NoValueException e) {
  				e.printStackTrace();
  			}
          }
        else if(action.getFunctor().equals("setNewId")) {       	
  			try {
  				Literal l = (Literal)action.getTerm(0);
  				int id = (int)((NumberTerm)l.getTerm(0)).solve();
  				map.getInjuredById(id).changeId();
  						
  			} catch (NoValueException e) {
  				e.printStackTrace();
  			}
          }
        else if(action.getFunctor().equals("removePercept")) {    		
        	LiteralImpl P = ((LiteralImpl)action.getTerm(0));    			   				     	
 				removePercept(agName,P);     		
        }
        else {
        	return false;
        }
        return true; // the action was executed with success
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    
    public void callCheck(String agName){
    	for(int i = 0; i< map.getInjureds().size(); i++) {
    	
    		Injured injured = map.getInjureds().get(i);
    		if(!injured.getBeingSaved()) {
    			if(injured != null) {
    				//logger.info(agName + " received a call");     
    				try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				addPercept(agName,Literal.parseLiteral("injured("+injured.getId()+")"));
    				//injured.beingSaved();    		
    			}
    		}
    		
    	}    	
    }
    public void countStationBid(String agName, int injuredId) {   
    	Station s = map.getStationById(agName);
    	if(s != null) {    		
    		CopyOnWriteArrayList<Ambulance> as= s.getAmbulances();
    		for(Ambulance a : as) {
    			//logger.info(a.getId()+" needed.");     	
    			//removePercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+","+x+","+y+")"));       
    			addPercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+","+injuredId+")"));     			
    		}
    	}
		
    }
    public void countAmbulanceBid(String agName , int injuredId) {    	
    	  	
    	Cell injured = map.getInjuredById(injuredId).getLocation();
    	Ambulance a = map.getAmbulanceById(agName);
    	
    	if(a != null) {    		
    		if(!a.isAllocated()) {    	       		
	       		AStarSearch asc = new AStarSearch();
	       		Path p = asc.search(a.getLocation(),injured,-1);
	       		if (p != null) {	       			
	       		//	logger.info(agName + " counted a bid: "+p.getLength());	  
	       			//removePercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));
	       			addPercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));  
	       			return;
	       		}    	
    		}   
    	}    	
		addPercept(agName,Literal.parseLiteral("bid("+1000000+")")); 
    	 	
    }
    public void countHospitalBid(String agName , String ambulanceId) {    	
	  	
    	Cell ambulance = map.getAmbulanceById(ambulanceId).getLocation();    		  
    	Hospital h = map.getHospitalById(agName);	
    	if(h != null) {
    		int bid = 0;
    		if(h.isFull()) {  
    			bid += 100000;
    		}
       		AStarSearch asc = new AStarSearch();
       		Path p = asc.search(h.getLocation(),ambulance,-1);
       		if (p != null) {	      
       			bid += p.getLength();
       		//	logger.info(agName + " counted a bid: "+p.getLength());	  
	       	    	
       		//removePercept(agName,Literal.parseLiteral("bid("+bid+")"));
       		addPercept(agName,Literal.parseLiteral("bid("+bid+")"));  
    		}   
    	}
    	 	
    }
    public void saveInjured(String agName, int injuredId) {
    	Cell injured = map.getInjuredById(injuredId).getLocation();
    	if(injured.getInjured()!= null) {
    		Ambulance a = map.getAmbulanceById(agName);	
    		if(a != null) {
    			if(!a.isAllocated()) {
    				injured.getInjured().beingSaved();
    				a.setAllocated(true);    			
    				AStarSearch asc = new AStarSearch();
    				Path p = asc.search(a.getLocation(),injured,-1);
    				for(int i = 0; i<p.getLength();i++) {
    					map.moveRobot(a, p.getPath().get(i));
    					map.stepTime(false);
    					try {
    						Thread.sleep(Ambulance.getSpeed());
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}   					
    				}
    			      // Pick up new injured
    		        if (a.getLocation().hasInjured()) {    		            
    		                a.getLocation().getInjured().setLocation(null);
    		                a.pickupInjured();    	
    		                searchForHospital(agName,a.getInjured().getId());
    		                map.stepTime(false);
    		        }else {
    		        	a.setAllocated(false);
    		        }
    				
    			}    		
    		}    		
    	}    	
    }
    public void searchForHospital(String agName,int injuredId) {
    	for(Hospital h : map.getHospitals()) {    		     
			addPercept(agName,Literal.parseLiteral("neededHospital("+h.getId()+","+injuredId+")"));        			
    	}
    	
    }
    public void takeInjuredToHospital(String agName, String hospital){
    	Hospital h = map.getHospitalById(hospital);
    	Ambulance a = map.getAmbulanceById(agName);
    	if(h!= null && a != null) {
    		AStarSearch asc = new AStarSearch();
			Path p = asc.search(a.getLocation(),h.getLocation(),-1);
			for(int i = 0; i<p.getLength();i++) {
				map.moveRobot(a, p.getPath().get(i));
				map.stepTime(false);
				try {
					Thread.sleep(Ambulance.getSpeed());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   					
			}		
	        
	        if (a.hasInjured() && a.getLocation().getHospital()!= null) {
	        	logger.info(agName + " a korhaznal vagyok");
        		while(true) {
        			//logger.info(agName + " letevo ciklusban vagyok");
        			if(!h.isFull()) {
        			
        				logger.info(agName + " letszem");
        				Injured savedInjured = a.dropInjured();
        				savedInjured.setSaved();
        				h.addInjured(savedInjured);
        				map.getSavedInjureds().add(savedInjured);
        				map.stepTime(false);
        				break;
        			}
        			try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}        	
	        	
	        }
    	}
    }
    
}

