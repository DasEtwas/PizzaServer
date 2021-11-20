package io.github.pizzaserver.api;

import io.github.pizzaserver.api.level.LevelManager;
import io.github.pizzaserver.api.event.EventManager;
import io.github.pizzaserver.api.packs.ResourcePackManager;
import io.github.pizzaserver.api.player.Player;
import io.github.pizzaserver.api.plugin.PluginManager;
import io.github.pizzaserver.api.scheduler.Scheduler;
import io.github.pizzaserver.api.utils.Logger;

import java.util.Set;

/**
 * Represents a Minecraft Server.
 */
public interface Server {

    /**
     * Return all {@link Player}s who been spawned into the server.
     * @return a set of all players online
     */
    Set<Player> getPlayers();

    /**
     * Retrieve the amount of players currently online.
     * @return online player count
     */
    int getPlayerCount();

    /**
     * Retrieve the motto of the day message displayed in the server list menu.
     * @return motto of the day
     */
    String getMotd();

    /**
     * Change the motto of the day message displayed in the server list menu.
     */
    void setMotd(String motd);

    /**
     * Get the maximum amount of {@link Player}s allowed on the server.
     * @return max player count
     */
    int getMaximumPlayerCount();

    /**
     * Set the maximum allowed {@link Player}s allowed on the server.
     * @param players max player count
     */
    void setMaximumPlayerCount(int players);

    /**
     * Retrieve the target ticks per second for the server.
     * @return target tps
     */
    int getTargetTps();

    /**
     * Change the target ticks per second for the server.
     * @param newTps new ticks per second
     */
    void setTargetTps(int newTps);

    /**
     * Retrieve the last recorded ticks per second.
     * @return current tps
     */
    int getCurrentTps();

    /**
     * Get the current server tick.
     * @return server tick
     */
    long getTick();

    PluginManager getPluginManager();

    ResourcePackManager getResourcePackManager();

    LevelManager getLevelManager();

    EventManager getEventManager();

    Scheduler getScheduler();

    Set<Scheduler> getSyncedSchedulers();

    /**
     * Sync a {@link Scheduler} to the server tick.
     * @param scheduler scheduler to sync
     */
    void syncScheduler(Scheduler scheduler);

    /**
     * Desync a {@link Scheduler} from the server tick.
     * @param scheduler scheduler to desync
     * @return if the scheduler was desynced
     */
    boolean desyncScheduler(Scheduler scheduler);

    Logger getLogger();

    /**
     * Get the path to the root server directory.
     * @return path to the root server directory
     */
    String getRootDirectory();



}