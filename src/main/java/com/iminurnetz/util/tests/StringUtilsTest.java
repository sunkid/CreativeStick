package com.iminurnetz.util.tests;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.iminurnetz.util.StringUtils;

public class StringUtilsTest extends TestCase {
	public void testConstantCaseToEnglish() {
		assertEquals("basic", StringUtils.constantCaseToEnglish("BASIC"));
		assertEquals("two words", StringUtils.constantCaseToEnglish("TWO_WORDS"));
	}

	public void testToConstantCase() {
		assertEquals("BASIC", StringUtils.toConstantCase("bASic"));
		assertEquals("TWO_WORDS", StringUtils.toConstantCase("Two words"));
		assertEquals("THIS_IS_A_LONGER_ONE", StringUtils.toConstantCase("this is a LoNgEr One"));
	}

	public void testClosestMatch() {
		List<String> candidates = Arrays.asList("exact", "exacting", "similar", "similac", "relax", "relevant interests", "relevant ideas");
		assertEquals(Arrays.asList("similar"), StringUtils.closestMatch("similar", candidates));
		assertEquals(Arrays.asList("exact"), StringUtils.closestMatch("exact", candidates));
		assertEquals(Arrays.asList("relax", "relevant interests", "relevant ideas"), StringUtils.closestMatch("r", candidates));
		assertEquals(Arrays.asList("exact", "exacting"), StringUtils.closestMatch("E", candidates));
		assertEquals(Arrays.asList("exacting"), StringUtils.closestMatch("E*ing", candidates));
		assertEquals(Arrays.asList("relevant interests", "relevant ideas"), StringUtils.closestMatch("RI", candidates));
		assertEquals(Arrays.asList("relevant interests", "relevant ideas"), StringUtils.closestMatch("RI*s", candidates));
		assertEquals(Arrays.asList("relevant interests"), StringUtils.closestMatch("RI*ts", candidates));		
		assertEquals(Arrays.asList("relevant interests"), StringUtils.closestMatch("R*ts", candidates));		
	}
}
