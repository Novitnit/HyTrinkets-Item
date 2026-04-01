package com.Nightingale.HyTrinketsItem;

import com.Nightingale.HyTrinketsItem.events.PreventDeathSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;
import com.Nightingale.HyTrinket.Api.TrinketRegistry;

public class Main extends JavaPlugin {
    static HytaleLogger logger = HytaleLogger.getLogger();

    public Main(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        logger.atInfo().log("Setting up plugin " + getName());
        TrinketRegistry.RegisterItem("Necklace","ResurrectionCollar");
        super.setup();
    }

    @Override
    protected void start() {
        super.start();
        this.getEntityStoreRegistry().registerSystem(new PreventDeathSystem());
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
