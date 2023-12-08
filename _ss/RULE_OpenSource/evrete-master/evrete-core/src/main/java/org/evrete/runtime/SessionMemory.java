package org.evrete.runtime;

import org.evrete.api.KeyedFactStorage;
import org.evrete.api.MemoryFactory;
import org.evrete.api.ReIterator;
import org.evrete.api.Type;
import org.evrete.collections.ArrayOf;
import org.evrete.runtime.evaluation.MemoryAddress;

public class SessionMemory extends MemoryComponent implements Iterable<TypeMemory> {
    private final ArrayOf<TypeMemory> typedMemories;

    SessionMemory(AbstractRuleSession<?> runtime, MemoryFactory memoryFactory) {
        super(runtime, memoryFactory);
        this.typedMemories = new ArrayOf<>(new TypeMemory[]{});
    }

    @Override
    protected void clearLocalData() {
    }

    @Override
    public ReIterator<TypeMemory> iterator() {
        return typedMemories.iterator();
    }

    void destroy() {
        clear();
        for (TypeMemory tm : typedMemories) {
            tm.destroy();
        }
    }

    void onNewActiveField(ActiveField newField) {
        getCreateUpdate(newField.type());
    }

    void onNewAlphaBucket(MemoryAddress address) {
        getCreateUpdate(address.fields().type())
                .onNewAlphaBucket(address);
    }

    KeyedFactStorage getBetaFactStorage(MemoryAddress address) {
        return getMemoryBucket(address).getFieldData();
    }

    KeyMemoryBucket getMemoryBucket(MemoryAddress address) {
        return get(address.fields().type()).getMemoryBucket(address);
    }

    public TypeMemory get(Type<?> t) {
        return get(t.getId());
    }

    TypeMemory getCreateUpdate(int type) {
        TypeMemory m = typedMemories.get(type);
        if (m == null) {
            m = new TypeMemory(this, type);
            typedMemories.set(type, m);
        } else {
            // Making sure type uses the same alpha conditions
            m.updateCachedData();
        }
        return m;
    }

    public TypeMemory get(int typeId) {
        TypeMemory m = typedMemories.get(typeId);
        if (m == null) {
            throw new IllegalStateException("No memory has been allocated for type id: " + typeId + ". Probable cause: the type has never been mentioned in any LHS.");
        }
        return m;
    }

    void commitBuffer() {
        typedMemories.forEach(TypeMemory::commitBuffer);
    }
}
