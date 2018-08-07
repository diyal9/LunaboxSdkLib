package com.lunabox.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GlobalsUtil {
	public static String getStackTrace(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}
}
