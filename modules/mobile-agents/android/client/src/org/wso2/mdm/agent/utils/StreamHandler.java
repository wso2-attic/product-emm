package org.wso2.mdm.agent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * This class handles the closure of all the stream types.
 */
public class StreamHandler {

	/**
	 * Close a ByteArrayOutputStream passed in.
	 * 
	 * @param stream
	 *            - ByteArrayOutputStream to be closed.
	 */
	public static void closeOutputStream(OutputStream stream, String tag) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(tag, "Exception occured when closing ByteArrayOutputStream." + e);
			}
		}
	}

	/**
	 * Close a InputStream passed in.
	 * 
	 * @param stream
	 *            - InputStream to be closed.
	 */
	public static void closeInputStream(InputStream stream, String tag) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(tag, "Exception occured when closing InputStream." + e);
			}
		}
	}

	/**
	 * Close a InputStream passed in.
	 * 
	 * @param stream
	 *            - InputStream to be closed.
	 */
	public static void closeBufferedReader(BufferedReader stream, String tag) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(tag, "Exception occured when closing BufferedReader." + e);
			}
		}
	}

}
