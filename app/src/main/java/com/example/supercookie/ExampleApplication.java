package com.example.supercookie;

import android.app.Activity;
import android.app.Application;
import retrofit2.Retrofit;
import sqip.CardEntry;

public class ExampleApplication extends Application {

  public static GooglePayChargeClient createGooglePayChargeClient(Activity activity) {
    ExampleApplication application = (ExampleApplication) activity.getApplication();
    return new GooglePayChargeClient(application.chargeCallFactory);
  }

  private ChargeCall.Factory chargeCallFactory;

  @Override
  public void onCreate() {
    super.onCreate();

    Retrofit retrofit = ConfigHelper.createRetrofitInstance();
    chargeCallFactory = new ChargeCall.Factory(retrofit);

    CardEntryBackgroundHandler cardHandler =
        new CardEntryBackgroundHandler(chargeCallFactory, getResources());
    CardEntry.setCardNonceBackgroundHandler(cardHandler);
  }
}
