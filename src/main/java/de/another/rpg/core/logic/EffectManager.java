package de.another.rpg.core.logic;

import de.another.rpg.api.logic.BlockBreakEffect;
import java.util.ArrayList;
import java.util.List;

/**
 * Registry for skill effects attached to events.
 */
public class EffectManager {

    private final List<BlockBreakEffect> blockBreakEffects = new ArrayList<>();

    public void registerBlockBreakEffect(BlockBreakEffect effect) {
        blockBreakEffects.add(effect);
    }

    public List<BlockBreakEffect> getBlockBreakEffects() {
        return blockBreakEffects;
    }
}
