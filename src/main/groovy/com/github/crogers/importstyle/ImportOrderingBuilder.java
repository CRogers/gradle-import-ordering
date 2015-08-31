package com.github.crogers.importstyle;

import com.google.common.collect.Lists;

import java.util.List;

public class ImportOrderingBuilder {
    private final List<ImportLine> importLines = Lists.newArrayList();

    public void importLine(String pattern) {
        importLines.add(ImportLine.instance(pattern));
    }

    public void importLine(WithSubpackages withSubpackages, String pattern) {
        importLines.add(ImportLine.instance(pattern, withSubpackages));
    }

    public void importStatic(String pattern) {
        importLines.add(ImportLine.fromStatic(pattern));
    }

    public void importStatic(WithSubpackages withSubpackages, String pattern) {
        importLines.add(ImportLine.fromStatic(pattern, withSubpackages));
    }

    public WithSubpackages withoutSubpackages() {
        return WithSubpackages.WITHOUT_SUBPACKAGES;
    }

    public ImportOrdering build() {
        return ImportOrdering.from(importLines);
    }
}
