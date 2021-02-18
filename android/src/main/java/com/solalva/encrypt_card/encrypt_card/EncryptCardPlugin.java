package com.solalva.encrypt_card.encrypt_card;

import androidx.annotation.NonNull;

import android.app.Activity;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import adyen.com.adyencse.pojo.Card;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/** EncryptCardPlugin */
public class EncryptCardPlugin implements MethodCallHandler {
  private final Activity activity;

  private EncryptCardPlugin(Activity activity) {
      this.activity = activity;
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
      final MethodChannel channel = new MethodChannel(registrar.messenger(), "encrypt_flutter");
      channel.setMethodCallHandler(new EncryptCardPlugin(registrar.activity()));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
      if (!(call.arguments instanceof Map)) {
          throw new IllegalArgumentException("Plugin not passing a map as parameter: " + call.arguments);
      }
      Map<String, Object> arguments = (Map<String, Object>) call.arguments;
      switch (call.method) {
          case "encryptedToken":
              try {
                  result.success(getEncryptedToken(arguments));
              } catch (Exception ex) {
                  result.error("Error", "Get encrypted Token failed.", ex);
              }
              break;
          default:
              result.notImplemented();
      }
  }

  private String getEncryptedToken(Map<String, Object> arguments) throws Error {
      String publicKey = (String) arguments.get("publicKey");
      String cardNumber = (String) arguments.get("cardNumber");
      String cardSecurityCode = (String) arguments.get("cardSecurityCode");
      String cardExpiryMonth = (String) arguments.get("cardExpiryMonth");
      String cardExpiryYear = (String) arguments.get("cardExpiryYear");
      String holderName = (String) arguments.get("holderName");

      Date generationDate;
      String generationDateString = (String) arguments.get("generationDate");

      try {
          generationDate = convertDate(generationDateString);
      } catch (Exception ex) {
          throw new Error("Could not parse the generation date string:'" + generationDateString + "'", ex);
      }

      try {
        Card card = new Card.Builder()
            .setHolderName(holderName)
            .setCvc(cardSecurityCode)
            .setExpiryMonth(cardExpiryMonth)
            .setExpiryYear(cardExpiryYear)
            .setGenerationTime(generationDate)
            .setNumber(cardNumber)
            .build();

        //	Encrypt card data
        return card.serialize(publicKey);
      } catch (Exception ex) {
          return "";//throw new Error("Could not encrypt the card", ex);
      }
  }

  private Date convertDate(String generationDateString) throws Error {
      DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
      try {
          return df2.parse(generationDateString);
      } catch (Exception ex) {
          Date date = new Date(System.currentTimeMillis());
          return date;
          //throw new Error("Could not parse the generation date string:'" + generationDateString + "'");
      }
  }

}