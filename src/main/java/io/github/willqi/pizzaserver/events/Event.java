package io.github.willqi.pizzaserver.events;
public abstract class Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        if (this instanceof Cancellable) {
            this.cancelled = cancelled;
        } else {
            throw new EventException(this.getClass().getName() + " cannot be cancelled.");
        }
    }

}
