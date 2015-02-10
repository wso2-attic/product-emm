/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package cdm.api.windows.wstep.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Common file operations such as read, write PEM files and .zip file creation
 * are handled by this class. These methods are added to improve reusability of
 * commonly used file operations.
 */
public class FileOperator {

	private static final Log LOG = LogFactory.getLog(FileOperator.class);

	/**
	 * Copy file from the source path to a destination.
	 *
	 * @param source      source file path
	 * @param destination destination file path
	 * @throws ApkGenerationException
	 */
	public static void copyFile(String source, String destination)
			throws ApkGenerationException {
		try {
			FileUtils.copyFile(new File(source), new File(destination));
		} catch (IOException e) {
			String message =
					"Cannot find one of the files, while trying to copy file :" + source +
					",  to its destination: " + destination;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Read a file and returns its content as a {@link String}
	 *
	 * @param path of the file to be read.
	 * @return the content of the file
	 * @throws ApkGenerationException
	 */
	public static String readFile(String path) throws ApkGenerationException {
		try {
			return FileUtils.fileRead(new File(path));
		} catch (IOException e) {
			String message = "Error reading file " + path;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Write content to a physical file
	 *
	 * @param path    the destination file path
	 * @param content data to be saved
	 * @throws ApkGenerationException
	 */
	public static void fileWrite(String path, String content) throws ApkGenerationException {
		try {
			FileUtils.fileWrite(path, content);
		} catch (IOException e) {
			String message = "Error writing to file " + path;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Creates a zip file from a list of files provided.
	 *
	 * @param zipFilePath the path of the final zip file to be created.
	 * @param files       An array of file paths that needs to be added to the zip
	 * @throws ApkGenerationException
	 */
	public static void createZip(String zipFilePath, String[] files)
			throws ApkGenerationException {
		FileOutputStream fileOut;
		File fileToCopy;
		ZipOutputStream zipOutStream = null;
		FileInputStream inputStream = null;
		byte[] bytes = new byte[1024];
		int count;
		int x = 0;

		try {
			fileOut = new FileOutputStream(zipFilePath);
		} catch (FileNotFoundException e) {
			String message = "Error opening file " + zipFilePath;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}

		try {
			zipOutStream = new ZipOutputStream(fileOut);
			for (x = 0; x < files.length; x++) {
				fileToCopy = new File(files[x]);
				inputStream = new FileInputStream(files[x]);
				zipOutStream.putNextEntry(new ZipEntry(fileToCopy.getName()));

				while ((count = inputStream.read(bytes)) > 0) {
					zipOutStream.write(bytes, 0, count);
				}
			}
		} catch (FileNotFoundException e) {
			String message = "Cannot open the file ," + files[x] + " to add to zip.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (IOException e) {
			String message = "Cannot write file ," + files[x] + " to zip.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					String message = "File error while closing the file, " + files[x];
					LOG.warn(message, e);
				}
			}
			if (zipOutStream != null) {
				try {
					zipOutStream.close();
				} catch (IOException e) {
					String message = "File error while closing the file, " + zipFilePath;
					LOG.warn(message, e);
				}
			}
		}
	}

	/**
	 * Get a file input stream when the file name is provided.
	 *
	 * @param sourceFile Name of the source file.
	 * @return the file input stream.
	 * @throws ApkGenerationException
	 */
	public static FileInputStream getFileInputStream(String sourceFile)
			throws ApkGenerationException {
		try {
			return new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			String message = "Cannot open the file ," + sourceFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

	/**
	 * Generates a new folder if it doesn't exist when the path is given.
	 *
	 * @param path the folder path that needs to be created
	 * @throws ApkGenerationException
	 */
	public static void makeFolder(String path) throws ApkGenerationException {
		try {
			File file=new File(path);
			if(!file.mkdirs()){
				String message = "Error when creating directory " + path;
				LOG.error(message);
				throw new ApkGenerationException(message);
			}
		} catch (SecurityException e) {
			String message = "Error when creating directory " + path;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}
}
