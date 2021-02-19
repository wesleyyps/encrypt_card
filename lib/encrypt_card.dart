import 'dart:async';
import 'package:flutter/services.dart';

class EncryptCard {
  static const MethodChannel _channel = const MethodChannel('encrypt_flutter');

  /// Encrypts Card information into a single token after requesting a public token from your [publicKeyToken].
  /// It requires your [publicKeyToken], [card] and a [generationDate].
  static Future<String> encryptedToken(
      {String publicKeyToken,
      String publicKey,
      Environment environment = Environment.TEST,
      String holderName,
      CreditCard card,
      DateTime generationDate}) async {
    ArgumentError.checkNotNull(publicKeyToken, 'publicKeyToken');
    ArgumentError.checkNotNull(publicKey, 'publicKey');
    ArgumentError.checkNotNull(generationDate, 'generationDate');
    ArgumentError.checkNotNull(holderName, 'holderName');
    ArgumentError.checkNotNull(card, 'card');
    ArgumentError.checkNotNull(card.number, 'card.number');
    ArgumentError.checkNotNull(card.securityCode, 'card.securityCode');
    ArgumentError.checkNotNull(card.expiryMonth, 'card.expiryMonth');
    ArgumentError.checkNotNull(card.expiryYear, 'card.expiryYear');

    try {
      return await _channel.invokeMethod('encryptedToken', <String, dynamic>{
        'publicKeyToken': publicKeyToken,
        'publicKey': publicKey,
        'cardNumber': card.number,
        'cardSecurityCode': card.securityCode,
        'cardExpiryMonth': card.expiryMonth,
        'cardExpiryYear': card.expiryYear,
        'environment': environment.toString().split('.')[1],
        'holderName': holderName,
        'generationDate': generationDate.toIso8601String()
      });
    } catch (ex) {
      throw Exception('Could not encrypt the token from the card:$ex');
    }
  }
}

enum Environment { TEST, LIVE }

class CreditCard {
  ///The card Number
  final String number;

  ///The card security code
  final String securityCode;

  ///The card expiry Month.
  ///Valid Value are [01-12]
  final String expiryMonth;

  ///The card expiry year.
  final String expiryYear;

  CreditCard(
      {this.number, this.securityCode, this.expiryMonth, this.expiryYear});
}