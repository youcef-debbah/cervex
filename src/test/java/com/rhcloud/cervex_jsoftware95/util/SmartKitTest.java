package com.rhcloud.cervex_jsoftware95.util;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmartKitTest {

    private SmartKit smartKit;

    @Before
    public void setUp() throws Exception {
        smartKit = new SmartKit();
    }

    @Test
    public void testFill_positiveLength() {
        String result = smartKit.fill(5);
        assertEquals("     ", result);
    }

    @Test
    public void testFill_zeroLength() {
        String result = smartKit.fill(0);
        assertEquals("", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFill_negativeLength() {
        smartKit.fill(-1);
    }

    @Test
    public void testFillBy_positiveLength() {
        String result = smartKit.fillBy(5, "x");
        assertEquals("xxxxx", result);
    }

    @Test
    public void testFillBy_zeroLength() {
        String result = smartKit.fillBy(0, "x");
        assertEquals("", result);
    }

    @Test
    public void testFillBy_nullToken() {
        String result = smartKit.fillBy(5, null);
        assertEquals("", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFillBy_negativeLength() {
        smartKit.fillBy(-1, "x");
    }

    @Test(expected = NullPointerException.class)
    public void testToString_nullObject() {
        smartKit.toString(null);
    }

    @Test
    public void testToString_stringObject() {
        String testString = "test";
        String result = smartKit.toString(testString);
        assertEquals(testString, result);
    }

    @Test
    public void testToString_arrayObject() {
        String[] testArray = {"a", "b", "c"};
        String result = smartKit.toString(testArray);
        assertTrue(result.startsWith("[java.lang.String, "));
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    public void testClearMap_emptyMap() {
        Map<String, String> map = new java.util.HashMap<>();
        smartKit.clearMap(map);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testClearMap_withNullValues() {
        Map<String, String> map = new java.util.HashMap<>();
        map.put(null, "value");
        map.put("key", null);
        map.put("key1", "value1");
        smartKit.clearMap(map);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("key1"));
    }

    @Test
    public void testClearMap_withEmptyStrings() {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("", "value");
        map.put("key", "");
        map.put("key1", "value1");
        smartKit.clearMap(map);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("key1"));
    }

    @Test
    public void testLoadImages_noSelector() throws IOException, URISyntaxException {
        File tempFile = File.createTempFile("test", ".jpg");
        ImageIO.write(new java.awt.image.BufferedImage(10, 10, java.awt.image.BufferedImage.TYPE_INT_RGB), "jpg", tempFile);

        List<Image> images = smartKit.loadImages(tempFile.getParent(), null);
        assertEquals(1, images.size());
        assertTrue(images.get(0) instanceof Image);

        tempFile.delete();
    }
}
