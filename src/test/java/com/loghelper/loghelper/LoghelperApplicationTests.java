package com.loghelper.loghelper;


import com.loghelper.annotation.PrintLog;
import org.junit.Test;
import org.junit.runner.RunWith;

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
