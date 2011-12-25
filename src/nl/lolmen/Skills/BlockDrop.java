package nl.lolmen.Skills;

import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BlockDrop {
	private static Random random = new Random();

	public static ItemStack getDrop(Block block) throws RuntimeException {
		if (block == null)
			return null;
		int blockTypeId = block.getTypeId();
		if (blockTypeId < 1 || blockTypeId > 255)
			return null;
		try {
			net.minecraft.server.Block b = net.minecraft.server.Block.byId[blockTypeId];

			int typeId = b.getDropType(blockTypeId, random, 0);
			if (typeId < 1)
				return null;

			int dropCount = b.getDropCount(0, random);
			if (dropCount < 1)
				return null;

			Method m = getMethod(b.getClass(), "getDropData",
					new Class[] { int.class });
			m.setAccessible(true);
			byte dropData = ((Integer) m.invoke(b, block.getData()))
					.byteValue();

			return new ItemStack(typeId, dropCount, dropData);
		} catch (Exception e) {
			throw new RuntimeException(
					"A severe error occured while retreiving the data dropped.",
					e);
		}
	}

	
	private static Method getMethod(Class<?> clazz, String methodName,
			Class<?>[] parameters) throws NoSuchMethodException,
			NullPointerException {
		if (methodName == null)
			throw new NullPointerException();
		if (clazz == null)
			throw new NoSuchMethodException();
		try {
			return clazz.getDeclaredMethod(methodName, parameters);
		} catch (Exception e) {
			return getMethod(clazz.getSuperclass(), methodName, parameters);
		}
	}
}