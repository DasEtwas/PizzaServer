package io.github.willqi.pizzaserver.server.plugin;

import io.github.willqi.pizzaserver.server.Server;
import io.github.willqi.pizzaserver.server.plugin.events.Event;

public class PluginManager {

    private Server server;

    public PluginManager(Server server) {
        this.server = server;
    }

    public void callEvent(Event event) {

    }

}
