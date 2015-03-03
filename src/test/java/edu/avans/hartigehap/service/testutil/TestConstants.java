package edu.avans.hartigehap.service.testutil;

public final class TestConstants {

	public static final String TEST_DATASOURCE = "/datasource-tx-jpa.xml";
	public static final String SPRING_ROOT_CONTEXT = "/root-context.xml";
	public static final String TX_MANAGER_NAME = "transactionManager";

	/** defeat instantiation. */
	private TestConstants() {
		// empty
	}

}
