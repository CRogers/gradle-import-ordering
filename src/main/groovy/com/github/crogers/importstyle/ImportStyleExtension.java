package com.github.crogers.importstyle;

import org.gradle.api.Action;

import java.util.Optional;

public class ImportStyleExtension implements Settings {
    private final ImportOrderingBuilder importOrderingBuilder = new ImportOrderingBuilder();
    private Optional<Integer> classCountToImportStar = Optional.empty();
    private Optional<Integer> nameCountToStaticImportStar = Optional.empty();

    public void classCountToImportStar(int classCountToImportStar) {
        this.classCountToImportStar = Optional.of(classCountToImportStar);
    }

    public void nameCountToStaticImportStar(int nameCountToStaticImportStar) {
        this.nameCountToStaticImportStar = Optional.of(nameCountToStaticImportStar);
    }

    public void importOrdering(Action<ImportOrderingBuilder> action) {
        action.execute(importOrderingBuilder);
    }

    @Override
    public ImportOrdering getImportOrdering() {
        return importOrderingBuilder.build();
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
