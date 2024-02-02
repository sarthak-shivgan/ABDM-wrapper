/* (C) 2024 */
package com.nha.abdm.wrapper.common.dataPackaging.keys;

import com.nha.abdm.wrapper.common.dataPackaging.Constants;
import java.security.*;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class KeyController {
  private String senderPublicKey;
  private String senderPrivateKey;
  private String senderNonce;

  public KeyMaterial fetchKeys() throws Exception {
    if ((senderPrivateKey == null || senderPublicKey == null) || senderNonce == null) {
      return generate();
    } else return new KeyMaterial(this.senderPublicKey, this.senderPrivateKey, this.senderNonce);
  }

  public KeyMaterial generate() throws Exception {
    KeyPair keyPair = generateKeyPair();
    String receiverPrivateKey = getBase64String(getEncodedPrivateKey(keyPair.getPrivate()));
    String receiverPublicKey = getBase64String(getEncodedPublicKey(keyPair.getPublic()));
    String receiverNonce = generateRandomKey();
    return new KeyMaterial(receiverPrivateKey, receiverPublicKey, receiverNonce);
  }

  private KeyPair generateKeyPair()
      throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    Security.addProvider(new BouncyCastleProvider());
    KeyPairGenerator keyPairGenerator =
        KeyPairGenerator.getInstance(Constants.ALGORITHM, Constants.PROVIDER);
    X9ECParameters ecParameters = CustomNamedCurves.getByName(Constants.CURVE);
    ECParameterSpec ecSpec =
        new ECParameterSpec(
            ecParameters.getCurve(),
            ecParameters.getG(),
            ecParameters.getN(),
            ecParameters.getH(),
            ecParameters.getSeed());

    keyPairGenerator.initialize(ecSpec, new SecureRandom());
    return keyPairGenerator.generateKeyPair();
  }

  private String getBase64String(byte[] value) {
    return new String(org.bouncycastle.util.encoders.Base64.encode(value));
  }

  private byte[] getEncodedPrivateKey(PrivateKey key) throws Exception {
    ECPrivateKey ecKey = (ECPrivateKey) key;
    return ecKey.getD().toByteArray();
  }

  private byte[] getEncodedPublicKey(PublicKey key) throws Exception {
    ECPublicKey ecKey = (ECPublicKey) key;
    return ecKey.getQ().getEncoded(false);
  }

  private String generateRandomKey() {
    byte[] salt = new byte[32];
    SecureRandom random = new SecureRandom();
    random.nextBytes(salt);
    return getBase64String(salt);
  }
}
