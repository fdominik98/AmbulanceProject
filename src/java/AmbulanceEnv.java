// Environment code for project proba2

import jason.asSyntax.*;
import jason.environment.*;
import rescueframework.RescueFramework;
import world.Injured;
import world.Map;
import jason.asSyntax.parser.*;

import java.util.logging.*;

public class AmbulanceEnv extends Environment {

	
    public static final Term    callAct = Literal.parseLiteral("actIfHasCall(call)");
    
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
        if (action.equals(callAct)) { // you may improve this condition
            checkForCalls(agName);
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
    
    public void checkForCalls(String agName) {
    	for(Injured i : map.getInjureds()) {
    		if(!i.getBeingSaved()) {
    			logger.info(agName + " received a call");
    			i.beingSaved();
    		}
    		
    	}
    	
    }
}

