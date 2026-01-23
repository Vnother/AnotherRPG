package de.another.rpg.core.logic;

import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import de.another.rpg.api.logic.BlockBreakEffect;
import de.another.rpg.api.logic.UseBlockEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for skill effects attached to events.
 */
public class EffectManager {

    private final List<BlockBreakEffect> blockBreakEffects = new ArrayList<>();

    private final List<UseBlockEffect> useBlockEffects = new ArrayList<>();

    public void registerBlockBreakEffect(BlockBreakEffect effect) {
        blockBreakEffects.add(effect);
    }

    public void registerUseBlockEffect(UseBlockEffect effect) {
        useBlockEffects.add(effect);
    }

    public List<BlockBreakEffect> getBlockBreakEffects() {
        return blockBreakEffects;
    }

    public List<UseBlockEffect> getUseBlockEffects() {
        return useBlockEffects;
    }


}
