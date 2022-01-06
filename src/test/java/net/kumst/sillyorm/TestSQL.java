package net.kumst.sillyorm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;



/**
 * Tests of SQLGenerator
 * 
 * @author Martin Kumst
 */
public class TestSQL {
			
	@Test
	public void testCreateTable() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		assertEquals("CREATE TABLE IF NOT EXISTS testentity (id integer primary key autoincrement, name text, value text);", generator.generateCreateTable());
	}
	
	@Test
	public void testGenerateSelect() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		assertEquals("SELECT * FROM testentity WHERE id = ?;", generator.generateSelect());
	}
	
	@Test
	public void testGenerateDelete() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		assertEquals("DELETE FROM testentity WHERE id = ?;", generator.generateDelete());
	}

	@Test
	public void testGenerateUpdate() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		assertEquals("UPDATE testentity SET name = ?, value = ? WHERE id = ?;", generator.generateUpdate());
	}	
	
	@Test
	public void testGenerateInsert() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		assertEquals("INSERT INTO testentity (id, name, value) VALUES (null, ?, ?);", generator.generateInsert());
	}
	
	@Test
	public void testGenerateSelectWithCriteriasWithoutOperators() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria(TestEntity.class, "name", Criteria.Operator.EQUAL, "nameValue"));
		criterias.add(new Criteria(TestEntity.class, "value", Criteria.Operator.EQUAL, "valueValue"));
		assertEquals("SELECT * FROM testentity WHERE name = ? AND value = ?", generator.generateSelect(criterias));
	}
	
	@Test
	public void testGenerateSelectWithCriteriasWithOperators() {
		SQLGenerator generator = new SQLGenerator(new Reflection(TestEntity.class));
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria(TestEntity.class, "name", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.AND));
		criterias.add(new Criteria(TestEntity.class, "name", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(TestEntity.class, "value", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.OR));
		criterias.add(new Criteria(TestEntity.class, "value", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.AND));
		criterias.add(new Criteria(TestEntity.class, "value", Criteria.Operator.EQUAL, "b"));
		assertEquals("SELECT * FROM testentity WHERE name = ? AND name = ? AND value = ? OR value = ? AND value = ?", generator.generateSelect(criterias));
	}
}
