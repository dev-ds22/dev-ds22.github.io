package org.evrete.api;

import org.evrete.api.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

public class TypeWrapper<T> implements Type<T> {
    private final Type<T> delegate;

    public TypeWrapper(Type<T> delegate) {
        this.delegate = delegate;
    }

    public final Type<T> getDelegate() {
        return delegate;
    }

    @Override
    public final int getId() {
        return this.delegate.getId();
    }

    @Override
    public final String getJavaType() {
        return delegate.getJavaType();
    }

    @Override
    public Class<T> resolveJavaType() {
        return delegate.resolveJavaType();
    }

    @Override
    public final String getName() {
        return delegate.getName();
    }

    @Override
    public Collection<TypeField> getDeclaredFields() {
        return delegate.getDeclaredFields();
    }

    @Override
    public @NonNull TypeField getField(@NonNull String name) {
        return delegate.getField(name);
    }

    @Override
    public <V> TypeField declareField(String name, Class<V> type, Function<T, V> function) {
        return delegate.declareField(name, type, function);
    }

    @Override
    public Type<T> copyOf() {
        return new TypeWrapper<>(delegate.copyOf());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Type<?>) {
            Type<?> that = (Type<?>) o;
            return getName().equals(that.getName());
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return delegate.hashCode();
    }
}
