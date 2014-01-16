package org.obiba.magma.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.obiba.magma.Datasource;
import org.obiba.magma.IncompatibleEntityTypeException;
import org.obiba.magma.NoSuchValueSetException;
import org.obiba.magma.NoSuchVariableException;
import org.obiba.magma.Value;
import org.obiba.magma.ValueSet;
import org.obiba.magma.ValueTable;
import org.obiba.magma.ValueType;
import org.obiba.magma.Variable;
import org.obiba.magma.VariableEntity;
import org.obiba.magma.VariableValueSource;
import org.obiba.magma.support.ValueSetBean;
import org.obiba.magma.support.VariableEntityBean;
import org.obiba.magma.test.AbstractMagmaTest;
import org.obiba.magma.type.TextType;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings({ "PMD.NcssMethodCount", "OverlyLongMethod", "OverlyCoupledClass" })
public class ViewTest extends AbstractMagmaTest {
  //
  // Test Methods
  //

  @Test
  public void testHasValueSetWithDefaultWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");

    expect(valueTableMock.hasValueSet(variableEntity)).andReturn(true);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    boolean result = view.hasValueSet(variableEntity);

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isTrue();
  }

  @Test
  public void testHasValueSetWithIncludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.hasValueSet(variableEntity)).andReturn(true);
    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    expect(whereClauseMock.where(valueSet)).andReturn(true);
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    boolean result = view.hasValueSet(variableEntity);

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);

    // Verify state.
    assertThat(result).isTrue();
  }

  @Test
  public void testHasValueSetWithExcludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.hasValueSet(variableEntity)).andReturn(true);
    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    expect(whereClauseMock.where(valueSet)).andReturn(false);
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    boolean result = view.hasValueSet(variableEntity);

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);

    // Verify state.
    assertThat(result).isFalse();
  }

  @Test
  public void testGetValueSetReturnsValueSetThatRefersToView() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.getName()).andReturn("wrappedTable").anyTimes();
    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    ValueSet viewValueSet = view.getValueSet(variableEntity);

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(viewValueSet).isNotNull();
    assertThat(viewValueSet.getValueTable()).isNotNull();
    assertThat(viewValueSet.getValueTable().getName()).isEqualTo("view");
  }

  @Test
  public void testGetValueSetWithDefaultWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.getName()).andReturn("wrappedTable").anyTimes();
    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    ValueSet result = null;
    try {
      result = view.getValueSet(variableEntity);
    } catch(NoSuchValueSetException ex) {
      fail("ValueSet not selected");
    }

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
  }

  @Test
  public void testGetValueSetWithIncludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    expect(whereClauseMock.where(valueSet)).andReturn(true);
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    ValueSet result = null;
    try {
      result = view.getValueSet(variableEntity);
    } catch(NoSuchValueSetException ex) {
      fail("ValueSet not selected");
    }

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
  }

  @Test(expected = NoSuchValueSetException.class)
  public void testGetValueSetWithExcludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);

    expect(valueTableMock.getValueSet(variableEntity)).andReturn(valueSet);
    expect(whereClauseMock.where(valueSet)).andReturn(false);
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    view.getValueSet(variableEntity);

    // Verify behaviour.
    verify(valueTableMock);
  }

  @Test
  public void testGetValueSetsWithDefaultWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);

    Collection<ValueSet> valueSets = new ArrayList<>();
    VariableEntity variableEntityFoo = new VariableEntityBean("type", "foo");
    VariableEntity variableEntityBar = new VariableEntityBean("type", "bar");
    ValueSet valueSetFoo = new ValueSetBean(valueTableMock, variableEntityFoo);
    ValueSet valueSetBar = new ValueSetBean(valueTableMock, variableEntityBar);
    valueSets.add(valueSetFoo);
    valueSets.add(valueSetBar);

    expect(valueTableMock.getValueSets()).andReturn(valueSets);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    Iterable<ValueSet> result = view.getValueSets();

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(containsValueSet(result, valueSetFoo)).isTrue();
    assertThat(containsValueSet(result, valueSetBar)).isTrue();
  }

  @Test
  public void testGetValueSetsWithIncludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);

    Collection<ValueSet> valueSets = new ArrayList<>();
    VariableEntity variableEntityFoo = new VariableEntityBean("type", "foo");
    VariableEntity variableEntityBar = new VariableEntityBean("type", "bar");
    ValueSet valueSetFoo = new ValueSetBean(valueTableMock, variableEntityFoo);
    ValueSet valueSetBar = new ValueSetBean(valueTableMock, variableEntityBar);
    valueSets.add(valueSetFoo);
    valueSets.add(valueSetBar);

    expect(valueTableMock.getValueSets()).andReturn(valueSets);
    expect(whereClauseMock.where((ValueSet) anyObject())).andReturn(true).anyTimes();
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    Iterable<ValueSet> result = view.getValueSets();

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(containsValueSet(result, valueSetFoo)).isTrue();
    assertThat(containsValueSet(result, valueSetBar)).isTrue();
  }

  @Test
  public void testGetValueSetsWithExcludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);

    Collection<ValueSet> valueSets = new ArrayList<>();
    VariableEntity variableEntityInclude = new VariableEntityBean("type", "include");
    VariableEntity variableEntityExclude = new VariableEntityBean("type", "exclude");
    ValueSet valueSetInclude = new ValueSetBean(valueTableMock, variableEntityInclude);
    ValueSet valueSetExclude = new ValueSetBean(valueTableMock, variableEntityExclude);
    valueSets.add(valueSetInclude);
    valueSets.add(valueSetExclude);

    expect(valueTableMock.getValueSets()).andReturn(valueSets);
    expect(whereClauseMock.where(valueSetInclude)).andReturn(true).anyTimes();
    expect(whereClauseMock.where(valueSetExclude)).andReturn(false).anyTimes();
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    Iterable<ValueSet> result = view.getValueSets();

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(containsValueSet(result, valueSetInclude)).isTrue();
  }

  @Test
  public void testGetVariableWithDefaultSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();

    expect(valueTableMock.getVariable(variable.getName())).andReturn(variable);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    Variable result = null;
    try {
      result = view.getVariable(variable.getName());
    } catch(NoSuchVariableException ex) {
      fail("Variable not selected");
    }

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(variable.getName()).isEqualTo(result.getName());
  }

  @Test
  public void testGetVariableWithIncludingSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    SelectClause selectClauseMock = createMock(SelectClause.class);
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();

    expect(valueTableMock.getVariable(variable.getName())).andReturn(variable);
    expect(selectClauseMock.select(variable)).andReturn(true);
    replay(valueTableMock, selectClauseMock);

    View view = View.Builder.newView("view", valueTableMock).select(selectClauseMock).build();
    Variable result = null;
    try {
      result = view.getVariable(variable.getName());
    } catch(NoSuchVariableException ex) {
      fail("Variable not selected");
    }

    // Verify behaviour.
    verify(valueTableMock, selectClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(variable.getName()).isEqualTo(result.getName());
  }

  @Test(expected = NoSuchVariableException.class)
  public void testGetVariableWithExcludingSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    SelectClause selectClauseMock = createMock(SelectClause.class);
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();

    expect(valueTableMock.getVariable(variable.getName())).andReturn(variable);
    expect(selectClauseMock.select(variable)).andReturn(false);
    replay(valueTableMock, selectClauseMock);

    View view = View.Builder.newView("view", valueTableMock).select(selectClauseMock).build();
    view.getVariable(variable.getName());

    // Verify behaviour.
    verify(valueTableMock, selectClauseMock);
  }

  @Test
  public void testGetVariablesWithDefaultSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);

    Collection<Variable> variables = new ArrayList<>();
    Variable variableFoo = new Variable.Builder("foo", TextType.get(), "type").build();
    Variable variableBar = new Variable.Builder("bar", TextType.get(), "type").build();
    variables.add(variableFoo);
    variables.add(variableBar);

    expect(valueTableMock.getVariables()).andReturn(variables);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    Iterable<Variable> result = view.getVariables();

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(containsVariable(result, variableFoo)).isTrue();
    assertThat(containsVariable(result, variableBar)).isTrue();
  }

  @Test
  public void testGetVariablesWithIncludingSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    SelectClause selectClauseMock = createMock(SelectClause.class);

    Collection<Variable> variables = new ArrayList<>();
    Variable variableFoo = new Variable.Builder("foo", TextType.get(), "type").build();
    Variable variableBar = new Variable.Builder("bar", TextType.get(), "type").build();
    variables.add(variableFoo);
    variables.add(variableBar);

    expect(valueTableMock.getVariables()).andReturn(variables);
    expect(selectClauseMock.select((Variable) anyObject())).andReturn(true).anyTimes();
    replay(valueTableMock, selectClauseMock);

    View view = View.Builder.newView("view", valueTableMock).select(selectClauseMock).build();
    Iterable<Variable> result = view.getVariables();

    // Verify behaviour.
    verify(valueTableMock, selectClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(containsVariable(result, variableFoo)).isTrue();
    assertThat(containsVariable(result, variableBar)).isTrue();
  }

  @Test
  public void testGetVariablesWithExcludingSelectClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    SelectClause selectClauseMock = createMock(SelectClause.class);

    Collection<Variable> variables = new ArrayList<>();
    Variable variableInclude = new Variable.Builder("include", TextType.get(), "type").build();
    Variable variableExclude = new Variable.Builder("exclude", TextType.get(), "type").build();
    variables.add(variableInclude);
    variables.add(variableExclude);

    expect(valueTableMock.getVariables()).andReturn(variables);
    expect(selectClauseMock.select(variableInclude)).andReturn(true).anyTimes();
    expect(selectClauseMock.select(variableExclude)).andReturn(false).anyTimes();
    replay(valueTableMock, selectClauseMock);

    View view = View.Builder.newView("view", valueTableMock).select(selectClauseMock).build();
    Iterable<Variable> result = view.getVariables();

    // Verify behaviour.
    verify(valueTableMock, selectClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(containsVariable(result, variableInclude)).isTrue();
  }

  @Test
  public void testGetValueWithDefaultWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);
    Value value = TextType.get().valueOf("someValue");

    expect(valueTableMock.getValue((Variable) anyObject(), (ValueSet) anyObject())).andReturn(value);
    replay(valueTableMock);

    View view = View.Builder.newView("view", valueTableMock).build();
    Value result = null;
    try {
      result = view.getValue(variable, new ValueSetWrapper(view, valueSet));
    } catch(NoSuchValueSetException ex) {
      fail("Value not selected");
    }

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(value.getValue().toString()).isEqualTo("someValue");
  }

  @Test
  public void testGetValueWithIncludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);
    Value value = TextType.get().valueOf("someValue");

    expect(valueTableMock.getValue((Variable) anyObject(), (ValueSet) anyObject())).andReturn(value);
    expect(whereClauseMock.where((ValueSet) anyObject())).andReturn(true).anyTimes();
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    Value result = null;
    try {
      result = view.getValue(variable, new ValueSetWrapper(view, valueSet));
    } catch(NoSuchValueSetException ex) {
      fail("Value not selected");
    }

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(value.getValue().toString()).isEqualTo("someValue");
  }

  @Test(expected = NoSuchValueSetException.class)
  public void testGetValueWithExcludingWhereClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    WhereClause whereClauseMock = createMock(WhereClause.class);
    VariableEntity variableEntity = new VariableEntityBean("type", "id1");
    Variable variable = new Variable.Builder("someVariable", TextType.get(), "type").build();
    ValueSet valueSet = new ValueSetBean(valueTableMock, variableEntity);
    Value value = TextType.get().valueOf("someValue");

    expect(valueTableMock.getValue(variable, valueSet)).andReturn(value);
    expect(whereClauseMock.where(valueSet)).andReturn(false).anyTimes();
    replay(valueTableMock, whereClauseMock);

    View view = View.Builder.newView("view", valueTableMock).where(whereClauseMock).build();
    view.getValue(variable, valueSet);

    // Verify behaviour.
    verify(valueTableMock, whereClauseMock);
  }

  @Test
  public void testGetVariablesWithListClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    ListClause listClauseMock = createMock(ListClause.class);

    VariableValueSource variableValueSourceMock = createMock(VariableValueSource.class);
    Collection<VariableValueSource> variableValueSourceList = new ArrayList<>();
    variableValueSourceList.add(variableValueSourceMock);

    expect(listClauseMock.getVariableValueSources()).andReturn(variableValueSourceList);
    replay(valueTableMock, listClauseMock);

    View view = View.Builder.newView("view", valueTableMock).list(listClauseMock).build();
    Iterable<Variable> result = view.getVariables();

    // Verify behaviour.
    verify(valueTableMock, listClauseMock);

    // Verify state.
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
  }

  @Test
  public void testGetVariableValueSourceWithListClause() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    ListClause listClauseMock = createMock(ListClause.class);

    VariableValueSource variableValueSourceMock = createMock(VariableValueSource.class);

    expect(listClauseMock.getVariableValueSource("variable-name")).andReturn(variableValueSourceMock).times(2);
    replay(valueTableMock, listClauseMock);

    View view = View.Builder.newView("view", valueTableMock).list(listClauseMock).build();
    VariableValueSource result = view.getVariableValueSource("variable-name");

    // Verify behaviour.
    verify(valueTableMock);

    // Verify state.
    assertThat(result).isNotNull();
  }

  @Test(expected = IncompatibleEntityTypeException.class)
  public void testCreateViewDifferentEntityType() {
    ValueTable valueTableMock = createMock(ValueTable.class);
    ListClause listClauseMock = createMock(ListClause.class);

    ViewPersistenceStrategy viewPersistenceMock = createMock(ViewPersistenceStrategy.class);
    Datasource datasourceMock = createMock(Datasource.class);

    ViewManager manager = new DefaultViewManagerImpl(viewPersistenceMock);

    Set<View> views = new HashSet<>();
    View view = View.Builder.newView("view", valueTableMock).list(listClauseMock).build();
    views.add(view);

    listClauseMock.setValueTable(view);

    Variable variable = Variable.Builder.newVariable("variable", ValueType.Factory.forName("text"), "Martian").build();

    VariableValueSource vSourceMock = createMock(VariableValueSource.class);

    Collection<VariableValueSource> variablesValueSource = new HashSet<>();
    variablesValueSource.add(vSourceMock);

    expect(viewPersistenceMock.readViews("datasource")).andReturn(views);
    expect(datasourceMock.getName()).andReturn("datasource").anyTimes();
    expect(listClauseMock.getVariableValueSources()).andReturn(variablesValueSource);
    expect(vSourceMock.getVariable()).andReturn(variable).once();
    expect(valueTableMock.getEntityType()).andReturn("NotMartian").anyTimes();

    replay(datasourceMock, valueTableMock, listClauseMock, viewPersistenceMock, vSourceMock);

    manager.decorate(datasourceMock);
    manager.addView("datasource", view, null);
  }

  //
  // Helper Methods
  //

  private boolean containsVariable(Iterable<Variable> iterable, final Variable variable) {
    return Iterables.any(iterable, new Predicate<Variable>() {
      @Override
      public boolean apply(Variable input) {
        return input.getName().equals(variable.getName());
      }
    });
  }

  private boolean containsValueSet(Iterable<ValueSet> iterable, final ValueSet valueSet) {
    return Iterables.any(iterable, new Predicate<ValueSet>() {
      @Override
      public boolean apply(ValueSet input) {
        return input.getVariableEntity().getIdentifier().equals(valueSet.getVariableEntity().getIdentifier());
      }
    });
  }
}
