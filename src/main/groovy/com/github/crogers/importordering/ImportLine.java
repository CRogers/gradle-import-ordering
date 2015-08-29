package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImportLine {
    public abstract String asString();

    public static ImportLine from(String importLine) {
        return new AutoValue_ImportLine(importLine);
    }
}
