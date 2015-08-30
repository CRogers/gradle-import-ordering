package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImportLine {
    public abstract String asString();
    public abstract boolean isStatic();

    public static ImportLine from(String importLine, boolean isStatic) {
        return new AutoValue_ImportLine(importLine, isStatic);
    }
}
