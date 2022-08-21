package com.logHelper.loghelper;


import com.logHelper.annotation.PrintLog;

import java.util.Arrays;
import java.util.List;


class PrintLogHandlerTest {

	public static void main(String[] args) {
		test("S", Arrays.asList(new String[]{""}));
	}

	@PrintLog
	static String test(String s, List<String > stringList){
		return "13r";
	}
}
