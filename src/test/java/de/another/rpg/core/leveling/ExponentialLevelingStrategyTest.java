package de.another.rpg.core.leveling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExponentialLevelingStrategyTest {

    @Test
    public void testLevel1Xp() {
        ExponentialLevelingStrategy strategy = new ExponentialLevelingStrategy(99);
        long xp = strategy.getXpRequiredForLevel(1);
        // Usually level 1 requires 0 XP or a base amount.
        // The implementation calculates partial sums.
        // Let's print it to see the curve.
        System.out.println("Level 1 XP: " + xp);
    }

    @Test
    public void testCurveProgression() {
        ExponentialLevelingStrategy strategy = new ExponentialLevelingStrategy(99);

        long prevXp = 0;
        for (int i = 1; i <= 10; i++) {
            long xp = strategy.getXpRequiredForLevel(i);
            System.out.println("Level " + i + ": " + xp + " XP");
            Assertions.assertTrue(xp >= prevXp, "XP should increase with level");
            prevXp = xp;
        }
    }

    @Test
    public void testLevelFromXp() {
        ExponentialLevelingStrategy strategy = new ExponentialLevelingStrategy(99);
        long xpForLevel5 = strategy.getXpRequiredForLevel(5);
        int level = strategy.getLevelFromXp(xpForLevel5);
        Assertions.assertEquals(5, level, "Should be level 5 if we have exact XP for level 5 start");
        // Logic: if totalXp < table[i+1], return i. table[i] is min XP for level i.
        // So if we have exact XP for level 5, it should be level 5.
        // Let's check the implementation logic in next step if this fails.
    }
}

