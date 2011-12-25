package nl.lolmen.Skills;

import nl.lolmen.Skills.skills.Mining;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

public class SkillBlockListener extends BlockListener{

	public SkillBlockListener() {

	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		for (SkillBase base : SkillManager.getSkills()) {
			if (base instanceof SkillBlockBase) {
				SkillBlockBase s = (SkillBlockBase) base;
				if (!s.isEnabled()) {
					continue;
				}
				handleSkill(s, event);
			}
		}
	}

	private void handleSkill(SkillBlockBase s, BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (s.hasBlock(event.getBlock())) {
			int lvlneeded = s.getLevelNeeded(event.getBlock());
			if (!s.isAllFromFirstLevel() && CPU.getLevel(p, s) < lvlneeded) {
				p.sendMessage("You are not allowed to mine this block! "
						+ s.getSkillName().substring(0, 1).toUpperCase()
						+ s.getSkillName().substring(1).toLowerCase()
						+ " level needed:" + lvlneeded);
				event.setCancelled(true);
			}
			int xpget = s.getXP(event.getBlock().getTypeId())
					* s.getMultiplier();
			CPU.addXP(p, s, xpget);
			if (s instanceof Mining) {
				// Change calculating here
				Mining m = (Mining) s;
				if (m.getWillDoubleDrop(p)) {
					ItemStack stack = BlockDrop.getDrop(event.getBlock());
					p.getWorld().dropItemNaturally(
							event.getBlock().getLocation(), stack);
				}
			}
		}
	}

}
