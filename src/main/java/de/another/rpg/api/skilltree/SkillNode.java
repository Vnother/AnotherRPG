package de.another.rpg.api.skilltree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a single node in the passive skill tree.
 * Can be serialized/deserialized from JSON.
 */
public class SkillNode {

    private String id;
    private SkillTreeType treeType;
    private NodeType type;
    private int x;
    private int y;
    private int cost;
    private List<String> connectedNodes;
    private Map<String, Double> stats;
    private String permission;

    /**
     * No-args constructor for JSON deserialization frameworks (like Gson/Jackson)
     */
    public SkillNode() {
        this.connectedNodes = new ArrayList<>();
        this.stats = new HashMap<>();
    }

    public SkillNode(String id, SkillTreeType treeType, NodeType type, int x, int y, int cost) {
        this.id = id;
        this.treeType = treeType;
        this.type = type;
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.connectedNodes = new ArrayList<>();
        this.stats = new HashMap<>();
    }

    // Getters

    public String getId() {
        return id;
    }

    public SkillTreeType getTreeType() {
        return treeType;
    }

    public NodeType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getConnectedNodes() {
        return connectedNodes;
    }

    public Map<String, Double> getStats() {
        return stats;
    }

    /**
     * Returns the permission required to unlock this node, if any.
     * @return Optional containing the permission string, or empty.
     */
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    // Setters (useful for Builders or deserialization)

    public void setId(String id) {
        this.id = id;
    }

    public void setTreeType(SkillTreeType treeType) {
        this.treeType = treeType;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setConnectedNodes(List<String> connectedNodes) {
        this.connectedNodes = connectedNodes;
    }

    public void setStats(Map<String, Double> stats) {
        this.stats = stats;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    // Utility methods

    public void addConnection(String nodeId) {
        if (!this.connectedNodes.contains(nodeId)) {
            this.connectedNodes.add(nodeId);
        }
    }

    public void addStat(String statName, double value) {
        this.stats.put(statName, value);
    }
}
