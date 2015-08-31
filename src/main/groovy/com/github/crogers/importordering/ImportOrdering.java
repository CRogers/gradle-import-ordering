package com.github.crogers.importordering;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class ImportOrdering implements Iterable<ImportLine> {
    protected abstract Iterable<ImportLine> importLines();

    @Override
    public Iterator<ImportLine> iterator() {
        return importLines().iterator();
    }

    public static ImportOrdering from(Iterable<ImportLine> importLines) {
        return new AutoValue_ImportOrdering(importLines);
    }

    public static ImportOrdering from(ImportLine... importLines) {
        return from(Arrays.asList(importLines));
    }

    public static ImportOrdering noImportLines() {
        return from(Collections.emptyList());
    }

    public static ImportOrdering importLines(String... importLines) {
        return ImportOrdering.from(Iterables.transform(Arrays.asList(importLines), ImportLine::instance));
    }
}
