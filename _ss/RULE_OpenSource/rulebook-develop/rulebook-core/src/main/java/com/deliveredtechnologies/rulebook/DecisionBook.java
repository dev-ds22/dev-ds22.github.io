package com.deliveredtechnologies.rulebook;

import java.util.Optional;

/**
 * DecisionBook is a type of {@link RuleBook} that stores a return type linked to all the rules in the DecisionBook.
 */
@Deprecated
public abstract class DecisionBook<T, U> extends RuleBook<T> {
  private Result<U> _result = new Result<U>();

  /**
   * The withDefaultResult method allows a default result value to be specified.
   * When using the DSL syntax to chain calls, this method should be the first one specified.
   *
   * @param result the initial value of the stored result
   * @return the current DecisionBook object
   */
  public final DecisionBook<T, U> withDefaultResult(U result) {
    _result.setValue(result);
    return this;
  }

  /**
   * The addRule() method allows a rule to be added to the DecisionBook in the abstract defineRules
   * method.
   * @param rule  the Decision rule to be added to the DecisionBook
   */
  public void addRule(Decision<T, U> rule) {
    if (rule == null) {
      return;
    }

    super.addRule(rule);
    rule.setResult(_result);
  }

  /**
   * The getResult() method allows the result of the DecisionBook rules execution to be retrieved.
   * @return the stored result value
   */
  public U getResult() {
    return _result.getValue();
  }

}
