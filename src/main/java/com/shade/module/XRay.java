// language: Java, file: src/main/java/com/shade/module/XRay.java
package com.shade.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class XRay extends Module {
    public XRay() { super("XRay"); }

    public static boolean shouldRender(Block block) {
        Module m = ModuleManager.INSTANCE.byName("XRay");
        if (m == null || !m.isEnabled()) return true;
        return block == Blocks.DIAMOND_ORE
            || block == Blocks.GOLD_ORE
            || block == Blocks.IRON_ORE
            || block == Blocks.COAL_ORE
            || block == Blocks.REDSTONE_ORE
            || block == Blocks.LAPIS_ORE
            || block == Blocks.EMERALD_ORE
            || block == Blocks.NETHER_GOLD_ORE
            || block == Blocks.ANCIENT_DEBRIS
            || block == Blocks.CHEST
            || block == Blocks.ENDER_CHEST
            || block == Blocks.SPAWNER;
    }
}
