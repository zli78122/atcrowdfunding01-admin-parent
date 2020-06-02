package com.atguigu.crowd.test;

import org.junit.Test;

import com.atguigu.crowd.util.CrowdUtil;

public class SpringTest {

	@Test
	public void testMd5() {
		String source = "123456";
		String encoded = CrowdUtil.md5(source);
		System.out.println(encoded);
	}
}
