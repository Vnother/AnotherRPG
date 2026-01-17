package de.another.rpg.core.registry;

import de.another.rpg.api.registry.SkillRegistry;
import de.another.rpg.api.skill.Skill;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of the SkillRegistry.
 * Resides in the Core module (Pure Java).
 */
public class SkillRegistryImpl implements SkillRegistry {

    private final Map<String, Skill> skills = new ConcurrentHashMap<>();

    @Override
    public void register(Skill skill) {
        if (skills.containsKey(skill.getId())) {
            throw new IllegalArgumentException("Skill with ID " + skill.getId() + " is already registered.");
        }
        skills.put(skill.getId(), skill);
    }

    @Override
    public Optional<Skill> getSkill(String id) {
        return Optional.ofNullable(skills.get(id));
    }

    @Override
    public Collection<Skill> getAllSkills() {
        return skills.values();
    }
}

