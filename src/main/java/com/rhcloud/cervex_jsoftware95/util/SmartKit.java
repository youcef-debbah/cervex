/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

/**
 * This class contain a couple of standard helper static methods that you may
 * need in any java application development.
 * 
 * @since 1.8
 * @author youcef debbah
 */

public class SmartKit {

	private static final String EOF = System.getProperty("file.separator");

	/**
	 * Returns a string that is consist of white spaces with length of
	 * {@code length}
	 * 
	 * @param length
	 *            The length of the returned string
	 * @return A string consist of white spaces and its length is {@code length}
	 */

	public static String fill(int length) {
		return fillBy(length, " ");
	}

	/**
	 * Returns a string formed by repeating the {@code token} until
	 * {@code length} is reached.
	 * 
	 * @param length
	 *            The length of the returned string
	 * @param token
	 *            The string to be repeated, if it is {@code null} an empty
	 *            string will be returned
	 * @return A string consist of repeated {@code token} and its length is
	 *         {@code length}
	 * @throws IllegalArgumentException
	 *             If {@code length} is less than zero
	 * 
	 */

	public static String fillBy(int length, String token) {
		if (length < 0)
			throw new IllegalArgumentException("for value:" + length);
		else if (length == 0 || token == null)
			return "";
		else {
			StringBuilder builder = new StringBuilder();
			while (builder.length() < length)
				builder.append(token);

			return builder.substring(0, length);
		}
	}

	/**
	 * Prints a string that represent this object using the default style to
	 * {@code System.out} stream. see {@link #toString(Object, Style)} for more
	 * information.
	 * 
	 * @param obj
	 *            Object to print
	 */

	public static void println(Object obj) {
		System.out.println(toString(obj));
	}

	/**
	 * Prints a string that represent this object using {@code style} to
	 * {@code System.out} stream.
	 * 
	 * @param obj
	 *            Object to print
	 * @param style
	 * @return A string representation of the object
	 * @throws NullPointerException
	 *             If {@code obj} is {@code null}
	 */

	public static void println(Object obj, Style style) {
		System.out.println(toString(obj, style));
	}

	/**
	 * Returns a string that represent this object using the default style. see
	 * {@link #toString(Object, Style)} for more information.
	 * 
	 * @param obj
	 *            Object to work on
	 * @return A string representation of the object
	 */

	public static String toString(Object obj) {
		return toString(obj, new Style());
	}

	/**
	 * Returns a string that represent this object using the given
	 * {@code style}.
	 * 
	 * @param obj
	 *            Object to work on
	 * @param style
	 * @return A string representation of the object
	 * @throws NullPointerException
	 *             If {@code obj} is {@code null}
	 */

	public static String toString(Object obj, Style style) {
		Objects.requireNonNull(style, "style must not be null");

		if (obj == null)
			return "null";
		else if (obj.getClass().isArray())
			return arrayToString((Object[]) obj, style);
		else if (obj instanceof Iterable)
			return iterableToString((Iterable<?>) obj, style);
		else if (obj instanceof Map)
			return mapToString((Map<?, ?>) obj, style);
		else if (obj instanceof Enumeration<?>)
			return enumerationToString((Enumeration<?>) obj, style);
		else
			return obj.toString();

	}

	public static String strace(Object obj) {
		String string = toString(obj, new Style(Style.WellDefined.TRACE));
		System.err.println(string);
		return string;
	}

	/**
	 * Give a string representation of the array.
	 */

	private static String arrayToString(Object[] array, Style style) {
		assert array != null;
		StringBuilder builder = new StringBuilder();
		if (style.isLabeled())
			builder.append(array.getClass().getSimpleName() + " ");

		String arrayPrefix = style.getAttribute(Style.Type.ARRAY, Style.Token.PREFIX);
		String arraySeparator = style.getAttribute(Style.Type.ARRAY, Style.Token.SEPARATOR);
		String arraySuffix = style.getAttribute(Style.Type.ARRAY, Style.Token.SUFFIX);

		if (arrayPrefix != null)
			builder.append(arrayPrefix);

		if (array.length > 0)
			builder.append(toString(array[0], style));

		for (int i = 1; i < array.length; i++) {
			if (arraySeparator != null)
				builder.append(arraySeparator);
			builder.append(toString(array[i], style));
		}

		if (arraySuffix != null)
			builder.append(arraySuffix);

		return builder.toString();
	}

	/**
	 * Give a string representation of the object.
	 */

	private static String iterableToString(Iterable<?> iterable, Style style) {
		assert iterable != null;
		StringBuilder builder = new StringBuilder();
		if (style.isLabeled())
			builder.append(iterable.getClass().getSimpleName());

		String iterablePrefix = style.getAttribute(Style.Type.ITERABLE, Style.Token.PREFIX);
		String iterableSeparator = style.getAttribute(Style.Type.ITERABLE, Style.Token.SEPARATOR);
		String iterableSuffix = style.getAttribute(Style.Type.ITERABLE, Style.Token.SUFFIX);

		if (iterablePrefix != null)
			builder.append(iterablePrefix);

		int i = 0;
		for (Object object : iterable) {
			if (i > 0 && iterableSeparator != null)
				builder.append(iterableSeparator);
			builder.append(toString(object, style));
			i++;
		}

		if (iterableSuffix != null)
			builder.append(iterableSuffix);

		return builder.toString();
	}

	/**
	 * Give a string representation of the object.
	 */

	private static String mapToString(Map<?, ?> map, Style style) {
		assert map != null;
		StringBuilder builder = new StringBuilder();
		if (style.isLabeled())
			builder.append(map.getClass().getSimpleName());

		String mapPrefix = style.getAttribute(Style.Type.MAP, Style.Token.PREFIX);
		String mapSeparator = style.getAttribute(Style.Type.MAP, Style.Token.SEPARATOR);
		String mapSuffix = style.getAttribute(Style.Type.MAP, Style.Token.SUFFIX);

		if (mapPrefix != null)
			builder.append(mapPrefix);

		int i = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			if (i > 0 && mapSeparator != null)
				builder.append(mapSeparator);
			builder.append(toString(entry.getKey(), style));

			if (style.getMapkeyValueSeparator() != null)
				builder.append(style.getMapkeyValueSeparator());

			builder.append(toString(entry.getValue(), style));
			i++;
		}

		if (mapSuffix != null)
			builder.append(mapSuffix);

		return builder.toString();
	}

	/**
	 * Give a string representation of the object.
	 */

	private static String enumerationToString(Enumeration<?> enumeration, Style style) {
		assert enumeration != null;
		StringBuilder builder = new StringBuilder();
		if (style.isLabeled())
			builder.append(enumeration.getClass().getSimpleName());

		String enumerationPrefix = style.getAttribute(Style.Type.ENUMERATION, Style.Token.PREFIX);
		String enumerationSeparator = style.getAttribute(Style.Type.ENUMERATION, Style.Token.SEPARATOR);
		String enumerationSuffix = style.getAttribute(Style.Type.ENUMERATION, Style.Token.SUFFIX);

		if (enumerationPrefix != null)
			builder.append(enumerationPrefix);

		int i = 0;
		while (enumeration.hasMoreElements()) {
			if (i > 0 && enumerationSeparator != null)
				builder.append(enumerationSeparator);

			builder.append(toString(enumeration.nextElement(), style));
			i++;
		}

		if (enumerationSuffix != null)
			builder.append(enumerationSuffix);

		return builder.toString();
	}

	/**
	 * Removes all pairs from this map that has:
	 * <ul>
	 * <li>null key</li>
	 * <li>null value</li>
	 * <li>key with empty representation string
	 * (<code>key.toString().isEmpty()</code> returns true)</li>
	 * <li>value with empty representation string
	 * (<code>vlaue.toString().isEmpty()</code> returns true)</li>
	 * </ul>
	 * 
	 * @param map
	 *            The map to be cleared
	 */

	public static void clearMap(Map<?, ?> map) {
		ArrayList<Object> toBeDeletedKeys = new ArrayList<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key == null || value == null || key.toString().isEmpty() || value.toString().isEmpty())
				toBeDeletedKeys.add(key);
		}

		for (Object key : toBeDeletedKeys) {
			map.remove(key);
		}
	}

	/**
	 * Loads any Images (GIF, JPEG or PNG) files in {@code directory} and
	 * satisfy {@code selector} {@link Predicate#test(Object) test}.
	 * <p>
	 * The {@code directory} is a '/'-separated path name that identifies the
	 * directory.
	 * 
	 * @param directory
	 *            The path of the folder that contain images to be leaded
	 * @param selector
	 *            The predicate that will filter image files names, null value
	 *            cause all image files in {@code directory} to be loaded
	 * @return Image files (GIF, JPEG or PNG) that has been successfully loaded
	 *         from {@code directory}, this value is guaranteed to be not
	 *         {@code null} (in worst case an empty list will be returned)
	 * @throws NullPointerException
	 *             if {@code directory} is {@code null}
	 */

	public static List<Image> loadImages(String directory, Predicate<String> selector) {
		Objects.requireNonNull(directory);
		String directoryPath = directory.endsWith("/") ? directory : directory + "/";

		Predicate<String> predicate = (selector == null) ? file -> true : selector;
		ArrayList<Image> images = new ArrayList<>();

		try (Stream<String> filePaths = getFilesIn(directoryPath)) {

			filePaths.forEach(fileName -> {
				try {
					if (predicate.test(fileName)) {
						Image image = ImageIO.read(ClassLoader.getSystemResource(directoryPath + fileName));
						if (image != null)
							images.add(image);
					}
				} catch (IOException | RuntimeException e) {
					System.err.println("could not read file: " + fileName);
				}
			});
		}

		return images;
	}

	/**
	 * Returns a stream of strings naming the files and directories in the
	 * directory denoted by {@code directory}.
	 * <p>
	 * The {@code directory} is a '/'-separated path name that identifies the
	 * directory.
	 * <p>
	 * The main advantage of this method over {@link File#listFiles()} is that
	 * it treat files with "jar" {@link URI#getScheme() scheme} properly, in
	 * other word this method behave in the same way even if {@code directory}
	 * refer to a directory that is packaged inside a jar file.
	 * <p>
	 * When working with jar packaged files this method does not decode any
	 * resources by itself instead it's delegate the dirty IO work to
	 * {@link FileSystem#getPath(String, String...)}, because of that you should
	 * call {@link Stream#close()} after using the returned stream to ensure
	 * that the underlying {@code FileSystem} (and any other closable resources)
	 * will be properly closed.
	 * 
	 * @param directory
	 *            The directory to be searched in
	 * @return the name of files and directories in {@code directory}, it is
	 *         guaranteed that this value is not {@code null} (in worst case it
	 *         will be an empty stream)
	 * @throws NullPointerException
	 *             if {@code directory} is null
	 */

	public static Stream<String> getFilesIn(String directory) {
		Objects.requireNonNull(directory);

		try {
			URI uri = ClassLoader.getSystemResource(directory).toURI();

			if ("jar".equals(uri.getScheme())) {
				FileSystem fileSystem = getFileSystem(uri);
				return getFilesFrom(fileSystem.getPath(directory)).onClose(() -> {
					if (fileSystem != null) {
						try {
							fileSystem.close();
						} catch (IOException e) {
							System.err.println("could not close a fileSystem");
						}
					}
				});
			} else {
				return getFilesFrom(Paths.get(uri));
			}

		} catch (RuntimeException | URISyntaxException | IOException e) {
			System.err.println("could not read files from: " + directory);
			return Stream.empty();
		}
	}

	/**
	 * Returns a stream of strings naming the files and directories in the
	 * directory denoted by {@code directory}.
	 * <p>
	 * The {@code directory} is a '/'-separated path name that identifies the
	 * directory.
	 * 
	 * @param directory
	 *            The directory to be searched in
	 * @return the name of files and directories in {@code directory}
	 * @throws SecurityException
	 *             If the security manager denies access to the starting file,
	 *             the checkRead method is invoked to check read access to the
	 *             directory
	 * @throws IOException
	 *             if an I/O error is thrown when accessing the starting file
	 * 
	 */

	public static Stream<String> getFilesFrom(Path directory) throws IOException {
		Stream<String> paths = Files.walk(directory, 1).map(path -> {
			try {
				if (path.equals(directory))
					return null;

				return path.getFileName().toString();
			} catch (RuntimeException e) {
				return null;
			}
		}).filter(Objects::nonNull);

		return paths;
	}

	/**
	 * Returns a reference to a FileSystem (constructs a new one if necessary).
	 * This method first try to get reference to an existing FileSystem, if no
	 * reference was not found a new file system will be constructed (with empty
	 * properties map).
	 * 
	 * @param uri
	 *            the URI to locate the file system
	 * @return the reference to the file system
	 * @throws IllegalArgumentException
	 *             if the pre-conditions for the {@code uri} parameter are not
	 *             met
	 * @throws ProviderNotFoundException
	 *             if a provider supporting the URI scheme is not installed
	 * @throws IOException
	 *             if an I/O error occurs creating the file system
	 * @throws SecurityException
	 *             if a security manager is installed and it denies an
	 *             unspecified permission required by the file system provider
	 *             implementation
	 */

	public static FileSystem getFileSystem(URI uri) throws IOException {
		try {
			return FileSystems.getFileSystem(uri);
		} catch (FileSystemNotFoundException e) {
			return FileSystems.newFileSystem(uri, Collections.emptyMap());
		}
	}

	/**
	 * Returns the system-dependent file separator string. It always returns the
	 * same value - the initial value of the {@linkplain #getProperty(String)
	 * system property} {@code path.separator}.
	 *
	 * <p>
	 * On UNIX systems, it returns {@code "/"}; on Microsoft Windows systems it
	 * returns {@code "\"}.
	 *
	 * @return the system-dependent line separator string
	 */

	public static String fileSeparator() {
		return EOF;
	}

	public static void printHeaders(HttpServletRequest req) {
		Enumeration<String> headers = req.getHeaderNames();
		System.out.println("___________ headers ___________");
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			System.out.print("header: " + header + " ---> ");
			System.out.println(SmartKit.toString(req.getHeaders(header)));
		}
		System.out.println("___________ end ___________");
	}

	public static String formatSize(long bytes, String byteSymbol) {
		int unit = 1024;
		if (bytes < unit)
			return bytes + " " + byteSymbol;

		int exp = (int) (Math.log(bytes) / Math.log(unit));
		return String.format("%.2f %s", bytes / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1) + byteSymbol);
	}

}
