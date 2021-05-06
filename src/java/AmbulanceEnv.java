// Environment code for project proba2

import jason.NoValueException;
import jason.asSyntax.*;
import jason.environment.*;
import rescueframework.RescueFramework;
import world.AStarSearch;
import world.Ambulance;
import world.Cell;
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

	
    public static final Term    callCheck = Literal.parseLiteral("checkIfHas(call)"); 
    public static final Term    countStationBid = Literal.parseLiteral("countStation(bid)"); 

 
    
    private Logger logger = Logger.getLogger("ambulanceProject."+AmbulanceEnv.class.getName());
    private Map map;
    
    public AmbulanceEnv() {
    	RescueFramework.start();    	
    }

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);        
        try {
			addPercept(ASSyntax.parseLiteral("percept(demo)"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    @Override
    public boolean executeAction(String agName, Structure action) {        
    	map = RescueFramework.getMap();       
        if (action.equals(callCheck)) { // you may improve this condition
        	callCheck(agName);
        }
        else if(action.equals(countStationBid)) {          	
        	countStationBid(agName);          	
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
    			//i.beingSaved();
    		}
    		
    	}    	
    }
    public void countStationBid(String agName) {   
    	Station s = map.getStationById(agName);
    	if(s != null) {    		
    		CopyOnWriteArrayList<Ambulance> as= s.getAmbulances();
    		for(Ambulance a : as) {
    			//logger.info(a.getId()+" needed.");     	
    			removePercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+")"));       
    			addPercept(agName,Literal.parseLiteral("neededAmbulance("+a.getId()+")"));        				
    		}
    	}
		
    }
    public void countAmbulanceBid(String agName , int x, int y) {    	
    	  	
    	Cell injured = map.getCell(x, y);
    	Ambulance a = map.getAmbulanceById(agName);	
    	if(a != null) {
    		if(a.isAllocated()) {  
    			removePercept(agName,Literal.parseLiteral("bid("+1000000+")"));  
    			addPercept(agName,Literal.parseLiteral("bid("+1000000+")"));  
    		}
    		else {    		
	       		logger.info(agName + ": "+a.getLocation().getX() + "," +a.getLocation().getY());
	       		AStarSearch asc = new AStarSearch();
	       		Path p = asc.search(a.getLocation(),injured,-1);
	       		if (p != null) {	       			
	       		//	logger.info(agName + " counted a bid: "+p.getLength());	  
	       			removePercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));
	       			addPercept(agName,Literal.parseLiteral("bid("+p.getLength()+")"));  
	       		}    	
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
    		        }else {
    		        	a.setAllocated(false);
    		        }
    				
    			}    		
    		}    		
    	}    	
    }
    
}

