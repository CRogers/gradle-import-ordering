package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImportLine {
    public abstract String asString();
    public abstract boolean isStatic();

    public static ImportLine instance(String importLine) {
        return new AutoValue_ImportLine(importLine, false);
    }

    public static ImportLine fromStatic(String importLine) {
        return new AutoValue_ImportLine(importLine, true);
    }
}
