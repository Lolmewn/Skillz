package nl.lolmen.Skillz;

public class CPU {
	/*public static Skillz plugin;
	public CPU(Skillz skillz) {
		plugin = skillz;
	}
	static Logger log = Logger.getLogger("Minecraft");
	static Properties prop = new Properties();
	public iConomy iConomy;
	
	/**
	 * 
	 * @param Player p
	 * @param skills
	 * 
	 */
	/*
	public static void addXP(Player p, String skills){
		String skill = skills.toLowerCase();
		if(!plugin.perm.has(p, "skillz." + skill)){
			return;
		}
		int xp = 0;
		int lvl = 0;
		if((!plugin.useSQL) && (!plugin.useMySQL)){
			try{
				FileInputStream in = new FileInputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt");
				prop.load(in);
				if(!prop.containsKey(skill)){
					prop.put(skill, "0;0");
					prop.store(new FileOutputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"), "Skill=XP;lvl");				
				}
				String get = prop.getProperty(skill);
				String[] array = get.split(";");
				xp = Integer.parseInt(array[0]);
				lvl = Integer.parseInt(array[1]);
				prop.setProperty(skill, Integer.toString(xp+1) + ";" + Integer.toString(lvl));
				FileOutputStream out = new FileOutputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt");
				prop.store(out, "Skill=XP;lvl");
				checkLeveling(p, skill, lvl, (xp+1));
				in.close();
				out.flush();
				out.close();
				return;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		if(plugin.useSQL && (!plugin.useMySQL)){
			try {
				String query = "SELECT * FROM Skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
				ResultSet set = plugin.dbManager.query(query);
				boolean exists = set.next();
				if (!exists) {
					query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', '" + skill + "', 0, 0);";
					plugin.dbManager.query(query);
					query = "SELECT * FROM Skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
					set = plugin.dbManager.query(query);
				}
				while (set.next()) {
					xp = set.getInt("xp");
					lvl = set.getInt("level");
				}
				int back = 1 + xp;
				query = "UPDATE Skillz SET xp=" + back + " WHERE skill = '" + skill + "' AND player = '" + p.getName() + "';";
				plugin.dbManager.query(query);
				checkLeveling(p, skill, lvl, back);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if ((!plugin.useSQL) && (plugin.useMySQL)) {
			try {
				String query = "SELECT * FROM skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
				ResultSet set = plugin.mysql.query(query);
				if (set == null) {
					p.sendMessage("Something went Horribly wrong while processing this!");
					return;
				}
				while(set.next()){
					xp = set.getInt("xp");
					lvl = set.getInt("level");
				}
				int back = 1 + xp;
				query = "UPDATE Skillz SET xp=" + back + " WHERE skill = '" + skill + "' AND player = '" + p.getName() + "';";
				plugin.mysql.query(query);
				checkLeveling(p, skill, lvl, back);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(plugin.useSQL && plugin.useMySQL){
				plugin.log.info("[Skillz] SQL and MySQL are both enabled! Nothing happened!");
				p.sendMessage("Tell an admin the settings of Skillz are wrong!");
				return;
			}
		}
	}
	/**
	 * 
	 * @param Player p
	 * @param skills
	 * @param amount
	 */
	/*
	public static void addXP(Player p , String skills, int amount){
		if(!plugin.perm.has(p, "skillz." + skills.toLowerCase())){
			return;
		}
		for(int i = 0; i < amount; i++){
			addXP(p, skills);
		}
	}

	public static void checkLeveling(Player p , String skill, int lvl, int newXP){
		SkillzXPGainEvent ev;
		if(lvl == 0){
			ev = new SkillzXPGainEvent("SkillzXPGainEvent", p, skill, newXP, true);
			Bukkit.getServer().getPluginManager().callEvent(ev);
			if(ev.isCancelled()){
				return;
			}
			levelUp(p, skill, lvl+1);
		}else{
			double result = newXP/((lvl*lvl)*10);
			if(result >= 1){
				ev = new SkillzXPGainEvent("SkillzXPGainEvent", p, skill, newXP, true);
				Bukkit.getServer().getPluginManager().callEvent(ev);
				if(ev.isCancelled()){
					return;
				}
				levelUp(p, skill, lvl+1);
			}else{
				Bukkit.getServer().getPluginManager().callEvent(new SkillzXPGainEvent("SkillzXPGainEvent", p, skill, newXP, false));
				
			}
		}
	}
	
	public static void removeXP(Player p, String skills, int amount){
		String skill = skills.toLowerCase();
		int xp = 0;
		int lvl = 0;
		if((!plugin.useSQL) && (!plugin.useMySQL)){
			try{
				FileInputStream in = new FileInputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt");
				prop.load(in);
				if(!prop.containsKey(skill)){
					prop.put(skill, "0;0");
					prop.store(new FileOutputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"), "Skill=XP;lvl");				
					return;
				}
				String get = prop.getProperty(skill);
				String[] array = get.split(";");
				xp = Integer.parseInt(array[0]);
				lvl = Integer.parseInt(array[1]);
				prop.setProperty(skill, Integer.toString(xp-amount) + ";" + Integer.toString(lvl));
				FileOutputStream out = new FileOutputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt");
				prop.store(out, "Skill=XP;lvl");
				in.close();
				out.flush();
				out.close();
				return;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		if(plugin.useSQL && (!plugin.useMySQL)){
			try {
				String query = "SELECT * FROM Skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
				ResultSet set = plugin.dbManager.query(query);
				boolean exists = set.next();
				if (!exists) {
					query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', '" + skill + "', 0, 0);";
					plugin.dbManager.query(query);
					query = "SELECT * FROM Skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
					set = plugin.dbManager.query(query);
					return;
				}
				while (set.next()) {
					xp = set.getInt("xp");
					lvl = set.getInt("level");
				}
				int back = xp-amount;
				query = "UPDATE Skillz SET xp=" + back + " WHERE skill = '" + skill + "' AND player = '" + p.getName() + "';";
				plugin.dbManager.query(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if ((!plugin.useSQL) && (plugin.useMySQL)) {
			try {
				String query = "SELECT * FROM skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
				ResultSet set = plugin.mysql.query(query);
				if (set == null) {
					p.sendMessage("Something went Horribly wrong while processing this!");
					return;
				}
				while(set.next()){
					xp = set.getInt("xp");
					lvl = set.getInt("level");
				}
				int back = xp-amount;
				query = "UPDATE Skillz SET xp=" + back + " WHERE skill = '" + skill + "' AND player = '" + p.getName() + "';";
				plugin.mysql.query(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(plugin.useSQL && plugin.useMySQL){
				plugin.log.info("[Skillz] SQL and MySQL are both enabled! Nothing happened!");
				p.sendMessage("Tell an admin the settings of Skillz are wrong!");
				return;
			}
		}
	}

	public static void levelUp(Player p , String skill, int lvl){
		SkillzLevelEvent event = new SkillzLevelEvent("SkillzLevelEvent", p, skill, lvl);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()){
			return;
		}
		log.info("Money:" + plugin.reward + " items: " + plugin.itemreward);
		if((!plugin.useSQL) && (!plugin.useMySQL)){
			String get = prop.getProperty(skill.toLowerCase());
			String[] array = get.split(";");
			int xp = Integer.parseInt(array[0]);
			String back = Integer.toString(xp) + ";" + Integer.toString(lvl);
			prop.setProperty(skill, back);
			try {
				FileOutputStream out = new FileOutputStream(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt");
				prop.store(out, "Skill=XP;lvl");
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((plugin.useSQL) && (!plugin.useMySQL)) {
			String query = "UPDATE Skillz SET level=" + lvl + " WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
			plugin.dbManager.query(query);
		} else if ((!plugin.useSQL) && (plugin.useMySQL)) {
			String query = "UPDATE Skillz SET level=" + lvl + " WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
			try{
				plugin.mysql.query(query);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		if(plugin.broadcast){
			plugin.getServer().broadcastMessage(ChatColor.RED + p.getDisplayName() + ChatColor.WHITE + " leveled up in " + ChatColor.RED + skill + ChatColor.WHITE +  " and is now level " + ChatColor.RED + lvl +ChatColor.WHITE + "!");
		}else{
			p.sendMessage(getMessage(skill, lvl));
		}
		if(plugin.lightning){
			p.getWorld().strikeLightningEffect(p.getLocation());
		}
		giveReward(p, skill);
		giveItem(p, skill);
		plugin.high.checkScore(p, skill, lvl);
	}

	private static void giveItem(Player p, String skill) {
		Skill s = plugin.skillList.get(skill);
		String item = "0,0";
		if(s == null){
			item = plugin.itemreward;
		}else{
			item = s.getItemReward();
		}
		String[] items = item.split(",");
		int itemnmbr = Integer.parseInt(items[0]);
		if(items[0].equalsIgnoreCase("0") || items[1].equalsIgnoreCase("0")){
			return;
		}
		Material m = Material.getMaterial(itemnmbr);
		int amount = Integer.parseInt(items[1]);
		if(m != null){
			InvManager man = new InvManager(p);
			man.addItem(new ItemStack(m, amount));
			p.sendMessage("You've been rewarded with "+ ChatColor.RED + Integer.toString(amount) + ChatColor.WHITE +" of itemID " + ChatColor.RED +Integer.toString(m.getId()) +ChatColor.WHITE + "!");
		}
		
	}
	
	public static void giveReward(Player p, String skill) {
		int money = 0;
		Skill s = plugin.skillList.get(skill);
		if(s == null){
			money = plugin.reward;
		}else{
			money = s.getMoneyReward();
		}
		if(Skillz.iConomy != null){
			Account acc = com.iConomy.iConomy.getAccount(p.getName());
			if(acc != null){
				Holdings h = acc.getHoldings();
				h.add(money);
				p.sendMessage("You got " + ChatColor.RED + com.iConomy.iConomy.format(money) +ChatColor.WHITE + " for leveling up!");
			}
		}
		if(Skillz.economy != null){
			Skillz.economy.addPlayerMoney(p.getName(), (double)money, false);
			p.sendMessage("You got " + ChatColor.RED + Skillz.economy.getMoneyFormatted(money) +ChatColor.WHITE + " for leveling up!");
		}
		if(Skillz.eco != null){
			Skillz.eco.giveMoney(p, money);
			p.sendMessage("You got "  +ChatColor.RED + Skillz.eco.pluralCurrency + ChatColor.WHITE +" for leveling up!");
		}
		
	}

	public static String getMessage(String skill, int lvl) {
		try{
			String msg = plugin.lvlupmsg;
			msg = msg.replace("SKILL", ChatColor.RED + skill + ChatColor.WHITE);
			msg = msg.replace("LVL", ChatColor.RED + Integer.toString(lvl) + ChatColor.WHITE);
			return msg;
		}catch(Exception e){
			e.printStackTrace();
			return "You leveled up in " + ChatColor.RED + skill + ChatColor.WHITE + "!";
		}
	}

	public static int getLevel(Player p, String skill) {
		int lvl = 0;
		if ((!plugin.useSQL) && (!plugin.useMySQL)) {
			Properties prop = new Properties();
			String file = plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt";
			try
			{
				FileInputStream in = new FileInputStream(file);
				prop.load(in);
				String key = skill.toLowerCase();
				if(!prop.containsKey(key)){
					prop.put(key, "0;0");
					FileOutputStream out = new FileOutputStream(file);
					prop.store(out, "Skill=XP;lvl");
					out.flush();
					out.close();
					return 1;
				}
				String get = prop.getProperty(key);
				in.close();
				String[] dit = get.split(";");
				return Integer.parseInt(dit[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((plugin.useSQL) && (!plugin.useMySQL)) {
			String query = "SELECT * FROM Skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
			ResultSet set = plugin.dbManager.query(query);
			if (set == null) {
				p.sendMessage("Something went Horribly wrong while processing this!");
				return 1;
			}
			try {
				while(set.next()){
					lvl = set.getInt("level");
					return lvl;
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		} else if ((!plugin.useSQL) && (plugin.useMySQL)) {
			String query = "SELECT * FROM skillz WHERE player='" + p.getName() + "' AND skill = '" + skill + "';";
			ResultSet set;
			try {
				set = plugin.mysql.query(query);
				if (set == null) {
					p.sendMessage("Something went Horribly wrong while processing this!");
					return 1;
				}
				if (set.next()) {
					lvl = set.getInt("level");
					return lvl;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}
		return 0;
	}*/
}

class InvManager{
	/*
	Inventory inv;
	Player p;
	Location loc;
	
	public InvManager(Player p){
		this.p = p;
		this.inv = p.getInventory();
		this.loc = p.getLocation();
	}
	
    public ItemStack addItem(ItemStack stack) {
    	int size = inv.getSize();
    	
    	int amount = stack.getAmount();
    	int max = stack.getType().getMaxStackSize();
    	
    	if(max<1) max = 64;
    	
    	for(int i = 0; i<size; i++) {
    		ItemStack slot = inv.getItem(i);
    		
    		int amt = slot.getAmount();
    		
    		if((amt<max)&&(((amt<1)||((stack.getTypeId()==slot.getTypeId())&&(handleData(stack.getData())==handleData(slot.getDurability())))))) {
    			inv.setItem(i, new ItemStack(stack.getType(), (((amount+amt)>max) ? (max) : (amount+amt)), stack.getDurability(), handleData(stack.getData())));
    			
    			amount -= (((max-amt)<0) ? (0) : (max-amt));
    		}
    		
    		if(amount<=0) break;
    	}
    	
    	if((amount>0)&&((p!=null))) for(; amount>0; amount -= max) ((p!=null) ? (p.getLocation()) : (loc)).getWorld().dropItemNaturally(((p!=null) ? (p.getLocation()) : (loc)), new ItemStack(stack.getType(), ((amount>max) ? (max) : (amount)), stack.getDurability(), handleData(stack.getData()))); else return new ItemStack(stack.getType(), amount, stack.getDurability(), handleData(stack.getData()));
    	
    	return new ItemStack(stack.getType(), 0, stack.getDurability(), handleData(stack.getData()));
    }

    public static byte handleData(MaterialData data) {
    	try {
    		if(data!=null) return data.getData(); else return ((byte)(0));
    	} catch(Exception ex) {
    		return ((byte)(0));
    	}
    }
    
    public static byte handleData(short data) {
    	try { 
    		return ((byte)(data));
    	} catch(Exception ex) {
    		return ((byte)(0));
    	}
    }*/
}