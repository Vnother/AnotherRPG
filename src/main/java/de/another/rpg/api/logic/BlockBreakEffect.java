package de.another.rpg.api.logic;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.core.SkillManager;

/**
 * Interface for logic that executes when a block is broken,
 * influenced by skills or stats.
 */
public interface BlockBreakEffect {

    /**
     * Executes the skill logic.
     * @param event The Hytale block break event.
     * @param player The player component invoking the event.
     * @param skills The skill component of the player (for stats/nodes).
     * @param skillManager The skill manager (for awarding XP etc).
     */
    void onBreak(BreakBlockEvent event, Player player, SkillComponent skills, SkillManager skillManager);
}
