package com.tinkerpop.blueprints.pgm.util.wrappers.event;

import com.tinkerpop.blueprints.pgm.CloseableIterable;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Index;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;
import com.tinkerpop.blueprints.pgm.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.blueprints.pgm.util.wrappers.event.util.EventEdgeIterable;
import com.tinkerpop.blueprints.pgm.util.wrappers.event.util.EventVertexIterable;

import java.util.Iterator;
import java.util.List;

/**
 * An index that wraps graph elements in the "evented" way. This class does not directly raise graph events, but
 * passes the GraphChangedListener to the edges and vertices returned from indices so that they may raise graph
 * events.
 *
 * @author Stephen Mallette
 */
public class EventIndex<T extends Element> implements Index<T> {
    protected final Index<T> rawIndex;
    protected final List<GraphChangedListener> graphChangedListeners;

    public EventIndex(final Index<T> rawIndex, final List<GraphChangedListener> graphChangedListeners) {
        this.rawIndex = rawIndex;
        this.graphChangedListeners = graphChangedListeners;
    }

    public void remove(final String key, final Object value, final T element) {
        this.rawIndex.remove(key, value, (T) ((EventElement) element).getBaseElement());
    }

    public void put(final String key, final Object value, final T element) {
        this.rawIndex.put(key, value, (T) ((EventElement) element).getBaseElement());
    }

    public CloseableIterable<T> get(final String key, final Object value) {
        if (Vertex.class.isAssignableFrom(this.getIndexClass())) {
            return (CloseableIterable<T>) new EventVertexIterable((Iterable<Vertex>) this.rawIndex.get(key, value), this.graphChangedListeners);
        } else {
            return (CloseableIterable<T>) new EventEdgeIterable((Iterable<Edge>) this.rawIndex.get(key, value), this.graphChangedListeners);
        }
    }

    public CloseableIterable<T> query(final String key, final Object query) {
        if (Vertex.class.isAssignableFrom(this.getIndexClass())) {
            return (CloseableIterable<T>) new EventVertexIterable((Iterable<Vertex>) this.rawIndex.query(key, query), this.graphChangedListeners);
        } else {
            return (CloseableIterable<T>) new EventEdgeIterable((Iterable<Edge>) this.rawIndex.query(key, query), this.graphChangedListeners);
        }
    }

    public long count(final String key, final Object value) {
        return this.rawIndex.count(key, value);
    }

    public String getIndexName() {
        return this.rawIndex.getIndexName();
    }

    public Class<T> getIndexClass() {
        return this.rawIndex.getIndexClass();
    }

    public String toString() {
        return StringFactory.indexString(this);
    }

}