package org.evrete.spi.minimal;

import org.evrete.api.KeyMode;
import org.evrete.api.ValueHandle;

class KeyedFactStorageMulti extends AbstractKeyedFactStorage<FactsMapMulti> {
    private final MultiState multiState;

    KeyedFactStorageMulti(int initialSize, int fieldCount) {
        super(FactsMapMulti.class, mode -> new FactsMapMulti(fieldCount, initialSize));
        this.multiState = new MultiState(fieldCount);
    }

    @Override
    KeyState writeKey(ValueHandle h) {
        return this.multiState.update(h);
    }

    @Override
    public void commitChanges() {
        FactsMapMulti main = get(KeyMode.OLD_OLD);
        main.merge(get(KeyMode.NEW_NEW));
        main.merge(get(KeyMode.OLD_NEW));
    }

    private static class MultiState extends AbstractKeyedFactStorage.KeyState {
        private final ValueHandle[] data;
        private int currentPosition = 0;

        MultiState(int size) {
            this.data = new ValueHandle[size];
            super.values = i -> data[i];
        }

        MultiState update(ValueHandle h) {
            if (currentPosition == data.length) {
                currentPosition = 0;
                super.hash = 0;
            }

            this.data[currentPosition++] = h;
            super.hash += 37 * h.hashCode();
            return this;
        }
    }

}
