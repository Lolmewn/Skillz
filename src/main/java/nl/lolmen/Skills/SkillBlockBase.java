package nl.lolmen.Skills;

import java.util.HashMap;
import nl.lolmen.Skillz.Skillz;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class SkillBlockBase extends SkillBase {

    public SkillBlockBase(Skillz plugin) {
        super(plugin);
    }
    private boolean allFromFirstLevel;
    private HashMap<ItemStack, Integer> blocks = new HashMap<ItemStack, Integer>();
    private HashMap<ItemStack, Integer> blockLevels = new HashMap<ItemStack, Integer>();

    public boolean isAllFromFirstLevel() {
        return allFromFirstLevel;
    }

    public void setAllFromFirstLevel(boolean allFromFirstLevel) {
        this.allFromFirstLevel = allFromFirstLevel;
    }

    public HashMap<ItemStack, Integer> getBlockLevels() {
        return blockLevels;
    }

    public void addBlockLevels(ItemStack block, int level) {
        blockLevels.put(block, level);
    }

    public void addBlock(ItemStack block, int xp) {
        blocks.put(block, xp);
    }

    public void setBlocks(HashMap<ItemStack, Integer> blocks) {
        this.blocks = blocks;
    }

    /**
     * @param data BlockData
     */
    public boolean hasBlock(int block, byte data) {
        return blocks.containsKey(new ItemStack(block, 1, data));
    }

    public boolean hasBlock(Block b) {
        return hasBlock(b.getTypeId(), b.getData());
    }

    public int getXP(int block, byte data) {
        if (blocks.containsKey(new ItemStack(block, 1, data))) {
            return blocks.get(new ItemStack(block, 1, data));
        }
        return 0;
    }

    public int getXP(Block block) {
        return this.getXP(block.getTypeId(), block.getData());
    }

    public int getLevelNeeded(ItemStack block) {
        if (blockLevels.containsKey(block)) {
            return blockLevels.get(block);
        }
        return 0;
    }

    public int getLevelNeeded(Block b) {
        return getLevelNeeded(new ItemStack(b.getType(), 1, b.getData()));
    }
}
