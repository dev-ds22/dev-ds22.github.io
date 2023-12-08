package com.deliveredtechnologies.rulebook;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RuleBook}.
 */
public class RuleBookTest {
  @Test
  @SuppressWarnings("unchecked")
  public void ruleBooksRunRules() {
    Rule<String> rule1 = (Rule<String>) mock(Rule.class);
    Rule<String> rule2 = (Rule<String>) mock(Rule.class);
    Rule<String> rule3 = (Rule<String>) mock(Rule.class);
    List<Fact<String>> factList = new ArrayList<>();
    Fact<String> fact = new Fact<String>("hello", "world");
    RuleBook<String> ruleBook = spy(new RuleBook<String>() {
      @Override
      protected void defineRules() {
      }
    });

    ruleBook.given(fact).given("goodbye", "world").addRule(rule1);
    ruleBook.addRule(rule2);
    ruleBook.addRule(rule3);
    ruleBook.run();
    ruleBook.run();

    verify(rule1, times(2)).given(anyList());
    verify(rule1, times(1)).setNextRule(rule2);
    verify(rule2, times(1)).setNextRule(rule3);
    verify(ruleBook, times(1)).given("goodbye", "world");
    verify(ruleBook, times(0)).defineRules(); //not run because rules were already added
    verify(rule1, times(2)).run();
  }

  @Test
  public void addingNullRuleToRuleBookDoesntBreak() {
    RuleBook<String> ruleBook = spy(new RuleBook<String>() {
      @Override
      protected void defineRules() {
        addRule(null);
      }
    });
    ruleBook.run();

    verify(ruleBook, times(1)).defineRules();
  }
}
