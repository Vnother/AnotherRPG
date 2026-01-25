package de.another.rpg.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.AnotherRPG;
import de.another.rpg.api.skill.Skill;
import de.another.rpg.core.SkillManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

public class XPCommand extends AbstractPlayerCommand {
    public XPCommand() {
        super("xp", "Check your current XP", false);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            // Retrieve and display player's XP from SkillManager
            UUID playerId = playerRef.getUuid();
            SkillManager skillManager = AnotherRPG.getInstance().getSkillManager();
            var registry = AnotherRPG.getInstance().getSkillRegistry();

            player.sendMessage(Message.raw("--- Skills & XP ---"));
            for (Skill skill : registry.getAllSkills()) {
                int level = skillManager.getLevel(playerId, skill.getId());
                double xp = skillManager.getXp(playerId, skill.getId());

                player.sendMessage(Message.raw(String.format("%s: Level %d (%.1f XP)", skill.getDisplayName(), level, xp)));
            }
            player.sendMessage(Message.raw("-------------------"));
        }

    }
}
