package org.evrete.dsl.issues;

import org.evrete.KnowledgeService;
import org.evrete.api.Knowledge;
import org.evrete.api.StatefulSession;
import org.evrete.dsl.issues.model.DayTrendFact;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class IssuesTests {
    private static KnowledgeService service;

    @BeforeAll
    static void setUpClass() {
        service = new KnowledgeService();
    }

    @AfterAll
    static void shutDownClass() {
        service.shutdown();
    }

    /**
     * <p>
     * Tests imports of third-party classes (<a href="https://github.com/andbi/evrete/issues/15">Issue 15</a>)
     * </p>
     */
    @Test
    void issue15() throws IOException {
        Knowledge knowledge = service.newKnowledge("JAVA-SOURCE", new File("src/test/resources/java/issues/MyRS.java").toURI().toURL());
        try (StatefulSession session = knowledge.newStatefulSession()) {
            DayTrendFact object = new DayTrendFact();
            object.value = 6;
            session.insertAndFire(object);
            assert object.value == -1;
        }
    }

}
