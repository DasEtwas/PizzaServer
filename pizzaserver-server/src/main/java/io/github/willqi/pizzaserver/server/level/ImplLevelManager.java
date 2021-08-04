package io.github.willqi.pizzaserver.server.level;

import io.github.willqi.pizzaserver.api.level.Level;
import io.github.willqi.pizzaserver.api.level.LevelManager;
import io.github.willqi.pizzaserver.api.level.world.World;
import io.github.willqi.pizzaserver.commons.utils.ReadWriteKeyLock;
import io.github.willqi.pizzaserver.commons.world.Dimension;
import io.github.willqi.pizzaserver.server.ImplServer;
import io.github.willqi.pizzaserver.server.level.providers.BaseLevelProvider;
import io.github.willqi.pizzaserver.server.level.providers.ProviderType;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImplLevelManager implements LevelManager, Closeable {

    private final ImplServer server;

    // fileName : Level
    private final Map<String, ImplLevel> levels = new ConcurrentHashMap<>();
    private final ReadWriteKeyLock<String> locks = new ReadWriteKeyLock<>();


    public ImplLevelManager(ImplServer server) {
        this.server = server;
    }

    public ImplServer getServer() {
        return this.server;
    }

    /**
     * Ticks all the levels loaded
     */
    public void tick() {
        for (ImplLevel level : this.levels.values()) {
            level.tick();
        }
    }

    @Override
    public boolean isLevelLoaded(String name) {
        return this.levels.containsKey(name);
    }

    @Override
    public Level getLevel(String name) {
        this.locks.readLock(name);
        this.levels.computeIfAbsent(name, ignored -> {
            this.locks.readUnlock(name);
            this.locks.writeLock(name); // Prevent multiple Threads from reading the directory at the same time

            ImplLevel level = null;
            try {
                if (!this.levels.containsKey(name)) {
                    level = this.fetchLevel(name);
                }
            } finally {
                this.locks.writeUnlock(name);
                this.locks.readLock(name);
            }
            return level;
        });
        try {
            return this.levels.getOrDefault(name, null);
        } finally {
            this.locks.readUnlock(name);
        }
    }

    @Override
    public World getLevelDimension(String levelName, Dimension dimension) {
        Level level = this.getLevel(levelName);
        if (level == null) {
            return null;
        }
        return level.getDimension(dimension);
    }

    private ImplLevel fetchLevel(String name) {
        File file = Paths.get(this.server.getRootDirectory(), "levels", name).toFile();
        if (!file.exists()) {
            return null;
        }
        BaseLevelProvider provider;
        try {
            provider = ProviderType.resolveByFile(file).create(file);
        } catch (IOException exception) {
            this.server.getLogger().error("Failed to create world provider with level: " + name, exception);
            return null;
        }
        return new ImplLevel(this.server, provider);
    }

    @Override
    public boolean unloadLevel(String name) {
        this.locks.writeLock(name);

        try {
            ImplLevel level = this.levels.getOrDefault(name, null);
            if (level == null) {
                return false;
            }
            try {
                level.save();
                level.close();
            } catch (IOException exception) {
                this.server.getLogger().error("Failed to unload level: " + name, exception);
                return false;
            }

            this.levels.remove(name);
            return true;
        } finally {
            this.locks.writeUnlock(name);
        }
    }

    @Override
    public void close() throws IOException {
        for (ImplLevel level : this.levels.values()) {
            level.close();
        }
    }
}
