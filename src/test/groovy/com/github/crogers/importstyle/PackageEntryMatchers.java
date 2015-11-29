package com.github.crogers.importstyle;

import com.intellij.psi.codeStyle.PackageEntry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

public class PackageEntryMatchers {
    public static Matcher<PackageEntry> package_(Matcher<PackageEntry>... matchers) {
        return withDescription(allOf(matchers), "package ");
    }

    public static class PackageNameMatcher extends FeatureMatcher<PackageEntry, String> {
        public PackageNameMatcher(Matcher<String> nameMatcher) {
            super(nameMatcher, "named", "name");
        }

        @Override
        protected String featureValueOf(PackageEntry packageEntry) {
            return packageEntry.getPackageName();
        }
    }

    public static PackageNameMatcher named(String name) {
        return new PackageNameMatcher(equalTo(name));
    }

    public static class WithSubpackagesMatcher extends FeatureMatcher<PackageEntry, Boolean> {
        public WithSubpackagesMatcher(Matcher<Boolean> matcher) {
            super(matcher, "with subpackages", "with subpackages");
        }

        @Override
        protected Boolean featureValueOf(PackageEntry packageEntry) {
            return packageEntry.isWithSubpackages();
        }
    }

    public static Matcher<PackageEntry> withSubpackages() {
        return new WithSubpackagesMatcher(equalTo(true));
    }

    public static Matcher<PackageEntry> withoutSubpackages() {
        return new WithSubpackagesMatcher(equalTo(false));
    }

    public static class StaticMatcher extends FeatureMatcher<PackageEntry, Boolean> {
        public StaticMatcher(Matcher<Boolean> matcher) {
            super(matcher, "is static", "is static");
        }

        @Override
        protected Boolean featureValueOf(PackageEntry packageEntry) {
            return packageEntry.isStatic();
        }
    }

    public static Matcher<PackageEntry> isStatic() {
        return new StaticMatcher(equalTo(true));
    }

    public static Matcher<PackageEntry> notStatic() {
        return new StaticMatcher(equalTo(false));
    }

    public static class WithDescription<T> extends BaseMatcher<T> {
        private final Matcher<T> matcher;
        private final String description;

        public WithDescription(Matcher<T> matcher, String description) {
            this.matcher = matcher;
            this.description = description;
        }

        @Override
        public boolean matches(Object o) {
            return matcher.matches(o);
        }

        @Override
        public void describeTo(Description desc) {
            desc.appendText(description);
            matcher.describeTo(desc);
        }
    }

    public static <T> Matcher<T> withDescription(Matcher<T> matcher, String description) {
        return new WithDescription<>(matcher, description);
    }
}
