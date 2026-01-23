package de.another.rpg.api.logic;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.core.SkillManager;

/**
 * Interface for logic that executes when a block is broken,
 * influenced by skills or stats.
 */
public interface UseBlockEffect {

    /**
     * Executes the skill logic.
     * @param event The Hytale use block event.
     * @param player The player component invoking the event.
     * @param skills The skill component of the player (for stats/nodes).
     * @param skillManager The skill manager (for awarding XP etc).
     */
    void onUse(UseBlockEvent event, Player player, SkillComponent skills, SkillManager skillManager);
}
