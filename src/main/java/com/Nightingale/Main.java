package com.Nightingale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    static HytaleLogger logger = HytaleLogger.getLogger();

    public Main(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup0() {
        logger.atInfo().log("Setting up plugin " + getName());
        super.setup0();
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
