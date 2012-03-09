package nl.lolmen.Skills.skills;

import java.util.HashMap;

import org.bukkit.block.Block;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skillz.Skillz;

public class CustomSkill extends SkillBase{
	
	public CustomSkill(Skillz plugin) {
		super(plugin);
	}

	private String uses;
	private HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> blockLevels = new HashMap<Integer, Integer>();

	public String getUses() {
		return uses;
	}
	
	public String[] getUsesArray(){
		if(!uses.contains(",")){
			return new String[]{uses};
		}
		return uses.split(",");
	}

	public void setUses(String uses) {
		this.uses = uses;
	}
	
	public boolean hasUse(String eventType){
		if(!uses.contains(",")){
			return uses.equalsIgnoreCase(eventType) ? true : false;
		}
		for(String use : uses.split(",")){
			if(use.equalsIgnoreCase(eventType)){
				return true;
			}
		}
		return false;
	}
	
	public HashMap<Integer, Integer> getBlockLevels() {
		return blockLevels;
	}
	
	public void addBlockLevels(int block, int level){
		blockLevels.put(block, level);
	}
	
	public void addBlock(int block, int xp){
		blocks.put(block, xp);
	}
	
	public void setBlocks(HashMap<Integer, Integer> blocks) {
		this.blocks = blocks;
	}
	
	public boolean hasBlock(int block, byte data){
		return blocks.containsKey(block);
	}
	
	public boolean hasBlock(Block b){
		return hasBlock(b.getTypeId(), b.getData());
	}
	
	public int getXP(int block){
		if(blocks.containsKey(block)){
			return blocks.get(block);
		}else{
			return 0;
		}
	}
	
	public int getXP(Block block){
		return this.getXP(block.getTypeId());
	}
	
	public int getLevelNeeded(int blockID){
		if(blockLevels.containsKey(blockID)){
			return blockLevels.get(blockID);
		}else{
			return 0;
		}
	}
	
	public int getLevelNeeded(Block b){
		return getLevelNeeded(b.getTypeId());
	}

}
