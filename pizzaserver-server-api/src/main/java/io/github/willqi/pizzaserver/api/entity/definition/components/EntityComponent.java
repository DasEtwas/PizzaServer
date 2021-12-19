package io.github.willqi.pizzaserver.api.entity.definition.components;

public abstract class EntityComponent {

    public abstract String getName();

    @Override
    public String toString() {
        return this.getName();
    }

}
