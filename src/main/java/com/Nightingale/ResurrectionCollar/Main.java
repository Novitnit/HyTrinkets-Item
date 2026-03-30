package com.Nightingale.ResurrectionCollar;

import com.Nightingale.ResurrectionCollar.events.PreventDeathSystem;
import com.hypixel.hytale.logger.HytaleLogger;
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
        super.setup();

        TrinketRegistry.RegisterItem("Necklace","ResurrectionCollar");
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
