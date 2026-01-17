package de.another.rpg.core.leveling;

import de.another.rpg.api.leveling.LevelingStrategy;

/**
 * Implementation of the curve similar to Old School RuneScape.
 */
public class ExponentialLevelingStrategy implements LevelingStrategy {

    private final int maxLevel;
    private final long[] xpTable;

    public ExponentialLevelingStrategy(int maxLevel) {
        this.maxLevel = maxLevel;
        this.xpTable = new long[maxLevel + 2];
        calculateTable();
    }

    private void calculateTable() {
        long total = 0;
        for (int i = 1; i <= maxLevel; i++) {
            xpTable[i] = total;
            double diff = Math.floor(i + 300 * Math.pow(2, i / 7.0));
            total += (long) (diff / 4);
        }
        // Store for level + 1 as well to avoid index out of bounds if checking next level of max
        xpTable[maxLevel + 1] = total;
    }

    @Override
    public long getXpRequiredForLevel(int level) {
        if (level > maxLevel) return xpTable[maxLevel]; // Or throw
        if (level < 1) return 0;
        return xpTable[level];
    }

    @Override
    public int getLevelFromXp(long totalXp) {
        for (int i = 1; i <= maxLevel; i++) {
            if (totalXp < xpTable[i + 1]) {
                return i;
            }
        }
        return maxLevel;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}


