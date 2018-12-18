package com.example.supercookie;

public final class ChargeResult {

  public static ChargeResult success() {
    return new ChargeResult(true, false, null);
  }

  public static ChargeResult error(String message) {
    return new ChargeResult(false, false, message);
  }

  public static ChargeResult networkError() {
    return new ChargeResult(false, true, null);
  }

  public final boolean success;
  public final boolean networkError;

  /**
   * Set if {@link #success} is false and {@link #networkError} is false.
   */
  public final String errorMessage;

  private ChargeResult(boolean success, boolean networkError, String errorMessage) {
    this.success = success;
    this.networkError = networkError;
    this.errorMessage = errorMessage;
  }
}
