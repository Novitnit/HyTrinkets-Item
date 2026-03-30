package com.Nightingale.HyTrinketsItem.events;

import com.Nightingale.HyTrinket.components.TrinketComponent;
import com.Nightingale.HyTrinket.components.TrinketSlot;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.world.SpawnParticleSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class PreventDeathSystem extends DamageEventSystem {
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Nullable
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public void handle(int index, @NotNull ArchetypeChunk<EntityStore> archetypeChunk, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer, @NotNull Damage damage) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;
        EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        if (statMap == null) return;
        EntityStatValue healthValue = statMap.get(DefaultEntityStatTypes.getHealth());
        if (healthValue == null) return;
        if (healthValue.get() > damage.getAmount()) return;

        TrinketComponent trinketComponent = store.getComponent(ref, TrinketComponent.getTrinketsComponentType());
        if (trinketComponent == null) return;
        TrinketSlot NecklaceSlot = trinketComponent.getSlot("Necklace");
        if (NecklaceSlot == null) return;
        boolean hasResurrectionCollar = NecklaceSlot.haveItem("ResurrectionCollar");
        player.sendMessage(Message.raw(Arrays.toString(NecklaceSlot.getItems())));
        if (hasResurrectionCollar) {
            NecklaceSlot.removeItem("ResurrectionCollar", 1);
            damage.setCancelled(true);
            heal(store, ref);
            playResurrectionCollarSound(player,store,ref);
            playParticles(store, ref);
        }
    }

    private void playResurrectionCollarSound(Player player, Store<EntityStore> store, Ref<EntityStore> ref) {
        assert player.getWorld() != null;
        player.getWorld().execute(() -> {
            Vector3d position = Objects.requireNonNull(store.getComponent(ref, EntityModule.get().getTransformComponentType())).getPosition();
            SoundUtil.playSoundEvent3d(
                    SoundEvent.getAssetMap().getIndex("Resurrection_Collar_Active"),
                    SoundCategory.SFX,
                    position.x, position.y, position.z, 1f, 1f,
                    store
            );
        });
    }

    public void heal(Store<EntityStore> store, Ref<EntityStore> ref) {
        EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert statMap != null;
        assert effectController != null;
        int healthType = DefaultEntityStatTypes.getHealth();
        int staminaType = DefaultEntityStatTypes.getStamina();

        EntityStatValue health = statMap.get(healthType);
        EntityStatValue stamina = statMap.get(staminaType);
        assert health != null;
        assert stamina != null;
        float healthToRestore = 1f;
        float staminaToRestore = 0f;

        statMap.setStatValue(healthType, healthToRestore);
        statMap.setStatValue(staminaType, staminaToRestore);

        effectController.clearEffects(ref, store);
        effectController.addEffect(
                ref,
                Objects.requireNonNull(EntityEffect.getAssetMap().getAsset("Food_Health_Regen_Large")),
                15f,
                OverlapBehavior.OVERWRITE,
                store
        );
    }

    private void playParticles(Store<EntityStore> store, Ref<EntityStore> ref) {
        Vector3d position = Objects.requireNonNull(store.getComponent(ref, EntityModule.get().getTransformComponentType())).getPosition();

        SpawnParticleSystem packet = new SpawnParticleSystem(
                "Explosion_Big",
                new Position(position.x, position.y, position.z),
                new Direction(0f, 0f, 0f),
                1.5f,
                new Color((byte)-1, (byte)-1, (byte)-1)
        );
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        assert playerRef != null;
        playerRef.getPacketHandler().writeNoCache(packet);
    }

}
