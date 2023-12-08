package org.evrete.benchmarks.helper;

import org.evrete.api.ReIterator;
import org.evrete.api.StatefulSession;
import org.evrete.collections.CollectionReIterator;
import org.evrete.collections.LinearHashSet;
import org.evrete.collections.LinkedDataRW;
import org.evrete.collections.LinkedDataRWD;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.event.rule.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class TestUtils {

    public static long nanoExecTime(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        return System.nanoTime() - t0;
    }

    private static <T> void deleteFrom(Collection<T> collection, Predicate<T> predicate) {
        LinkedList<T> selected = new LinkedList<>();
        for (T obj : collection) {
            if (predicate.test(obj)) selected.add(obj);
        }

        for (T o : selected) {
            collection.remove(o);
        }
    }

    static Collection<FactEntry> sessionFacts(StatefulSession s) {
        Collection<FactEntry> col = new LinkedList<>();
        s.forEachFact((handle, fact) -> col.add(new FactEntry(handle, fact)));
        return col;
    }


    public static KieContainer droolsKnowledge(String file) {
        KieServices ks = KieServices.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(new File(file)));
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        return ks.newKieContainer(kr.getDefaultReleaseId());
    }


    public static <Z> IterableSet<Z> setOf(Set<Z> set) {
        return new IterableSet<Z>() {
            @Override
            public boolean contains(Z element) {
                return set.contains(element);
            }

            @Override
            public ReIterator<Z> iterator() {
                return new CollectionReIterator<>(set);
            }

            @Override
            public boolean remove(Z element) {
                return set.remove(element);
            }

            @Override
            public boolean add(Z element) {
                return set.add(element);
            }

            @Override
            public long size() {
                return set.size();
            }

            @Override
            public Stream<Z> stream() {
                return set.stream();
            }

            @Override
            public void delete(Predicate<Z> predicate) {
                deleteFrom(set, predicate);
            }

            @Override
            public void clear() {
                set.clear();
            }

            @Override
            public void forEach(Consumer<? super Z> consumer) {
                set.forEach(consumer);
            }

            @Override
            public String toString() {
                return set.toString();
            }
        };
    }

    public static <Z> IterableCollection<Z> collectionOf(final LinkedList<Z> list) {
        return new IterableCollection<Z>() {
            @Override
            public boolean add(Z element) {
                list.add(element);
                return true;
            }

            @Override
            public long size() {
                return list.size();
            }

            @Override
            public void delete(Predicate<Z> predicate) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public Stream<Z> stream() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ReIterator<Z> iterator() {
                return new CollectionReIterator<>(list);
            }

            @Override
            public void forEach(Consumer<? super Z> consumer) {
                list.iterator().forEachRemaining(consumer);
            }
        };
    }

    public static <Z> IterableCollection<Z> collectionOf(final LinkedDataRWD<Z> list) {
        return new IterableCollection<Z>() {
            @Override
            public boolean add(Z element) {
                list.add(element);
                return true;
            }

            @Override
            public long size() {
                return list.size();
            }

            @Override
            public void delete(Predicate<Z> predicate) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public Stream<Z> stream() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ReIterator<Z> iterator() {
                return list.iterator();
            }

            @Override
            public void forEach(Consumer<? super Z> consumer) {
                list.iterator().forEachRemaining(consumer);
            }
        };
    }

    public static <Z> IterableCollection<Z> collectionOf(final LinkedDataRW<Z> list) {
        return new IterableCollection<Z>() {
            @Override
            public boolean add(Z element) {
                list.add(element);
                return true;
            }

            @Override
            public long size() {
                return list.size();
            }

            @Override
            public void delete(Predicate<Z> predicate) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                list.clear();
            }

            @Override
            public Stream<Z> stream() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ReIterator<Z> iterator() {
                return list.iterator();
            }

            @Override
            public void forEach(Consumer<? super Z> consumer) {
                list.iterator().forEachRemaining(consumer);
            }
        };
    }

    public static <Z> IterableSet<Z> setOf(LinearHashSet<Z> set) {
        return new IterableSet<Z>() {
            @Override
            public boolean contains(Z element) {
                return set.contains(element);
            }

            @Override
            public ReIterator<Z> iterator() {
                return set.iterator();
            }

            @Override
            public boolean remove(Z element) {
                return set.remove(element);
            }

            @Override
            public boolean add(Z element) {
                return set.addVerbose(element);
            }

            @Override
            public long size() {
                return set.size();
            }

            @Override
            public Stream<Z> stream() {
                return set.stream();
            }

            @Override
            public void delete(Predicate<Z> predicate) {
                set.delete(predicate);
            }

            @Override
            public void clear() {
                set.clear();
            }

            @Override
            public void forEach(Consumer<? super Z> consumer) {
                set.forEach(consumer);
            }

            @Override
            public String toString() {
                return set.toString();
            }
        };
    }

    public static void logAgenda(KieSession dSession) {
        dSession.addEventListener(new AgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                System.out.println("Created: " + event);
            }

            @Override
            public void matchCancelled(MatchCancelledEvent event) {
                System.out.println("Cancelled: " + event);
            }

            @Override
            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                System.out.println("Before fire: " + event);
            }

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                System.out.println("After fire: " + event);
            }

            @Override
            public void agendaGroupPopped(AgendaGroupPoppedEvent agendaGroupPoppedEvent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void agendaGroupPushed(AgendaGroupPushedEvent agendaGroupPushedEvent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {
                throw new UnsupportedOperationException();
            }
        });
    }
}
