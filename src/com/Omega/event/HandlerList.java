package com.Omega.event;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores handlers per event. Based on lahwran's fevents.
 */
class HandlerList {

	private final EnumMap<EventPriority, ArrayDeque<EventListener>> handlerSlots;

	private EventListener[] bakedList;

	/**
	 * Create a new handler list and initialize using an EventPriority. The
	 * HandlerList is then added to meta-list for use in bakeAll().
	 */
	public HandlerList() {
		this.handlerSlots =
			new EnumMap<EventPriority, ArrayDeque<EventListener>>(
				EventPriority.class);
		for (EventPriority o : EventPriority.values()) {
			this.handlerSlots.put(o, new ArrayDeque<EventListener>());
		}
		this.bakedList = null;
	}

	/**
	 * Creates an array of listeners that can be returned. This is done whenever
	 * the hashset changes and saves on memory.
	 */
	private synchronized void bake() {
		if (this.bakedList != null) {
			return;// The list is still valid, so do not bake it again
		}
		// A temporary list of entries
		ArrayDeque<EventListener> entries = new ArrayDeque<EventListener>();

		// add all of the listeners, in priority order, to the entries list
		for (Map.Entry<EventPriority, ArrayDeque<EventListener>> entry : this.handlerSlots
			.entrySet()) {
			entries.addAll(entry.getValue());
		}
		// bake the list into an array
		this.bakedList = entries.toArray(new EventListener[entries.size()]);
	}

	/**
	 * Get the baked registered listeners associated with this handler list
	 *
	 * @return The listeners registered
	 */
	public EventListener[] getRegisteredListeners() {
		EventListener[] handlers;
		while ((handlers = this.bakedList) == null) {
			this.bake(); // This prevents fringe cases of returning null
		}
		return handlers;
	}

	/**
	 * Register a new listener in this handler list.
	 *
	 * @param listener The listener to register
	 * @throws IllegalStateException if the listener is already registered
	 */
	public synchronized void register(EventListener listener) {
		if (this.handlerSlots.get(listener.getPriority()).contains(listener)) {
			throw new IllegalStateException(
				"This listener is already registered to priority "
					+ listener.getPriority().toString());
		}
		this.bakedList = null;
		this.handlerSlots.get(listener.getPriority()).add(listener);
	}

	/**
	 * Register a collection of new listeners in this handler list.
	 *
	 * @param listeners The collection to register
	 */
	public void registerAll(Collection<EventListener> listeners) {
		for (EventListener listener : listeners) {
			this.register(listener);
		}
	}

	/**
	 * Remove a listener from a specific order slot.
	 *
	 * @param listener The listener to unregister
	 */
	public synchronized void unregister(EventListener listener) {
		if (this.handlerSlots.get(listener.getPriority()).remove(listener)) {
			this.bakedList = null;
		}
	}

	/**
	 * Remove a specific listener from this handler
	 *
	 * @param listener listener to remove
	 */
	public synchronized void unregister(Listener listener) {
		// go through each priority
		for (ArrayDeque<EventListener> list : this.handlerSlots.values()) {
			// remove the listener from each list
			for (Iterator<EventListener> iter = list.iterator(); iter.hasNext();) {
				if (iter.next().equals(listener)) {
					iter.remove();
				}
			}
		}

		this.bakedList = null;
	}

	/**
	 * Unregisters all handlers.
	 */
	public synchronized void unregisterAll() {
		for (ArrayDeque<EventListener> list : this.handlerSlots.values()) {
			list.clear();
		}
		this.bakedList = null;
	}
}
