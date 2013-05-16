package com.nimble.content;

import java.util.List;
import java.util.Map;

/**
 * Date: 4/25/13
 * Time: 4:43 PM
 */
public interface ContentModifier {
    String modify(String input);

    Map<String, List<String>> modify(Map<String, List<String>> input);

}
