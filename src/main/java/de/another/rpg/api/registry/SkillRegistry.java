package de.another.rpg.api.registry;

import de.another.rpg.api.skill.Skill;
import java.util.Collection;
import java.util.Optional;

/**
 * Registry interface for managing available skills.
 * Part of the API package.
 */
public interface SkillRegistry {

    /**
     * Registers a new skill.
     * @param skill The skill implementation to register.
     */
    void register(Skill skill);

    /**
     * Retrieves a skill by its ID.
     * @param id The unique skill ID.
     * @return Optional containing the skill if found.
     */
    Optional<Skill> getSkill(String id);

    /**
     * returns all registered skills.
     * @return Collection of skills.
     */
    Collection<Skill> getAllSkills();
}

