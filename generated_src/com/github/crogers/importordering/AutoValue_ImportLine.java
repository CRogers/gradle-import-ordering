
package com.github.crogers.importordering;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ImportLine extends ImportLine {

  private final String asString;

  AutoValue_ImportLine(
      String asString) {
    if (asString == null) {
      throw new NullPointerException("Null asString");
    }
    this.asString = asString;
  }

  @Override
  public String asString() {
    return asString;
  }

  @Override
  public String toString() {
    return "ImportLine{"
        + "asString=" + asString
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ImportLine) {
      ImportLine that = (ImportLine) o;
      return (this.asString.equals(that.asString()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= asString.hashCode();
    return h;
  }

}
