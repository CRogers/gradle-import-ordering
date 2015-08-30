package com.github.crogers.importordering;

import com.google.common.collect.Lists;

import java.util.List;

public class ImportOrderingExtension {
    private final List<ImportLine> importLines = Lists.newArrayList();

    public void importLine(String pattern) {
        importLines.add(ImportLine.from(pattern, false));
    }

    public void importStatic(String pattern) {
        importLines.add(ImportLine.from(pattern, true));
    }

    public ImportLines getImportLines() {
        return ImportLines.from(importLines);
    }
}
