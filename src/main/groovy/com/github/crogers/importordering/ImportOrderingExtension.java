package com.github.crogers.importordering;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class ImportOrderingExtension {
    private final List<ImportLine> importLines = Lists.newArrayList();

    public void importLine(String pattern) {
        importLines.add(ImportLine.from(pattern));
    }

    public List<ImportLine> getImportLines() {
        return Collections.unmodifiableList(importLines);
    }
}
