package org.yarnandtail.andhow;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.load.StringArgumentLoader;
import org.yarnandtail.andhow.property.*;

/**
 *
 * @author eeverman
 */
public class AndHowUsageExampleTest extends AndHowTestBase {
	
	String uiFullPath = UI_CONFIG.class.getCanonicalName() + ".";
	String svsFullPath = SERVICE_CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgsWFullClassName = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgsWFullClassName = new String[] {
			uiFullPath + "DISPLAY_NAME" + StringArgumentLoader.KVP_DELIMITER + "My App",
			uiFullPath + "BACKGROUP_COLOR" + StringArgumentLoader.KVP_DELIMITER + "ffffff",
			svsFullPath + "REST_ENDPOINT_URL" + StringArgumentLoader.KVP_DELIMITER + "google.com",
			svsFullPath + "RETRY_COUNT" + StringArgumentLoader.KVP_DELIMITER + "4",
			svsFullPath + "TIMEOUT_SECONDS" + StringArgumentLoader.KVP_DELIMITER + "10",
			svsFullPath + "GRAVITY" + StringArgumentLoader.KVP_DELIMITER + "9.8",
			svsFullPath + "PIE" + StringArgumentLoader.KVP_DELIMITER + "3.14"
		};
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		AndHowNonProduction.builder()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.addCmdLineArgs(cmdLineArgsWFullClassName)
				.build();
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertEquals("ffffff", UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("google.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(4), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(10), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
		assertEquals(new Double(9.8), SERVICE_CONFIG.GRAVITY.getValue(), .00000001d);
		assertEquals(new Double(3.14), SERVICE_CONFIG.PIE.getValue(), .00000001d);
	}
	
	@Test
	public void testOptionalValuesAreUnset() {
		AndHowNonProduction.builder()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.addCmdLineArg(uiFullPath + "DISPLAY_NAME", "My App")
				.addCmdLineArg(svsFullPath + "REST_ENDPOINT_URL", "yahoo.com")
				.addCmdLineArg(svsFullPath + "TIMEOUT_SECONDS", "99")
				.build();
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertNull(UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("yahoo.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(3), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(99), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testMissingValuesException() {
		
		try {
			AndHowNonProduction.builder()
					.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
					.build();
			fail();
		} catch (AppFatalException ce) {
			assertEquals(3, ce.getProblems().filter(RequirementProblem.class).size());
			assertEquals(UI_CONFIG.DISPLAY_NAME, ce.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
			assertEquals(SERVICE_CONFIG.REST_ENDPOINT_URL, ce.getProblems().filter(RequirementProblem.class).get(1).getPropertyCoord().getProperty());
			assertEquals(SERVICE_CONFIG.TIMEOUT_SECONDS, ce.getProblems().filter(RequirementProblem.class).get(2).getPropertyCoord().getProperty());
		}
	}
	
	public static interface UI_CONFIG extends PropertyGroup {
		StrProp DISPLAY_NAME = StrProp.builder().mustBeNonNull().build();
		StrProp BACKGROUP_COLOR = StrProp.builder().build();
	}
	
	public static interface SERVICE_CONFIG extends PropertyGroup {
		StrProp REST_ENDPOINT_URL = StrProp.builder().mustBeNonNull().build();
		IntProp RETRY_COUNT = IntProp.builder().defaultValue(3).build();
		IntProp TIMEOUT_SECONDS = IntProp.builder().mustBeNonNull().build();
		DblProp GRAVITY = DblProp.builder().mustBeGreaterThan(9.1d).mustBeLessThan(10.2d).build();
		DblProp PIE = DblProp.builder().mustBeGreaterThanOrEqualTo(3.1).mustBeLessThanOrEqualTo(3.2).build();

	}
	

}