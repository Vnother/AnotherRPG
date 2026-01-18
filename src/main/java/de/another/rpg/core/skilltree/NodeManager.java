package de.another.rpg.core.skilltree;

import de.another.rpg.api.skilltree.SkillNode;
import de.another.rpg.api.skilltree.SkillTreeType;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Manages the registration and retrieval of SkillNodes.
 * separating the graph into COMBAT and LIFESTYLE sections.
 */
public class NodeManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // Registry: ID -> SkillNode
    private final Map<String, SkillNode> nodeRegistry = new HashMap<>();

    /**
     * Loads all skill nodes from JSON files in the specified directory.
     * @param directory The directory containing .json files with lists of SkillNodes.
     */
    public void loadNodesFromDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<SkillNode>>(){}.getType();
                List<SkillNode> nodes = gson.fromJson(reader, listType);

                if (nodes != null) {
                    for (SkillNode node : nodes) {
                        registerNode(node);
                    }
                    System.out.println("Loaded " + nodes.size() + " nodes from " + file.getName());
                }
            } catch (IOException e) {
                System.err.println("Failed to load skill nodes from " + file.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers a new node into the system.
     * @param node The SkillNode to register.
     */
    public void registerNode(SkillNode node) {
        if (nodeRegistry.containsKey(node.getId())) {
            // Log warning or overwrite? Let's overwrite for hot-reloading support
            System.out.println("Warning: Overwriting existing node with ID " + node.getId());
        }
        nodeRegistry.put(node.getId(), node);
    }

    /**
     * Retrieves a specific node by its ID.
     * @param id The node ID.
     * @return The SkillNode, or null if not found.
     */
    public SkillNode getNode(String id) {
        return nodeRegistry.get(id);
    }

    /**
     * Filters all registered nodes by their SkillTreeType (COMBAT/LIFESTYLE).
     * @param type The type of tree to filter for.
     * @return List of matching SkillNodes.
     */
    public List<SkillNode> getNodesByType(SkillTreeType type) {
        return nodeRegistry.values().stream()
                .filter(node -> node.getTreeType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Checks if two nodes are directly connected.
     * Note: This check is directional based on how connections are stored in nodes.
     * Usually you'd want bidirectional checks or ensure data consistency.
     * Here we check if either node lists the other as connected.
     *
     * @param nodeId1 ID of the first node.
     * @param nodeId2 ID of the second node.
     * @return True if they are connected.
     */
    public boolean areNodesConnected(String nodeId1, String nodeId2) {
        SkillNode node1 = nodeRegistry.get(nodeId1);
        SkillNode node2 = nodeRegistry.get(nodeId2);

        if (node1 == null || node2 == null) {
            return false;
        }

        return node1.getConnectedNodes().contains(nodeId2) ||
               node2.getConnectedNodes().contains(nodeId1);
    }

    /**
     * Returns all registered nodes.
     * @return Map of ID -> SkillNode
     */
    public Map<String, SkillNode> getAllNodes() {
        return new HashMap<>(nodeRegistry);
    }
}
