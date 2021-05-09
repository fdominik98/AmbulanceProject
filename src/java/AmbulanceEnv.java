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
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
				int y = (int)((NumberTerm)action.getTerm(1)).solve();
				countStationBid(agName , x ,y );          	
			} catch (NoValueException e) {
				e.printStackTrace();
			}         	
        }
        else if(action.getFunctor().equals("countAmbulance")) {       	
			try {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
				int y = (int)((NumberTerm)action.getTerm(1)).solve();
				countAmbulanceBid(agName , x ,y );          	
			} catch (NoValueException e) {
				e.printStackTrace();
			}
        }
        else if(action.getFunctor().equals("countHospital")) {       	
			try {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
				int y = (int)((NumberTerm)action.getTerm(1)).solve();
				countHospitalBid(agName , x ,y );          	
			} catch (NoValueException e) {
				e.printStackTrace();
			}
        }
        else if(action.getFunctor().equals("goToHospital")) {         			
  				String h = ((Atom)action.getTerm(0)).toString(); 
  				takeInjuredToHospital(agName,h);
          }
        else if(action.getFunctor().equals("saveInjured")) {       	
  			try {
  				int x = (int)((NumberTerm)action.getTerm(0)).solve();
  				int y = (int)((NumberTerm)action.getTerm(1)).solve();
  				saveInjured(agName , x ,y ); 				
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
    
    public void callCheck(String agName) {
    	for(int i = 0; i< map.getInjureds().size(); i++) {
    		Injured injured = map.getInjureds().get(i);
    		if(!injured.getBeingSaved()) {
    			//logger.info(agName + " received a call");      
    			removePercept(agName,Literal.parseLiteral("injured("+injured.getLocation().getX()+","+injured.getLocation().getY()+")"));
    			addPercept(agName,Literal.parseLiteral("injured("+injured.getLocation().getX()+","+injured.getLocation().getY()+")"));
    			//injured.beingSaved();
    		}
    		
    	}    	
    }
    public void countStationBid(String agName, int x, int y) {   
    	Station s = map.getStationById(agName);
    	if(s != null) {    		
    		CopyOnWriteArrayList<Ambulance> as= s.getAmbulances();
    		for(Ambulance a : as) {
    			//logger.info(a.getId()+" needed.");     	
    			removePercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+","+x+","+y+")"));       
    			addPercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+","+x+","+y+")"));     			
    		}
    	}
		
    }
    public void countAmbulanceBid(String agName , int x, int y) {    	
    	  	
    	Cell injured = map.getCell(x, y);
    	Ambulance a = map.getAmbulanceById(agName);
    	
    	if(a != null) {    		
    		if(!a.isAllocated()) {    		
	       		logger.info(agName + ": "+a.getLocation().getX() + "," +a.getLocation().getY());
	       		AStarSearch asc = new AStarSearch();
	       		Path p = asc.search(a.getLocation(),injured,-1);
	       		if (p != null) {	       			
	       		//	logger.info(agName + " counted a bid: "+p.getLength());	  
	       			removePercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));
	       			addPercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));  
	       			return;
	       		}    	
    		}   
    	}
    	removePercept(agName,Literal.parseLiteral("bid("+1000000+")"));  
		addPercept(agName,Literal.parseLiteral("bid("+1000000+")")); 
    	 	
    }
    public void countHospitalBid(String agName , int x, int y) {    	
	  	
    	Cell injured = map.getCell(x, y);
    	Hospital h = map.getHospitalById(agName);	
    	if(h != null) {
    		int bid = 0;
    		if(h.isFull()) {  
    			bid += 100000;
    		}
    		   		
       		logger.info(agName + ": "+h.getLocation().getX() + "," +h.getLocation().getY());
       		AStarSearch asc = new AStarSearch();
       		Path p = asc.search(h.getLocation(),injured,-1);
       		if (p != null) {	      
       			bid += p.getLength();
       		//	logger.info(agName + " counted a bid: "+p.getLength());	  
	       	    	
       		removePercept(agName,Literal.parseLiteral("bid("+bid+")"));
       		addPercept(agName,Literal.parseLiteral("bid("+bid+")"));  
    		}   
    	}
    	 	
    }
    public void saveInjured(String agName, int x, int y) {
    	Cell injured = map.getCell(x, y);
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
    					map.stepTime();
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
    		                searchForHospital(agName, a.getLocation());
    		                map.stepTime();
    		        }else {
    		        	a.setAllocated(false);
    		        }
    				
    			}    		
    		}    		
    	}    	
    }
    public void searchForHospital(String agName, Cell loc) {
    	for(Hospital h : map.getHospitals()) {
    		removePercept(agName,Literal.parseLiteral("neededHospital("+h.getId()+","+loc.getX()+","+loc.getY()+")"));       
			addPercept(agName,Literal.parseLiteral("neededHospital("+h.getId()+","+loc.getX()+","+loc.getY()+")"));        			
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
				map.stepTime();
				try {
					Thread.sleep(Ambulance.getSpeed());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   					
			}		
	        
	        if (a.hasInjured() && a.getLocation().getHospital()!= null) {
	        
        		while(true) {
        			if(!h.isFull()) {
        				Injured savedInjured = a.dropInjured();
        				savedInjured.setSaved();
        				h.addInjured(savedInjured);
        				map.getSavedInjureds().add(savedInjured);
        				map.stepTime();
        				break;
        			}			      
        		} 	       		
	        	
	        	removePercept(agName,Literal.parseLiteral("ambulanceReleased("+agName+")"));       
				addPercept(agName,Literal.parseLiteral("ambulanceReleased("+agName+")"));  
	        }
    	}
    }
    
}

