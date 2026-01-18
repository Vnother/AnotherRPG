package de.another.rpg.modules.professions.mining;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.logic.BlockBreakEffect;
import de.another.rpg.core.SkillManager;

import java.util.UUID;

public class DoubleDropEffect implements BlockBreakEffect {

    @Override
    public void onBreak(BreakBlockEvent event, Player player, SkillComponent skills, SkillManager skillManager) {
        double chance = skills.getStat("mining_double_drop_chance");

        if (chance > 0 && Math.random() < chance) {
             // For now we just send a message and give extra XP to simulate "value"
             player.sendMessage(Message.raw("§aDouble Drop triggered! (Skill Effect)"));

             // Simulate "Double Drop" by giving double XP benefit or separate drop logic
             // In a real scenario, we would modify `event.getDrops()` if mutable, or spawn an entity item.
             @SuppressWarnings("removal") UUID playerId = player.getUuid();
             skillManager.awardXpForBlock(playerId, event.getBlockType().getId());
        }
    }
}
