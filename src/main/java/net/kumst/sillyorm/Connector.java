package net.kumst.sillyorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import net.kumst.sillyorm.configuration.IConfiguration;

/**
 * Database connector
 * 
 * @author Martin Kumst
 */
public class Connector {
	private HashMap<IConfiguration, Connection> jConnections = new HashMap<>();
	private static final Connector CONNECTOR = new Connector();
	
	/**
	 * Constructs the connector
	 */
	private Connector() {
		
	}
	
	/**
	 * Returns the connector instance
	 * 
	 * @return The connector instance
	 */
	public static Connector getInstance() {
		return CONNECTOR;
	}
	
	/**
	 * Connects to the database by the given configuration or use previously created connection
	 * 
	 * @param configuration The configuration
	 * @return The connection
	 * @throws SQLException
	 */
	public Connection getConnection(IConfiguration configuration) throws SQLException {
		if (!jConnections.containsKey(configuration)) {
			jConnections.put(configuration, createNewConnection(configuration));
		}
		return jConnections.get(configuration);
	}
	
	/**
	 * Creates new db connection
	 * 
	 * @param configuration The configuration
	 * @return The db connection
	 * @throws SQLException
	 */
	private Connection createNewConnection(IConfiguration configuration) throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + configuration.getDBFile());
	}
}
