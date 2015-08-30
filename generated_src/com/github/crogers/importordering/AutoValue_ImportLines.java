
package com.github.crogers.importordering;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ImportLines extends ImportLines {

  private final java.lang.Iterable<ImportLine> importLines;

  AutoValue_ImportLines(
      java.lang.Iterable<ImportLine> importLines) {
    if (importLines == null) {
      throw new NullPointerException("Null importLines");
    }
    this.importLines = importLines;
  }

  @Override
  protected java.lang.Iterable<ImportLine> importLines() {
    return importLines;
  }

  @Override
  public String toString() {
    return "ImportLines{"
        + "importLines=" + importLines
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ImportLines) {
      ImportLines that = (ImportLines) o;
      return (this.importLines.equals(that.importLines()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= importLines.hashCode();
    return h;
  }

}
