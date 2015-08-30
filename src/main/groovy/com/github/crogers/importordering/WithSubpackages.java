package com.github.crogers.importordering;

public enum WithSubpackages {
    WITH_SUBPACKAGES(true),
    WITHOUT_SUBPACKAGES(false);

    private final boolean withSubpackages;

    WithSubpackages(boolean withSubpackages) {
        this.withSubpackages = withSubpackages;
    }

    public boolean asBoolean() {
        return withSubpackages;
    }
}
