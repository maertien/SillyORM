package net.kumst.sillyorm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kumst.sillyorm.configuration.IConfiguration;

/**
 * SillyORM
 * 
 * @author Martin Kumst
 */
public class ORM {
	
	private IConfiguration jConfiguration;
	
	/**
	 * Constructs ORM
	 * 
	 * @param configuration The DB configuration
	 */
	public ORM(IConfiguration configuration) {
		jConfiguration = configuration;
	}
	
	/**
	 * Generates table in database for the given entity class
	 * 
	 * @param entityClass The entity class
	 */
	public void prepareTableFor(Class<?> entityClass) {
		SQLGenerator gen = new SQLGenerator(new Reflection(entityClass));
		String sql = gen.generateCreateTable();
		try {
			PreparedStatement statement = Connector.getInstance().getConnection(jConfiguration).prepareStatement(sql);
			statement.executeUpdate();
		}
		catch (Exception e) {
			// We do not care, we are silly
		}
	}
	
	/**
	 * Returns the object with the given id
	 * 
	 * @param entityClass The entity class
	 * @param id The id of object 
	 * @return The object with the given id or null
	 */
	public <T> T find(Class<T> entityClass, int id) {
		SQLGenerator gen = new SQLGenerator(new Reflection(entityClass));
		String sql = gen.generateSelect();
		List<T> result = find(entityClass, sql, id);
		if (result.size() == 1) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Returns the list of objects fulfill given criterias
	 * 
	 * @param <T> The type of entity
	 * @param entityClass The entity class
	 * @param criterias Criterias to meet
	 * @return The list of objects
	 */
	public <T> List<T> find(Class<T> entityClass, Criteria ... criterias) {
		SQLGenerator gen = new SQLGenerator(new Reflection(entityClass));
		String sql = gen.generateSelect(Arrays.asList(criterias));
		
		List<Object> args = new ArrayList<>();
		for (Criteria c : criterias) {
			if (!c.isOperator()) {
				args.add(c.getVariableValue());
			}
		}
		
		return find(entityClass, sql, args.toArray()); 
	}
	
	/**
	 * Returns the of objects got by sql command
	 * 
	 * @param <T> The type of entity
	 * @param entityClass The entity class
	 * @param sql The SQL command to select the objects
	 * @param args The arguments bound to DB statement
	 * @return The list of objects
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, Object ... args) {
		try {
			PreparedStatement statement = Connector.getInstance().getConnection(jConfiguration).prepareStatement(sql);
			
			int i = 1;
			for (Object arg : args) {
				if (arg instanceof Integer) {
					statement.setInt(i, (int) arg);
				}
				else {
					statement.setString(i, (String) arg);
				}
				i++;
			}
			
			ResultSet rs = statement.executeQuery();
			List<T> result = new ArrayList<>();
			while (rs.next()) {
				T obj = entityClass.getDeclaredConstructor().newInstance();
				Reflection reflector = new Reflection(obj);
				for (String varName : reflector.getAllVariables()) {
					reflector.setValue(varName, rs.getString(varName));
				}
				reflector.setValue("id", rs.getString("id"));
				result.add(obj);
			}
			return result;
		}
		catch (Exception e) {
			// We do not care, we are silly
		}
		return null;
	}
	
	/**
	 * Saves the given entity
	 * 
	 * @param entity The entity to be saved
	 */
	public void save(Object entity) {
		Reflection reflection = new Reflection(entity);
		SQLGenerator gen = new SQLGenerator(reflection);
		String sql = null;
		boolean update = false;
		String id = reflection.getValue("id");
		if (id == null) {
			sql = gen.generateInsert();
		}
		else {
			sql = gen.generateUpdate();
			update = true;
		}
		if (sql != null) {
			try {
				PreparedStatement statement = Connector.getInstance().getConnection(jConfiguration).prepareStatement(sql);
				int n = 1;
				for (String varName : reflection.getAllVariables()) {
					statement.setString(n, reflection.getValue(varName));
					n++;
				}
				if (update) {
					statement.setInt(n, Integer.parseInt(id));
				}
				statement.executeUpdate();
				if (!update) {
					ResultSet keys = statement.getGeneratedKeys();
					if (keys.next()) {
						String newId = keys.getString(1);
						reflection.setValue("id", newId);
					}
				}
			} catch (Exception e) {
				// We do not care, we are silly
				System.out.println(e);
			}
		}
	}
	
	/**
	 * Deletes the given entity
	 * 
	 * @param entity The entity to be deleted
	 */
	public void delete(Object entity) {
		Reflection reflection = new Reflection(entity);
		SQLGenerator gen = new SQLGenerator(reflection);
		String id = reflection.getValue("id");
		if (id != null) {
			String sql = gen.generateDelete();
			try {
				PreparedStatement statement = Connector.getInstance().getConnection(jConfiguration).prepareStatement(sql);
				statement.setInt(1, Integer.parseInt(id));
				statement.executeUpdate();
			}
			catch (Exception e) {
				// We do not care, we are silly
			}
		}
	}
}
