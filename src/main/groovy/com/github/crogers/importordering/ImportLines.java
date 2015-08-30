package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class ImportLines implements Iterable<ImportLine> {
    protected abstract Iterable<ImportLine> importLines();

    @Override
    public Iterator<ImportLine> iterator() {
        return importLines().iterator();
    }

    public static ImportLines from(Iterable<ImportLine> importLines) {
        return new AutoValue_ImportLines(importLines);
    }

    public static ImportLines from(ImportLine... importLines) {
        return from(Arrays.asList(importLines));
    }

    public static ImportLines noImportLines() {
        return from(Collections.emptyList());
    }

    public static ImportLines importLines(String... importLines) {
        return ImportLines.from(Iterables.transform(Arrays.asList(importLines), ImportLine::instance));
    }
}
