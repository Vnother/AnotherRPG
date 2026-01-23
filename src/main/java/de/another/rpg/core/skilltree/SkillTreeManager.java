package de.another.rpg.core.skilltree;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.skilltree.SkillNode;
import de.another.rpg.api.skilltree.SkillTreeType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SkillTreeManager {

    private final NodeManager nodeManager;

    public SkillTreeManager(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    /**
     * Attempts to unlock a node for a player entity.
     * Checks cost, connectivity, and permissions.
     * @param ref Reference to the player entity.
     * @param store The entity store.
     * @param nodeId The node to unlock.
     * @return true if successful, false otherwise.
     */
    public boolean unlockNode(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, String nodeId) {
        SkillComponent component  = store.getComponent(ref, SkillComponent.getComponentType());
        if (component == null) return false;
        return unlockNode(component, nodeId);
    }

    public boolean unlockNode(SkillComponent component, String nodeId) {
        // 1. Check if valid node
        SkillNode node = nodeManager.getNode(nodeId);
        if (node == null) return false;

        // 2. Check if already unlocked
        if (component.hasNodeUnlocked(nodeId)) return false;

        // 3. Check connectivity
        if (!canUnlock(component, node)) {
            // Send message?
            return false;
        }

        // 4. Check cost
        if (!hasEnoughPoints(component, node)) {
            return false;
        }

        // 5. Apply
        // Deduct points
        spendPoints(component, node);

        // Add node
        component.addUnlockedNode(nodeId);

        // Recalculate stats
        recalculateStats(component);

        return true;
    }

    /**
     * Checks if a player can unlock a specific node (connectivity check).
     * Start nodes are always unlockable.
     */
    public boolean canUnlock(SkillComponent component, SkillNode node) {
        // If it's a start node (no parents? or marked as start?)
        // In our JSON, start nodes have "start" in ID or key, but logic:
        // Ideally start nodes are those at (0,0) or specifically tagged.
        // For now, let's assume if it has no connections OR is close to 0,0?
        // Better: Check if any connected node is already unlocked.

        // Special case: If player has NO nodes of this type unlocked, find "start" nodes?
        // Let's rely on checking if any neighbor is unlocked.

        boolean hasAnyOfTreeType = component.getUnlockedNodes().stream()
                .map(nodeManager::getNode)
                .anyMatch(n -> n != null && n.getTreeType() == node.getTreeType());

        if (!hasAnyOfTreeType) {
            // First node of this tree type being unlocked.
            // Allow if it is a "start" node.
            // Convention: ID ends with "_start" or cost is 0?
            return node.getId().endsWith("_start");
        }

        // Otherwise, must be connected to an unlocked node
        for (String neighborId : node.getConnectedNodes()) {
            if (component.hasNodeUnlocked(neighborId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEnoughPoints(SkillComponent component, SkillNode node) {
        if (node.getTreeType() == SkillTreeType.COMBAT) {
            return component.getCombatPoints() >= node.getCost();
        } else {
            return component.getProfessionPoints() >= node.getCost();
        }
    }

    private void spendPoints(SkillComponent component, SkillNode node) {
        if (node.getTreeType() == SkillTreeType.COMBAT) {
            component.addCombatPoints(-node.getCost());
        } else {
            component.addProfessionPoints(-node.getCost());
        }
    }

    /**
     * Aggregates stats from all unlocked nodes and updates the cache.
     */
    public void recalculateStats(SkillComponent component) {
        Map<String, Double> newStats = new HashMap<>();

        for (String nodeId : component.getUnlockedNodes()) {
            SkillNode node = nodeManager.getNode(nodeId);
            if (node == null) continue;

            for (Map.Entry<String, Double> entry : node.getStats().entrySet()) {
                String statName = entry.getKey();
                // Simple additive stacking for now
                newStats.put(statName, newStats.getOrDefault(statName, 0.0) + entry.getValue());
            }
        }

        component.setCachedStats(newStats);
        // System.out.println("Recalculated stats for user. Active stats: " + newStats.size());
    }
}
