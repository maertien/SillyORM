package net.kumst.sillyorm.configuration;

/**
 * IConfiguration interface
 * 
 * @author Martin Kumst
 */
public interface IConfiguration {
	
	/**
	 * Returns the path to the db file
	 * 
	 * @return The database file path
	 */
	public String getDBFile();
	
	/**
	 * Default implementation of exception handler
	 * 
	 * @param e The exception to be handled
	 */
	public default void handleError(Exception e) {
		e.printStackTrace();
	}
}
