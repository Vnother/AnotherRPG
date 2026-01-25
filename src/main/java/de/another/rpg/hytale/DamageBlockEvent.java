package de.another.rpg.hytale;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.AnotherRPG;
import de.another.rpg.core.SkillManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

// Note: System class name conflicts with Event class name, using full qualification for Event
public class DamageBlockEvent extends EntityEventSystem<EntityStore, com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent> {

    private static final ConcurrentHashMap<Class<?>, DamageAccessors> ACCESSORS_CACHE = new ConcurrentHashMap<>();
    private final SkillManager skillManager;

    public DamageBlockEvent() {
        super(com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent.class);
        this.skillManager = AnotherRPG.getInstance().getSkillManager();
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent damageBlockEvent) {
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(entityRef, Player.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());

        if (player != null && uuidComponent != null) {

            BlockType blockType = damageBlockEvent.getBlockType();
            if (blockType != null) {
               String blockTypeId = blockType.getId();
               String lowerId = blockTypeId.toLowerCase();
               boolean isMiningBlock = lowerId.contains("ore") || lowerId.contains("stone") || lowerId.contains("rock") || lowerId.contains("mineral");
               boolean isWoodcuttingBlock = lowerId.contains("wood") || lowerId.contains("log") || lowerId.contains("plank") || lowerId.contains("tree");

               // Prevent overlap double counting if any
               if (isMiningBlock && isWoodcuttingBlock) {
                  isWoodcuttingBlock = false;
               }

               if (isMiningBlock || isWoodcuttingBlock) {
                  // Use "prosperity" skill for both as configured in system
                  int statLevel = skillManager.getLevel(uuidComponent.getUuid(), "prosperity");

                  if (statLevel > 0) {
                     // Multiplier: 1% per level
                     double damageMultiplier = 1.0 + (statLevel * 0.01);

                     DamageAccessors acc = ACCESSORS_CACHE.computeIfAbsent(damageBlockEvent.getClass(), DamageBlockEvent::resolveAccessors);

                     float currentAmount = 0.0F;
                     boolean hasAmount = false;
                     if (acc.getAmount != null) {
                        try {
                           Object result = acc.getAmount.invoke(damageBlockEvent);
                           if (result instanceof Number) {
                              currentAmount = ((Number)result).floatValue();
                              hasAmount = true;
                           }
                        } catch (ReflectiveOperationException e) {
                           ACCESSORS_CACHE.remove(damageBlockEvent.getClass());
                           hasAmount = false;
                        }
                     }

                     if (hasAmount && acc.setAmount != null) {
                        float newAmount = (float)((double)currentAmount * damageMultiplier);

                        try {
                           acc.setAmount.invoke(damageBlockEvent, newAmount);
                           // Optional debug
                           System.out.println("Buffed Block Damage! Lvl: " + statLevel + " Old: " + currentAmount + " New: " + newAmount);
                        } catch (ReflectiveOperationException e) {
                           ACCESSORS_CACHE.remove(damageBlockEvent.getClass());
                        }
                     }
                  }
               }
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @NonNullDecl
    private static DamageAccessors resolveAccessors(@NonNullDecl Class<?> eventClass) {
      try {
         Method get = eventClass.getMethod("getAmount");
         Method set = eventClass.getMethod("setAmount", Float.TYPE);
         return new DamageAccessors(get, set, "setAmount");
      } catch (NoSuchMethodException var13) {
         String[] getNames = new String[]{"getDamage", "getDamageAmount", "getBlockDamage"};
         String[] setNames = new String[]{"setDamage", "setDamageAmount", "setBlockDamage"};
         Method get = null;

         for (String name : getNames) {
            try {
               get = eventClass.getMethod(name);
               break;
            } catch (NoSuchMethodException ignored) {
            }
         }

         Method set = null;
         String via = "unknown";

         for (String name : setNames) {
            try {
               set = eventClass.getMethod(name, Float.TYPE);
               via = name;
               break;
            } catch (NoSuchMethodException ignored) {
            }
         }

         return new DamageAccessors(get, set, via);
      }
   }

   private static final class DamageAccessors {
      @NullableDecl
      final Method getAmount;
      @NullableDecl
      final Method setAmount;
      @NonNullDecl
      final String via;

      DamageAccessors(@NullableDecl Method getAmount, @NullableDecl Method setAmount, @NonNullDecl String via) {
         this.getAmount = getAmount;
         this.setAmount = setAmount;
         this.via = via;
      }
   }

}
