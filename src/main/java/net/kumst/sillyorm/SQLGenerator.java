package net.kumst.sillyorm;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLGenerator for SillyORM
 * 
 * @author Martin Kumst
 */
public class SQLGenerator {

	private Reflection jReflector;
	
	/**
	 * Constructs SQLGenerator
	 * 
	 * @param reflector The SillyORM reflection
	 */
	public SQLGenerator(Reflection reflector) {
		if (reflector == null) {
			throw new IllegalArgumentException("Invalid reflector");
		}
		jReflector = reflector;
	}
	
	/**
	 * Generates SQL command to create a new table
	 * 
	 * @return The SQL command
	 */
	public String generateCreateTable() {
		StringBuilder result = new StringBuilder();
		result.append("CREATE TABLE IF NOT EXISTS ");
		result.append(jReflector.getEntityName());
		result.append(" (id integer primary key autoincrement");
		for (String var : jReflector.getAllVariables()) {
			if (var.toLowerCase().equals("id")) {
				continue;
			}
			result.append(", ");
			result.append(var);
			result.append(" text");
		}
		result.append(");");
		return result.toString();
	}
	
	/**
	 * Generates SQL command to insert the entity
	 * 
	 * @return SQL
	 */
	public String generateInsert() {
		StringBuilder result = new StringBuilder();
		result.append("INSERT INTO ");
		result.append(jReflector.getEntityName());
		result.append(" (id, ");
		
		List<String> vars = jReflector.getAllVariables();
		result.append(String.join(", ", vars));
		result.append(") VALUES (null, ");
		
		List<String> qs = new ArrayList<>();
		for (String var : vars) {
			qs.add("?");
		}
		result.append(String.join(", ", qs));
		result.append(");");
		return result.toString();
	}
	
	/**
	 * Generates SQL command to update the entity
	 * 
	 * @return SQL
	 */
	public String generateUpdate() {
		StringBuilder result = new StringBuilder();
		result.append("UPDATE ");
		result.append(jReflector.getEntityName());
		result.append(" SET ");
		
		List<String> sets = new ArrayList<>();
		for (String var : jReflector.getAllVariables()) {
			sets.add(var + " = ?");
		}
		result.append(String.join(", ", sets));
		
		result.append(" WHERE id = ?;");
		return result.toString();
	}
	
	/**
	 * Generates SQL command to select entity by its id
	 * 
	 * @return SQL 
	 */
	public String generateSelect() {
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM ");
		result.append(jReflector.getEntityName());
		result.append(" WHERE id = ?;");
		return result.toString();
	}
	
	/**
	 * Generates SQL command to select entity
	 * 
	 * @return SQL
	 */
	public String generateSelect(List<Criteria> criterias) {
		criterias = addOperators(criterias);
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM ");
		result.append(jReflector.getEntityName());
		result.append(" WHERE ");
		
		List<String> conds = new ArrayList<>();
		for (Criteria c : criterias) {
			StringBuilder cond = new StringBuilder();
			if (!c.isOperator()) {
				cond.append(c.getVariableName());
				cond.append(" ");
				cond.append(c.getOperator().toString());
				cond.append(" ?");
			}
			else {
				cond.append(c.toString());
			}
			conds.add(cond.toString());
		}

		return result.toString() + String.join("", conds);
	}
	
	/**
	 * Adds AND operator between each pair of Criterias in the given list if there is no operator
	 * 
	 * @param criterias The list of Criterias
	 * @return The list of Criterias with added operators
	 */
	private List<Criteria> addOperators(List<Criteria> criterias) {
		List<Criteria> result = new ArrayList<>();
		for (int i = 0; i < criterias.size(); i++) {
			Criteria criteria = criterias.get(i);
			result.add(criteria);
			Criteria sibling = null;
			if (i < (criterias.size() - 1)) {
				sibling = criterias.get(i + 1);
			}
			if (sibling != null && !sibling.isOperator() && !criteria.isOperator()) {
				result.add(new Criteria(Criteria.Operator.AND));
			}
		}
		return result;
	}
	
	/**
	 * Generates SQL command to delete entity by its id
	 * 
	 * @return SQL
	 */
	public String generateDelete() {
		StringBuilder result = new StringBuilder();
		result.append("DELETE FROM ");
		result.append(jReflector.getEntityName());
		result.append(" WHERE id = ?;");
		return result.toString();
	}
}
