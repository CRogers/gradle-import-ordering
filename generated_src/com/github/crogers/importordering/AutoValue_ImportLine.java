
package com.github.crogers.importordering;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ImportLine extends ImportLine {

  private final String asString;
  private final boolean isStatic;

  AutoValue_ImportLine(
      String asString,
      boolean isStatic) {
    if (asString == null) {
      throw new NullPointerException("Null asString");
    }
    this.asString = asString;
    this.isStatic = isStatic;
  }

  @Override
  public String asString() {
    return asString;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public String toString() {
    return "ImportLine{"
        + "asString=" + asString + ", "
        + "isStatic=" + isStatic
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ImportLine) {
      ImportLine that = (ImportLine) o;
      return (this.asString.equals(that.asString()))
           && (this.isStatic == that.isStatic());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= asString.hashCode();
    h *= 1000003;
    h ^= isStatic ? 1231 : 1237;
    return h;
  }

}
