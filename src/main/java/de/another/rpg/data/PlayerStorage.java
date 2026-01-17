package de.another.rpg.data;

import de.another.rpg.api.component.SkillComponent;
import java.util.UUID;

public interface PlayerStorage {

    /**
     * Loads the player's skill data.
     * @param playerId The player's UUID.
     * @return SkillComponent containing XP data.
     */
    SkillComponent loadPlayer(UUID playerId);

    /**
     * Saves the player's skill data.
     * @param playerId The player's UUID.
     * @param skillData The SkillComponent to save.
     */
    void savePlayer(UUID playerId, SkillComponent skillData);
}

