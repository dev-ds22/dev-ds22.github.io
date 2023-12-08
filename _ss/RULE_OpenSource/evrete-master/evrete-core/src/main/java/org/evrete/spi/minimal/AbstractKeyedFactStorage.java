package org.evrete.spi.minimal;

import org.evrete.api.*;
import org.evrete.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

abstract class AbstractKeyedFactStorage<T extends AbstractFactsMap<?>> implements KeyedFactStorage {
    private final T[] maps;
    private KeyState currentRecord = null;

    AbstractKeyedFactStorage(Class<T> mapType, Function<KeyMode, T> mapSupplier) {
        //this.fields = fields;
        this.maps = CollectionUtils.array(mapType, KeyMode.values().length);
        for (KeyMode mode : KeyMode.values()) {
            this.maps[mode.ordinal()] = mapSupplier.apply(mode);
        }
    }

    abstract KeyState writeKey(ValueHandle h);

    @Override
    public final ReIterator<FactHandleVersioned> values(KeyMode mode, MemoryKey key) {
        return get(mode).values(key);
    }

    @Override
    public final void clear() {
        for (T map : maps) {
            map.clear();
        }
    }

    private void insert(IntToValueHandle key, int keyHash, Collection<FactHandleVersioned> factHandles) {
        if (get(KeyMode.OLD_OLD).hasKey(keyHash, key)) {
            // Existing key
            get(KeyMode.OLD_NEW).add(key, keyHash, factHandles);
        } else {
            // New key
            get(KeyMode.NEW_NEW).add(key, keyHash, factHandles);
        }
    }


    @Override
    public void write(ValueHandle partialKey) {
        this.currentRecord = writeKey(partialKey);
    }

    @Override
    public void write(Collection<FactHandleVersioned> factHandles) {
        insert(currentRecord.values, currentRecord.hash, factHandles);
        this.currentRecord = null;
    }

    public final ReIterator<MemoryKey> keys(KeyMode keyMode) {
        return get(keyMode).keys();
    }

    final T get(KeyMode mode) {
        return maps[mode.ordinal()];
    }

    @Override
    public String toString() {
        return Arrays.toString(maps);
    }

    static class KeyState {
        int hash;
        IntToValueHandle values;
    }
}
