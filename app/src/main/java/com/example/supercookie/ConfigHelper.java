package com.example.supercookie;

import android.util.Log;
import java.util.UUID;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ConfigHelper {

  public static final String SQUARE_LOCATION_ID_FOR_GOOGLE_PAY = "REPLACE_ME";
  private static final String CHARGE_SERVER_HOST = "REPLACE_ME";
  private static final String CHARGE_SERVER_URL = "https://" + CHARGE_SERVER_HOST + "/";

  public static boolean serverHostSet() {
    return !CHARGE_SERVER_HOST.equals("REPLACE_ME");
  }

  public static boolean squareLocationIdSet() {
    return !SQUARE_LOCATION_ID_FOR_GOOGLE_PAY.equals("REPLACE_ME");
  }

  public static void printCurlCommand(String nonce) {
    String uuid = UUID.randomUUID().toString();
    Log.d("ExampleApplication",
        "Run this curl command to charge the nonce:\n"
            + "curl --request POST https://connect.squareup.com/v2/locations/SQUARE_LOCATION_ID_FOR_GOOGLE_PAY/transactions \\\n"
            + "--header \"Content-Type: application/json\" \\\n"
            + "--header \"Authorization: Bearer YOUR_ACCESS_TOKEN\" \\\n"
            + "--header \"Accept: application/json\" \\\n"
            + "--data \'{\n"
            + "\"idempotency_key\": \"" + uuid + "\",\n"
            + "\"amount_money\": {\n"
            + "\"amount\": 100,\n"
            + "\"currency\": \"USD\"},\n"
            + "\"card_nonce\": \"" + nonce + "\""
            + "}\'");
  }

  public static Retrofit createRetrofitInstance() {
    return new Retrofit
        .Builder()
        .baseUrl(ConfigHelper.CHARGE_SERVER_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build();
  }
}
