package de.another.rpg.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.ui.AnotherUI;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class RPGCommand extends AbstractPlayerCommand {

    public RPGCommand() {
        super("rpg", "Open the Skill Tree", false);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {

            //assert world != null;
            //assert playerRef != null;

           CompletableFuture.runAsync(() -> {
               CustomUIPage page = player.getPageManager().getCustomPage();
               if (page == null) {
                   page = new AnotherUI(playerRef, CustomPageLifetime.CanDismiss);
                   player.getPageManager().openCustomPage(ref, store, page);
               }

               playerRef.sendMessage(Message.raw("UI Page Shown"));
           }, world);
        }
    }
}
