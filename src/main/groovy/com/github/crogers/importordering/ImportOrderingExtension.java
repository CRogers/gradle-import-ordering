package com.github.crogers.importordering;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public class ImportOrderingExtension implements Settings {
    private final List<ImportLine> importLines = Lists.newArrayList();
    private Optional<Integer> classCountToImportStar = Optional.empty();
    private Optional<Integer> nameCountToStaticImportStar = Optional.empty();

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

    public void classCountToImportStar(int classCountToImportStar) {
        this.classCountToImportStar = Optional.of(classCountToImportStar);
    }

    public void nameCountToStaticImportStar(int nameCountToStaticImportStar) {
        this.nameCountToStaticImportStar = Optional.of(nameCountToStaticImportStar);
    }

    @Override
    public ImportLines getImportLines() {
        return ImportLines.from(importLines);
    }

    @Override
    public Optional<Integer> getClassCountToImportStar() {
        return classCountToImportStar;
    }

    @Override
    public Optional<Integer> getNameCountToStaticImportStar() {
        return nameCountToStaticImportStar;
    }
}
