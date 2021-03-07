package net.kumst.sillyorm.configuration;

/**
 * Configuration of SillyORM
 * 
 * @author Martin Kumst
 */
public class Configuration implements IConfiguration {
	private String jFile;
	
	/**
	 * Constructs configuration
	 * 
	 * @param file DB file
	 */
	public Configuration(String file) {
		jFile = file;
	}
	
	
	@Override
	public String getDBFile() {
		return jFile;
	}
}
