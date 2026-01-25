package de.another.rpg.modules.professions.mining;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.logic.BlockBreakEffect;
import de.another.rpg.core.SkillManager;

import java.util.UUID;

public class ShatterStrikeEffect implements BlockBreakEffect {

    @Override
    public void onBreak(BreakBlockEvent event, Player player, UUID playerId, SkillComponent skills, SkillManager skillManager) {
        if (skills.hasNodeUnlocked("keystone_shatter_strike")) {
            // Check if stone? (Simplified check)
            if (event.getBlockType().getId().contains("stone")) {
                 player.sendMessage(Message.raw("§c[Shatter Strike] Stone pulverized!"));
                 // Logic to instantly break or modify drops would go here
            }
        }
    }
}
