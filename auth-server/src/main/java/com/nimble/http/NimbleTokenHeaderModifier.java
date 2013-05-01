package com.nimble.http;

import com.nimble.content.ContentModifier;
import com.nimble.security.core.userdetails.NimbleUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/25/13
 * Time: 5:20 PM
 */
public class NimbleTokenHeaderModifier implements ContentModifier {
    public String modify(String input) {
        return input;
    }

    public Map<String, List<String>> modify(Map<String, List<String>> input) {
        //need to add the Nimble token header
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            //should not be here if this is null;
            Authentication auth = ctx.getAuthentication();
            NimbleUser user = (NimbleUser) auth.getPrincipal();
            if (user != null) {
                //it should never be the case that we do not get here due to lack of having a user.
                //Checks are done via security configuration
                List<String> authList = new LinkedList<String>();
                authList.add("Nimble token=\"" + user.getNimbleToken() + "\"");
                //want to overwrite the Authorization if it already existed.  Authorization values cannot be multiple.
                //servers will likely return a 400 error
                input.put("Authorization", Collections.unmodifiableList(authList));
            }
        }
        return input;
    }
}
