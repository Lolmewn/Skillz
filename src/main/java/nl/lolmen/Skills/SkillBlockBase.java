package nl.lolmen.Skills;

import java.util.HashMap;

import org.bukkit.block.Block;

public class SkillBlockBase extends SkillBase{

	private boolean allFromFirstLevel;
	private HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> blockLevels = new HashMap<Integer, Integer>();
	
	public boolean isAllFromFirstLevel() {
		return allFromFirstLevel;
	}
	public void setAllFromFirstLevel(boolean allFromFirstLevel) {
		this.allFromFirstLevel = allFromFirstLevel;
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
