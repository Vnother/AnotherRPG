package de.another.rpg.api.leveling;

/**
 * Strategy interface for calculating XP requirements and levels.
 * Entkoppelt die Mathematik von der Skill-Logik (Strategy Pattern).
 */
public interface LevelingStrategy {

    /**
     * Calculates the total XP required to reach a specific level.
     * @param level The target level.
     * @return The total XP required.
     */
    long getXpRequiredForLevel(int level);

    /**
     * Calculates the level reached with a specific amount of XP.
     * @param totalXp The total XP accumulated.
     * @return The current level.
     */
    int getLevelFromXp(long totalXp);

    /**
     * Gets the max level configured for this strategy.
     * @return Max Level.
     */
    int getMaxLevel();
}

