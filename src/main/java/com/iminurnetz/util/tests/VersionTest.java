package com.iminurnetz.util.tests;

import com.iminurnetz.util.Version;

import junit.framework.TestCase;

public class VersionTest extends TestCase {
	public void testVersion() {
		assertNotNull(getClass().getResource("version.txt"));
		Version v = new Version(getClass().getResource("version.txt"));
		assertEquals("VersionTest", v.getProject());
	}
}
