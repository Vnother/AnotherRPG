package de.another.rpg.api.event;

/**
 * Listener interface for skill events.
 */
public interface SkillEventListener {
    void onLevelUp(SkillLevelUpEvent event);
}

