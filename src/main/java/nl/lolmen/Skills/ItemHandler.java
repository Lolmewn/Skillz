package nl.lolmen.Skills;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemHandler {

	public static void addItems(Player player, ItemStack item) {
		player.getInventory().addItem(item);
		//player.updateInventory();
	}

	public static void addItems(Player player, String item, int amount) {
		Material m = Material.getMaterial(item);
		if (m != null) {
			ItemStack is = new ItemStack(m, amount);
			player.getInventory().addItem(is);
			//player.updateInventory();
		}
	}

	public static void addItems(Player player, Material item, int amount) {
		ItemStack is = new ItemStack(item, amount);
		player.getInventory().addItem(is);
		//player.updateInventory();
	}

	public static void addItems(Chest chest, ItemStack item) {
		chest.getInventory().addItem(item);
		chest.update(true);
	}

	public static void addItems(Chest chest, String item, int amount) {
		Material m = Material.getMaterial(item);
		if (m != null) {
			ItemStack is = new ItemStack(m, amount);
			chest.getInventory().addItem(is);
			chest.update(true);
		}
	}

	public static void addItems(Chest chest, Material item, int amount) {
		ItemStack is = new ItemStack(item, amount);
		chest.getInventory().addItem(is);
		chest.update(true);
	}

	public static void removeItems(Player player, ItemStack item) {
		player.getInventory().remove(item);
		//player.updateInventory();
	}

	public static void removeItems(Player player, String item, int amount) {
		Material m = Material.getMaterial(item);
		if (m != null) {
			ItemStack is = new ItemStack(m, amount);
			player.getInventory().remove(is);
			//player.updateInventory();
		}
	}

	public static void removeItems(Player player, Material item, int amount) {
		ItemStack is = new ItemStack(item, amount);
		player.getInventory().remove(is);
		//player.updateInventory();
	}

	public static void removeItems(Chest chest, ItemStack item) {
		chest.getInventory().remove(item);
		chest.update(true);
	}

	public static void removeItems(Chest chest, String item, int amount) {
		Material m = Material.getMaterial(item);
		if (m != null) {
			ItemStack is = new ItemStack(m, amount);
			chest.getInventory().remove(is);
			chest.update(true);
		}
	}

	public static void removeItems(Chest chest, Material item, int amount) {
		ItemStack is = new ItemStack(item, amount);
		chest.getInventory().remove(is);
		chest.update(true);
	}

	public static boolean hasSpace(Player player, ItemStack item) {
		for (ItemStack is : player.getInventory().getContents()) {
			if (is == null) {
				return true;
			}
			if (is == item) {
				if (is.getMaxStackSize() - item.getAmount() > 0) {
					return true;
				}
			}

		}
		return false;
	}

	public static boolean hasSpace(Player player, int amount) {
		int i = 0;
		for (ItemStack is : player.getInventory().getContents()) {
			if (is == null) {
				i++;
			}
			if (i == amount) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSpace(Chest chest, ItemStack item) {
		for (ItemStack is : chest.getInventory().getContents()) {
			if (is == null) {
				return true;
			}
			if (is == item) {
				if (is.getMaxStackSize() - item.getAmount() > 0) {
					return true;
				}
			}

		}
		return false;
	}

	public static boolean hasSpace(Chest chest, int amount) {
		int i = 0;
		for (ItemStack is : chest.getInventory().getContents()) {
			if (is == null) {
				i++;
			}
			if (i == amount) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasItem(Chest chest, ItemStack item) {
		Inventory inv = chest.getInventory();
		return inv.contains(item);
	}

	public static boolean hasItem(Chest chest, Material item, int amount) {
		Inventory inv = chest.getInventory();
		return inv.contains(item, amount);
	}

	public static boolean hasItem(Chest chest, String item, int amount) {
		Inventory inv = chest.getInventory();
		Material mat = Material.getMaterial(item);
		if (mat == null)
			return false;

		return inv.contains(mat, amount);
	}

	public static boolean hasItem(Player player, ItemStack item) {
		Inventory inv = player.getInventory();
		return inv.contains(item);
	}

	public static boolean hasItem(Player player, String item, int amount) {
		Inventory inv = player.getInventory();
		return inv.contains(Material.getMaterial(item), amount);
	}

	public static boolean hasItem(Player player, Material item, int amount) {
		Inventory inv = player.getInventory();
		return inv.contains(item, amount);
	}

}
