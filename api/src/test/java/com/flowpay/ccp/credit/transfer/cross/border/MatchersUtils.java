package com.flowpay.ccp.credit.transfer.cross.border;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class MatchersUtils {

    public static StringMatchesUUIDPattern isUuidString() {
        return new StringMatchesUUIDPattern();
    }

    private static class StringMatchesUUIDPattern extends TypeSafeMatcher<String> {
        private static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

        @Override
        protected boolean matchesSafely(String s) {
            return s.matches(UUID_REGEX);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string matching the pattern of a UUID");
        }

    }
}
