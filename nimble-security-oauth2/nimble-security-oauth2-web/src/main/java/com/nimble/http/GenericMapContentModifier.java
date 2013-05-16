package com.nimble.http;

import com.nimble.content.ContentModifier;

import java.util.*;

/**
 * Date: 4/25/13
 * Time: 5:47 PM
 */
public class GenericMapContentModifier implements ContentModifier {
    private Set<String> contentToRemove = null;
    private Map<String, List<String>> contentToSet = null;
    private Map<String, List<String>> contentToAdd = null;

    public void setContentToRemove(Set<String> contentToRemove) {
        this.contentToRemove = contentToRemove;
    }

    public void setContentToSet(Map<String, List<String>> contentToSet) {
        this.contentToSet = contentToSet;
    }

    public void setContentToAdd(Map<String, List<String>> contentToAdd) {
        this.contentToAdd = contentToAdd;
    }

    public String modify(String input) {
        return input;
    }

    private Set<String> getContentToRemove() {
        if (contentToRemove == null) {
            contentToRemove = new HashSet<String>();
        }
        return contentToRemove;
    }

    private Map<String, List<String>> getContentToSet() {
        if (contentToSet == null) {
            contentToSet = new HashMap<String, List<String>>();
        }
        return contentToSet;
    }

    private Map<String, List<String>> getContentToAdd() {
        if (contentToAdd == null) {
            contentToAdd = new HashMap<String, List<String>>();
        }
        return contentToAdd;
    }

    public Map<String, List<String>> modify(Map<String, List<String>> input) {
        //create new in case the input is read only
        Map<String, List<String>> newContent = new HashMap<String, List<String>>();
        Map<String, List<String>> contentToAdd = getContentToAdd();
        Set<String> contentToRemove = getContentToRemove();
        List<String> value;
        for (String key : input.keySet()) {
            if (contentToRemove.contains(key)) {
                //do not add it to the new content
                continue;
            }
            value = input.get(key);
            //do anything else here
            if (contentToAdd.containsKey(key)) {
                value.addAll(contentToAdd.get(key));
            }
            newContent.put(key, value);
        }
        //overwrite existing content as needed
        newContent.putAll(getContentToSet());

        return newContent;
    }
}
