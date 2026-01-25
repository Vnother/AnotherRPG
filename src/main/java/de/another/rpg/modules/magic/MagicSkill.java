package de.another.rpg.modules.magic;

import de.another.rpg.api.skill.Skill;

public class MagicSkill implements Skill {
    @Override
    public String getId() {
        return "magic";
    }

    @Override
    public String getDisplayName() {
        return "Magic";
    }

    @Override
    public String displayItemName() {
        return "Weapon_Sword_Runic";
    }
}
