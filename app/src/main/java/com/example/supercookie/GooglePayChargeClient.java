package com.example.supercookie;

import androidx.annotation.Nullable;
import sqip.Call;
import sqip.GooglePay;
import sqip.GooglePayNonceResult;

public class GooglePayChargeClient {
  private final ChargeCall.Factory chargeCallFactory;

  @Nullable private CheckoutActivity activity;
  @Nullable private Call<GooglePayNonceResult> requestNonceCall;
  @Nullable private Call<ChargeResult> chargeCall;

  GooglePayChargeClient(ChargeCall.Factory chargeCallFactory) {
    this.chargeCallFactory = chargeCallFactory;
  }

  public void charge(String googlePayToken) {
    if (nonceRequestInFlight() || chargeRequestInFlight()) {
      return;
    }
    requestNonceCall = GooglePay.requestGooglePayNonce(googlePayToken);
    requestNonceCall.enqueue(result -> onNonceRequestResult(googlePayToken, result));
  }

  private void onNonceRequestResult(String googlePayToken, GooglePayNonceResult result) {
    if (!nonceRequestInFlight()) {
      return;
    }
    requestNonceCall = null;
    if (activity == null) {
      return;
    }
    if (result.isSuccess()) {
      String nonce = result.getSuccessValue().getNonce();
      chargeNonce(nonce);
    } else if (result.isError()) {
      GooglePayNonceResult.Error error = result.getErrorValue();
      switch (error.getCode()) {
        case NO_NETWORK:
          activity.showNetworkErrorRetryPayment(() -> charge(googlePayToken));
          break;
        case UNSUPPORTED_SDK_VERSION:
        case USAGE_ERROR:
          activity.showError(error.getMessage());
      }
    }
  }

  public void cancel() {
    if (nonceRequestInFlight()) {
      requestNonceCall.cancel();
      requestNonceCall = null;
    }
    if (chargeRequestInFlight()) {
      chargeCall.cancel();
      chargeCall = null;
    }
  }

  public void onActivityCreated(CheckoutActivity activity) {
    this.activity = activity;
  }

  public void onActivityDestroyed() {
    activity = null;
  }

  private boolean chargeRequestInFlight() {
    return chargeCall != null;
  }

  private boolean nonceRequestInFlight() {
    return requestNonceCall != null;
  }

  private void chargeNonce(String nonce) {
    if (!ConfigHelper.serverHostSet()) {
      if (activity == null) {
        return;
      }
      ConfigHelper.printCurlCommand(nonce);
      activity.showServerHostNotSet();
      return;
    }
    chargeCall = chargeCallFactory.create(nonce);
    chargeCall.enqueue(chargeResult -> onChargeResult(nonce, chargeResult));
  }

  private void onChargeResult(String nonce, ChargeResult chargeResult) {
    if (!chargeRequestInFlight()) {
      return;
    }
    chargeCall = null;
    if (activity == null) {
      return;
    }
    if (chargeResult.success) {
      activity.showSuccessCharge();
    } else if (chargeResult.networkError) {
      activity.showNetworkErrorRetryPayment(() -> chargeNonce(nonce));
    } else {
      activity.showError(chargeResult.errorMessage);
    }
  }
}
