package net.kumst.sillyorm;

/**
 * Search criteria or operator
 * 
 * @author Martin Kumst
 */
public class Criteria {
	public enum Operator {
		EQUAL,
		NOT_EQUAL,
		GREATER,
		LOWER,
		GREATER_OR_EQUAL,
		LOWER_OR_EQUAL, 
		AND, 
		OR;
		
		public String toString() {
			switch (this) {
				case EQUAL:
					return "=";
				case NOT_EQUAL:
					return "!=";
				case GREATER:
					return ">";
				case LOWER:
					return "<";
				case GREATER_OR_EQUAL:
					return ">=";
				case LOWER_OR_EQUAL:
					return "<=";
				case AND:
					return "AND";
				case OR:
					return "OR";
			}
			return null;
		}
	};
	
	private Class<?> jEntityClass;
	private String jVariableName;
	private Operator jOperator;
	private String jVariableValue;
	
	
	/**
	 * Constructs search criteria
	 * 
	 * @param entityClass The entity class
	 * @param variableName The variable name
	 * @param operator The operator
	 * @param variableValue The value
	 */
	public Criteria(Class<?> entityClass, String variableName, Operator operator, String variableValue) {
		jEntityClass = entityClass;
		jVariableName = variableName.toLowerCase();
		jOperator = operator;
		jVariableValue = variableValue;
		validateVariableName();
	}
	
	/**
	 * Construct search operator
	 * 
	 * @param operator The operator
	 */
	public Criteria(Operator operator) {
		jOperator = operator;
	}

	public String getVariableName() {
		return jVariableName;
	}

	public void setVariableName(String variableName) {
		jVariableName = variableName;
	}

	public Operator getOperator() {
		return jOperator;
	}

	public void setOperator(Operator operator) {
		jOperator = operator;
	}

	public String getVariableValue() {
		return jVariableValue;
	}

	public void setVariableValue(String variableValue) {
		jVariableValue = variableValue;
	}
	
	/**
	 * Validates the jVariableName
	 */
	private void validateVariableName() {
		Reflection reflection = new Reflection(jEntityClass);
		if (!reflection.getAllVariables().contains(jVariableName)) {
			throw new IllegalArgumentException("Invalid variable name");
		}
	}
	
	/**
	 * Checks whether this Criteria is Operator or not
	 * 
	 * @return True if this Criteria is Operator, otherwise false
	 */
	public boolean isOperator() {
		return !(jEntityClass != null && jVariableName != null && jVariableValue != null);
	}

	@Override
	public String toString() {
		StringBuilder cond = new StringBuilder();
		if (!isOperator()) {
			cond.append(jVariableName);
			cond.append(" ");
			cond.append(jOperator.toString());
			cond.append(" ?");
			return cond.toString();
		}
		cond.append(" ");
		cond.append(jOperator.toString());
		cond.append(" ");
		return cond.toString();
	}
}