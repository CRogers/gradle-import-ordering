package com.github.crogers.importordering;

import java.util.Optional;

public interface Settings {
    ImportOrdering getImportOrdering();
    Optional<Integer> getClassCountToImportStar();
    Optional<Integer> getNameCountToStaticImportStar();
}
