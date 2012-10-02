/*
Copyright (C) 2011 by Joe Graham

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package gov.hhs.fha.nhinc.directbox.client;

import com.google.gwt.user.client.Window;

public class ErrorHandler {

    public static String getHtmlThreadStackTrace(String header, Throwable e) {
        StringBuffer out = new StringBuffer(header);
        out.append("<br>\n");
        out.append("**********************************************<br>\n");
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement element : stack) {
            out.append(element.toString());
            out.append("<br>\n");
        }
        out.append("**********************************************<br>\n");
        return out.toString();
    }

    public static String getThreadStackTrace(String header, Throwable e) {
        StringBuffer out = new StringBuffer(header);
        out.append("\n");
        out.append("**********************************************\n");
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement element : stack) {
            out.append(element.toString());
            out.append("\n");
        }
        out.append("**********************************************\n");
        return out.toString();
    }

    public static void showError(Throwable exception) {
        showError((String) null, exception);
    }

    public static void showError(String aMessage, Throwable exception) {
        String msg = "";

        if (aMessage != null) {
            msg = aMessage + ". ";
        }

        if (exception != null) {
            if (exception.getMessage() != null) {
                msg = "Error: " + exception.getMessage();
            }
            else {
                msg = "Error: " + exception.getClass().getName();
            }
        }
        String error = getThreadStackTrace(msg, exception);
        Window.alert(error);
    }
}
