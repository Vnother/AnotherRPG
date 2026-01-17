package de.another.rpg.api.event;

import java.util.UUID;

/**
 * Event triggered when a player levels up a skill.
 */
public class SkillLevelUpEvent {

    private final UUID playerId;
    private final String skillId;
    private final int newLevel;
    private final int oldLevel;

    public SkillLevelUpEvent(UUID playerId, String skillId, int newLevel, int oldLevel) {
        this.playerId = playerId;
        this.skillId = skillId;
        this.newLevel = newLevel;
        this.oldLevel = oldLevel;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getSkillId() {
        return skillId;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getOldLevel() {
        return oldLevel;
    }
}

