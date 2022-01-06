package net.kumst.sillyorm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.kumst.sillyorm.configuration.MemConfiguration;

/**
 * Tests of ORM
 * 
 * @author Martin Kumst
 */
public class TestORM {
	
	private ORM orm;
	
	@Before
	public void init() {
		orm = new ORM(new MemConfiguration());
		orm.prepareTableFor(TestEntity.class);
	}
	
	@Test
	public void testInsert() {
		TestEntity test = new TestEntity();
		test.setName("Name");
		test.setValue("Value");
		assertNull(test.getId());
		assertEquals("Name", test.getName());
		assertEquals("Value", test.getValue());
		orm.save(test);
		assertEquals("1", test.getId());
		assertEquals("Name", test.getName());
		assertEquals("Value", test.getValue());
		orm.save(test);
		assertEquals("1", test.getId());
		assertEquals("Name", test.getName());
		assertEquals("Value", test.getValue());
		TestEntity storedObject = (TestEntity) orm.find(TestEntity.class, 1);
		assertEquals("1", storedObject.getId());
		assertEquals("Name", storedObject.getName());
		assertEquals("Value", storedObject.getValue());
	}
	
	@Test
	public void testFindEntityById() {
		for (int i = 0; i < 10; i++) {
			TestEntity test = new TestEntity();
			test.setName("Name" + i);
			test.setValue("Value" + i);
			orm.save(test);
			assertEquals(String.valueOf(i + 1), test.getId());
			assertEquals("Name" + i, test.getName());
			assertEquals("Value" + i, test.getValue());
		}
		for (int i = 0; i < 10; i++) {
			TestEntity test = (TestEntity) orm.find(TestEntity.class, i + 1);
			assertEquals(String.valueOf(i + 1), test.getId());
			assertEquals("Name" + i, test.getName());
			assertEquals("Value" + i, test.getValue());
		}
		assertNull(orm.find(TestEntity.class, 112222));
	}
	
	@Test
	public void testFindEntityByCriterias() {
		for (int i = 0; i < 10; i++) {
			TestEntity test = new TestEntity();
			test.setName("Crit" + i);
			test.setValue("Val" + i);
			orm.save(test);
			assertEquals(String.valueOf(i + 1), test.getId());
			assertEquals("Crit" + i, test.getName());
			assertEquals("Val" + i, test.getValue());
		}
		
		List<TestEntity> result = orm.find(TestEntity.class, new Criteria(TestEntity.class, "name", Criteria.Operator.EQUAL, "Crit6"),
				new Criteria(Criteria.Operator.OR),
				new Criteria(TestEntity.class, "name", Criteria.Operator.EQUAL, "Crit7"));
		
		assertEquals(2, result.size());
		assertEquals("Crit6", result.get(0).getName());
		assertEquals("Val6", result.get(0).getValue());
		assertEquals(String.valueOf("7"), result.get(0).getId());
		assertEquals("Crit7", result.get(1).getName());
		assertEquals("Val7", result.get(1).getValue());
		assertEquals(String.valueOf("8"), result.get(1).getId());
	}
	
	@Test
	public void testFindBySQL() {
		for (int i = 0; i < 10; i++) {
			TestEntity test = new TestEntity();
			test.setName("Crit" + i);
			test.setValue("Val" + i);
			orm.save(test);
			assertEquals(String.valueOf(i + 1), test.getId());
			assertEquals("Crit" + i, test.getName());
			assertEquals("Val" + i, test.getValue());
		}
		
		List<TestEntity> result = orm.find(TestEntity.class, "select * from testentity where name = ? or name = ?", "Crit6", "Crit7");
		assertEquals(2, result.size());
		assertEquals("Crit6", result.get(0).getName());
		assertEquals("Val6", result.get(0).getValue());
		assertEquals(String.valueOf("7"), result.get(0).getId());
		assertEquals("Crit7", result.get(1).getName());
		assertEquals("Val7", result.get(1).getValue());
		assertEquals(String.valueOf("8"), result.get(1).getId());
	}
	
	@Test
	public void testUpdateEntity() {
		TestEntity test = new TestEntity();
		test.setName("Martin");
		test.setValue("Value");
		assertNull(test.getId());
		orm.save(test);
		assertEquals(String.valueOf(1), test.getId());
		test.setName("NewName");
		test.setValue("NewValue");
		TestEntity stored = (TestEntity) orm.find(TestEntity.class, 1);
		assertEquals(String.valueOf(1), stored.getId());
		assertEquals("Martin", stored.getName());
		assertEquals("Value", stored.getValue());
		orm.save(test);
		TestEntity updated = (TestEntity) orm.find(TestEntity.class, 1);
		assertEquals(String.valueOf(1), updated.getId());
		assertEquals("NewName", updated.getName());
		assertEquals("NewValue", updated.getValue());
	}
	
	@Test
	public void testDeleteEntity() {
		TestEntity test = new TestEntity();
		test.setName("Name");
		test.setValue("Value");
		orm.save(test);
		assertEquals(test.getId(), "1");
		TestEntity stored = (TestEntity) orm.find(TestEntity.class, 1);
		assertEquals("1", stored.getId());
		assertEquals("Name", stored.getName());
		assertEquals("Value", stored.getValue());
		orm.delete(stored);
		assertNull(orm.find(TestEntity.class, 1));
	}
}
