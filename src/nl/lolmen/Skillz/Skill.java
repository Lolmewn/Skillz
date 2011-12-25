package nl.lolmen.Skillz;

import java.util.HashMap;
import java.util.HashSet;

public class Skill
{
	private String name;
	private HashSet<Integer> ids = new HashSet<Integer>();
	private HashMap<Integer, Integer> idXP = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> idLVL = new HashMap<Integer, Integer>();
	private boolean enabled;
	private boolean allFromLvl0;
	private int xpMulti;
	private String itemReward;
	private int moneyReward;
	private int change;

	public Skill(String name)
	{
		setName(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void addItem(int ID) {
		if (ids.contains(Integer.valueOf(ID))) {
			return;
		}
		ids.add(ID);
	}

	public void addXP(int ID, int XP) {
		addItem(ID);
		idXP.put(ID, XP);
	}

	public void addLVL(int ID, int lvl) {
		addItem(ID);
		idLVL.put(ID, lvl);
	}

	public boolean hasItem(int ID) {
		return ids.contains(ID);
	}

	public boolean hasCustomXP(int ID) {
		return idXP.containsKey(ID);
	}

	public int getCustomXP(int ID) {
		if (!hasCustomXP(ID)) {
			return 1;
		}
		return idXP.get(ID);
	}

	public boolean hasCustomLevel(int ID) {
		return idLVL.containsKey(ID);
	}

	public int getCustomLevel(int ID) {
		if (!hasCustomLevel(ID)) {
			return 0;
		}
		return idLVL.get(ID);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isAllFromLvl0() {
		return allFromLvl0;
	}
	public void setAllFromLvl0(boolean allFromLvl0) {
		this.allFromLvl0 = allFromLvl0;
	}
	public int getXpMulti() {
		return xpMulti;
	}
	public void setXpMulti(int xpMulti) {
		this.xpMulti = xpMulti;
	}
	public String getItemReward() {
		return itemReward;
	}
	public void setItemReward(String itemReward) {
		this.itemReward = itemReward;
	}
	public int getMoneyReward() {
		return moneyReward;
	}
	public void setMoneyReward(int moneyReward) {
		this.moneyReward = moneyReward;
	}
	public int getChange() {
		return change;
	}
	public void setChange(int change) {
		this.change = change;
	}
}