package net.kumst.sillyorm.configuration;

/**
 * The configuration of in-memory database
 * 
 * @author Martin Kumst
 */
public class MemConfiguration implements IConfiguration {

	@Override
	public String getDBFile() {
		return ":memory:";
	}
}
