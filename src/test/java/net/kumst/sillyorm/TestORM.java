package net.kumst.sillyorm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.kumst.sillyorm.configuration.MemConfiguration;

public class TestORM {
	
	private static class A {
		
		private String x;
		private String y;
		private String a;
		private String id;
		private String b;
		private String c;
		private String d;
		private String e;
		
		public A() {
			
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String v) {
			id = v;
		}
		
		public String getX() {
			return x;
		}
		
		public void setX(String v) {
			x = v;
		}
		
		public String getY() {
			return y;
		}
		
		public void setY(String vv) {
			y = vv;;
		}
		
		public String getA() {
			return a;
		}
		
		public void setA(String v) {
			a = v;
		}
		
		public String getB() {
			return b;
		}
		
		public void setB(String bb) {
			b = bb;
		}
		
		public String getC() {
			return c;
		}
		
		public void setC(String cc) {
			c = cc;
		}
		
		public String getD() {
			return d;
		}
		
		public void setD(String dd) {
			d = dd;
		}
		
		public String getE() {
			return e;
		}
		
		public void setE(String ee) {
			e = ee;
		}
	}

	@Test
	public void testORM() {
		A test = new A();
		test.setA("A");
		test.setX("X");
		test.setY("Y");
		
		// Insert new object
		ORM orm = new ORM(new MemConfiguration());
		orm.prepareTableFor(A.class);
		orm.save(test);
		assertEquals("1", test.getId());
		
		// Select object
		A result = (A) orm.find(A.class, 1);
		
		// Check selected object
		assertEquals("1", result.getId());
		assertEquals("A", result.getA());
		assertEquals("X", result.getX());
		assertEquals("Y", result.getY());
		
		// Update object
		result.setX("XXX");
		result.setY("YYY");
		orm.save(result);
		
		// Select updated object
		A updated = (A) orm.find(A.class,  1);
		
		// Check updated object
		assertEquals("1", result.getId());
		assertEquals("A", result.getA());
		assertEquals("XXX", result.getX());
		assertEquals("YYY", result.getY());
		
		// Delete updated object
		orm.delete(updated);
		
		// Try to select deleted object
		A deleted = (A) orm.find(A.class, 1);
		assertNull(deleted);
	}
	
	@Test
	public void testCreateTable() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		assertEquals("CREATE TABLE IF NOT EXISTS a (id integer primary key autoincrement, a text, b text, c text, d text, e text, x text, y text);", generator.generateCreateTable());
	}
	
	@Test
	public void testGenerateSelect() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		assertEquals("SELECT * FROM a WHERE id = ?;", generator.generateSelect());
	}
	
	@Test
	public void testGenerateDelete() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		assertEquals("DELETE FROM a WHERE id = ?;", generator.generateDelete());
	}

	@Test
	public void testGenerateUpdate() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		assertEquals("UPDATE a SET a = ?, b = ?, c = ?, d = ?, e = ?, x = ?, y = ? WHERE id = ?;", generator.generateUpdate());
	}	
	
	@Test
	public void testGenerateInsert() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		assertEquals("INSERT INTO a (id, a, b, c, d, e, x, y) VALUES (null, ?, ?, ?, ?, ?, ?, ?);", generator.generateInsert());
	}
	
	@Test
	public void testGenerateSelectWithCriteriasWithoutOperators() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		List<Criteria> criterias = new ArrayList();
		criterias.add(new Criteria(A.class, "a", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(A.class, "b", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(A.class, "c", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(A.class, "d", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(A.class, "e", Criteria.Operator.EQUAL, "b"));
		assertEquals("SELECT * FROM a WHERE a = ? AND b = ? AND c = ? AND d = ? AND e = ?", generator.generateSelect(criterias));
	}
	
	@Test
	public void testGenerateSelectWithCriteriasWithOperators() {
		SQLGenerator generator = new SQLGenerator(new Reflection(A.class));
		List<Criteria> criterias = new ArrayList();
		criterias.add(new Criteria(A.class, "a", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.AND));
		criterias.add(new Criteria(A.class, "b", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(A.class, "c", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.OR));
		criterias.add(new Criteria(A.class, "d", Criteria.Operator.EQUAL, "b"));
		criterias.add(new Criteria(Criteria.Operator.AND));
		criterias.add(new Criteria(A.class, "e", Criteria.Operator.EQUAL, "b"));
		assertEquals("SELECT * FROM a WHERE a = ? AND b = ? AND c = ? OR d = ? AND e = ?", generator.generateSelect(criterias));
	}
	
	@Test
	public void testMultipleRecords() {
		ORM orm = new ORM(new MemConfiguration());
		orm.prepareTableFor(A.class);
		
		for (int i = 0; i < 10; i++) {
			A test = new A();
			orm.save(test);
		}
	}
}
