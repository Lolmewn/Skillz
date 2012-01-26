package nl.lolmen.API;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

@SuppressWarnings("deprecation")
public class SkillzEventListener extends CustomEventListener{

	@Override
    public void onCustomEvent(Event event) {
        if (!(event instanceof SkillzLevelEvent)){
            return;
        }
        onLevel((SkillzLevelEvent)event);
	}
	
	public void onLevel(SkillzLevelEvent event){
		
	}
	
	public void onXPGain(SkillzXPGainEvent event){
		
	}
	
}
