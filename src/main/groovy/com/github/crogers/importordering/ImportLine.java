package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImportLine {
    public abstract String asString();
    public abstract WithSubpackages withSubpackages();
    public abstract boolean isStatic();

    public static ImportLine instance(String importLine) {
        return instance(importLine, WithSubpackages.WITH_SUBPACKAGES);
    }

    public static ImportLine instance(String importLine, WithSubpackages withSubpackages) {
        return new AutoValue_ImportLine(importLine, withSubpackages, false);
    }

    public static ImportLine fromStatic(String importLine) {
        return new AutoValue_ImportLine(importLine, WithSubpackages.WITH_SUBPACKAGES, true);
    }
}
