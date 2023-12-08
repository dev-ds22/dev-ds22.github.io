package org.evrete.spi.minimal;

import org.evrete.api.RhsContext;
import org.evrete.api.RuntimeRule;

import java.util.function.Consumer;

public abstract class AbstractLiteralRhs implements Consumer<RhsContext>, RhsContext {
    private RhsContext ctx;

    @SuppressWarnings("WeakerAccess")
    protected abstract void doRhs();

    @Override
    public void accept(RhsContext ctx) {
        this.ctx = ctx;
        doRhs();
    }

    @Override
    public Object getObject(String name) {
        return ctx.getObject(name);
    }

    @Override
    public RhsContext update(Object obj) {
        return ctx.update(obj);
    }

    @Override
    public RhsContext delete(Object obj) {
        return ctx.delete(obj);
    }

    @Override
    public RuntimeRule getRule() {
        return ctx.getRule();
    }

    @Override
    public RhsContext insert(Object fact, boolean resolveCollections) {
        return ctx.insert(fact, resolveCollections);
    }
}
