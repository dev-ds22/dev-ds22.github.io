package org.evrete.runtime;

import org.evrete.AbstractRule;
import org.evrete.util.MapFunction;
import org.evrete.util.NextIntSupplier;

public final class RuleDescriptor extends AbstractRuntimeRule<FactType> {
    private final LhsDescriptor lhsDescriptor;

    private RuleDescriptor(AbstractRuntime<?, ?> runtime, AbstractRule other, String ruleName, int salience, LhsDescriptor lhsDescriptor) {
        super(runtime, other, ruleName, salience, lhsDescriptor.getFactTypes());
        this.lhsDescriptor = lhsDescriptor;
    }

    static RuleDescriptor factory(AbstractRuntime<?, ?> runtime, RuleBuilderImpl<?> rule, String ruleName, int salience) {
        LhsDescriptor lhsDescriptor = new LhsDescriptor(runtime, rule.getLhs(), new NextIntSupplier(), new MapFunction<>());
        return new RuleDescriptor(runtime, rule, ruleName, salience, lhsDescriptor);
    }

    public LhsDescriptor getLhs() {
        return lhsDescriptor;
    }

    @Override
    public RuleDescriptor set(String property, Object value) {
        super.set(property, value);
        return this;
    }
}
