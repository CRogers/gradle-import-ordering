package com.github.crogers.importstyle;

import java.util.Optional;

public interface Settings {
    ImportOrdering getImportOrdering();
    Optional<Integer> getClassCountToImportStar();
    Optional<Integer> getNameCountToStaticImportStar();
}
