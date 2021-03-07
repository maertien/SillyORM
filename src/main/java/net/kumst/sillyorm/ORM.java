package net.kumst.sillyorm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	public Object find(Class<?> entityClass, int id) {
		SQLGenerator gen = new SQLGenerator(new Reflection(entityClass));
		String sql = gen.generateSelect();
		try {
			PreparedStatement statement = Connector.getInstance().getConnection(jConfiguration).prepareStatement(sql);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				Object result = entityClass.getDeclaredConstructor().newInstance();
				Reflection reflector = new Reflection(result);
				for (String varName : reflector.getAllVariables()) {
					reflector.setValue(varName, rs.getString(varName));
				}
				reflector.setValue("id", rs.getString("id"));
				return result;
			}
		}
		catch (Exception e) {
			// We do not care, we are silly
		}
		return null;
	}
	
	public List<Object> find(Class<?> entityClass, Criteria ... criterias) {
		SQLGenerator gen = new SQLGenerator(new Reflection(entityClass));
		gen.generateSelect(Arrays.asList(criterias));
		
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
