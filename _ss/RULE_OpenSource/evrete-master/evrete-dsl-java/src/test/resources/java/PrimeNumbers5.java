package org.mypackage;

import org.evrete.api.RhsContext;
import org.evrete.dsl.annotation.Fact;
import org.evrete.dsl.annotation.MethodPredicate;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.Where;

public class PrimeNumbers5 {

    @Rule("Delete non-prime integers")
    @Where(
            methods = {@MethodPredicate(method = "test", args = {"$i1.intValue", "$i2.intValue", "$i3.intValue"})}
    )
    public static void rule(RhsContext ctx, int $i1, @Fact("$i2") int i2, int $i3) {
        ctx.delete($i3);
    }

    public static boolean test(int i1, int i2, int i3) {
        return i3 == i1 * i2;
    }
}