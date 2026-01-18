package de.another.rpg.api.skill;

/**
 * Basis-Interface für alle Skills.
 * Andere Plugins interagieren nur mit diesem Interface (API Layer).
 */
public interface Skill {

    /**
     * Unique Identifier for the skill (e.g., "mining", "swords").
     */
    String getId();

    /**
     * Display name for the user interface.
     */
    String getDisplayName();

    /**
     * Display name for items related to this skill.
     */
    String displayItemName();
}

