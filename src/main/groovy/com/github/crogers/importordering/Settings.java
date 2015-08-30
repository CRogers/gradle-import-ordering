package com.github.crogers.importordering;

import java.util.Optional;

public interface Settings {
    ImportLines getImportLines();
    Optional<Integer> getClassCountToImportStar();
    Optional<Integer> getNameCountToStaticImportStar();
}
