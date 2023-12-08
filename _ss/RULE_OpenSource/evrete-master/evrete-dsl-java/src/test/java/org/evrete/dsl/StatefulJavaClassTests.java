package org.evrete.dsl;

import org.evrete.KnowledgeService;
import org.evrete.api.*;
import org.evrete.dsl.rules.*;
import org.evrete.util.NextIntSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.List;

class StatefulJavaClassTests {
    private static KnowledgeService service;

    @BeforeAll
    static void setUpClass() {
        service = new KnowledgeService();
    }

    @AfterAll
    static void shutDownClass() {
        service.shutdown();
    }

    private static StatefulSession session(Knowledge knowledge, ActivationMode mode) {
        return knowledge.newStatefulSession().setActivationMode(mode);
    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void primeTest1(ActivationMode mode) throws IOException {
        Knowledge knowledge = service.newKnowledge(DSLClassProvider.class, SampleRuleSet1.class);
        try (StatefulSession session = session(knowledge, mode)) {
            assert session.getRules().size() == 1;
            for (int i = 2; i < 100; i++) {
                session.insert(i);
            }
            session.fire();

            NextIntSupplier primeCounter = new NextIntSupplier();
            session.forEachFact((h, o) -> primeCounter.next());

            assert primeCounter.get() == 25;
        }
    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void primeTest1_1(ActivationMode mode) throws IOException {
        TypeResolver typeResolver = service.newTypeResolver();
        Knowledge knowledge = service.newKnowledge(DSLClassProvider.class, typeResolver, SampleRuleSet1.class);
        try (StatefulSession session = session(knowledge, mode)) {
            assert session.getRules().size() == 1;
            for (int i = 2; i < 100; i++) {
                session.insert(i);
            }
            session.fire();

            NextIntSupplier primeCounter = new NextIntSupplier();
            session.forEachFact((h, o) -> primeCounter.next());

            assert primeCounter.get() == 25;
        }
    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void primeTest2(ActivationMode mode) throws IOException {
        Knowledge knowledge = service.newKnowledge(DSLClassProvider.class, SampleRuleSet2.class);
        try (StatefulSession session = session(knowledge, mode)) {
            assert session.getRules().size() == 1;
            for (int i = 2; i < 100; i++) {
                session.insert(i);
            }
            session.fire();

            NextIntSupplier primeCounter = new NextIntSupplier();
            session.forEachFact((h, o) -> primeCounter.next());

            assert primeCounter.get() == 25;
        }
    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void primeTest3(ActivationMode mode) throws IOException {
        Knowledge knowledge = service.newKnowledge(AbstractDSLProvider.PROVIDER_JAVA_C, SampleRuleSet3.class);
        try (StatefulSession session = session(knowledge, mode)) {
            assert session.getRules().size() == 1;
            for (int i = 2; i < 100; i++) {
                session.insert(i);
            }
            session.fire();

            NextIntSupplier primeCounter = new NextIntSupplier();
            session.forEachFact((h, o) -> primeCounter.next());

            assert primeCounter.get() == 25;
        }
    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void sortInheritance1(ActivationMode mode) throws IOException {
        Knowledge knowledge = service.newKnowledge(AbstractDSLProvider.PROVIDER_JAVA_C, SortedRuleSet1.class);
        try (StatefulSession session = session(knowledge, mode)) {
            List<RuntimeRule> rules = session.getRules();

            assert rules.size() == 5;
            assert rules.get(0).getName().endsWith("rule2"); // Salience 100
            assert rules.get(1).getName().endsWith("rule3"); // Salience 10
            assert rules.get(2).getName().endsWith("rule1"); // Salience -1
            assert rules.get(3).getName().endsWith("rule5");
            assert rules.get(4).getName().endsWith("rule4");
        }

    }

    @ParameterizedTest
    @EnumSource(ActivationMode.class)
    void sortInheritance2(ActivationMode mode) throws IOException {
        Knowledge knowledge = service.newKnowledge(AbstractDSLProvider.PROVIDER_JAVA_C, SortedRuleSet2.class);
        try (StatefulSession session = session(knowledge, mode)) {
            List<RuntimeRule> rules = session.getRules();
            assert rules.size() == 5 : "Actual: " + rules.size() + ": " + rules;
            assert rules.get(0).getName().endsWith("rule2"); // Salience 100
            assert rules.get(1).getName().endsWith("rule3"); // Salience 10
            assert rules.get(2).getName().endsWith("rule1"); // Salience -1
            assert rules.get(3).getName().endsWith("rule5");
            assert rules.get(4).getName().endsWith("rule4");
        }
    }

}
