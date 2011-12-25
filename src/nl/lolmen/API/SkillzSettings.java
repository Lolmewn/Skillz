package nl.lolmen.API;

import nl.lolmen.Skillz.Skillz;

public class SkillzSettings {
	
private Skillz plugin;
	
	public SkillzSettings(Skillz plugin) {
		this.plugin = plugin;
	}

	public boolean usePermissions(){
		return plugin.usePerms;
	}
	
	public boolean useiConomy(){
		return plugin.useIco;
	}
	
	public boolean use3Co(){
		return plugin.use3Co;
	}
	
	public boolean useBOSE(){
		return plugin.useBOSE;
	}
	
	public boolean useMySQL(){
		return plugin.useMySQL;
	}
	
	public boolean useSQLite(){
		return plugin.useSQL;
	}
	
	public int moneyReward(){
		return plugin.reward;
	}
	
	/**
	 * 
	 * @return String, In the form of ID,AMOUNT - given on Levelup
	 * 
	 */
	public String itemReward(){
		return plugin.itemreward;
	}
	
	public void setMoneyReward(int money){
		plugin.reward = money;
	}
	
	public void setItemReward(String string){
		if(!string.contains(",")){
			plugin.log.info("[Skillz] Failed to set ItemReward, string does not contain , !");
			return;
		}
		String[] split = string.split(",");
		if(!(split.length == 2)){
			plugin.log.info("[Skillz] Failed to set ItemReward, Too many arguments, or only one!");
			return;
		}
		if(!isInt(split[0])){
			plugin.log.info("[Skillz] Failed to set ItemReward, strings ID is not an int!");
			return;
		}
		if(!isInt(split[1])){
			plugin.log.info("[Skillz] Failed to set ItemReward, strings AMOUNT is not an int!");
			return;
		}
		plugin.itemreward = string;
	}
	
	private boolean isInt(String i){
		try{
			@SuppressWarnings("unused")
			int a = Integer.parseInt(i);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
