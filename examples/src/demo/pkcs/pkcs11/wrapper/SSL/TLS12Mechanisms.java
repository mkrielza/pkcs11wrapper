// Copyright (c) 2002 Graz University of Technology. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include the following acknowledgment:
//
//    "This product includes software developed by IAIK of Graz University of
//     Technology."
//
//    Alternately, this acknowledgment may appear in the software itself, if and
//    wherever such third-party acknowledgments normally appear.
//
// 4. The names "Graz University of Technology" and "IAIK of Graz University of
//    Technology" must not be used to endorse or promote products derived from
//    this software without prior written permission.
//
// 5. Products derived from this software may not be called "IAIK PKCS Wrapper",
//    nor may "IAIK" appear in their name, without prior written permission of
//    Graz University of Technology.
//
// THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE LICENSOR BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package demo.pkcs.pkcs11.wrapper.SSL;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Test;

import demo.pkcs.pkcs11.wrapper.TestBase;
import demo.pkcs.pkcs11.wrapper.util.Util;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.SecretKey;
import iaik.pkcs.pkcs11.objects.ValuedSecretKey;
import iaik.pkcs.pkcs11.parameters.SSL3KeyMaterialOutParameters;
import iaik.pkcs.pkcs11.parameters.SSL3RandomDataParameters;
import iaik.pkcs.pkcs11.parameters.TLS12KeyMaterialParameters;
import iaik.pkcs.pkcs11.parameters.TLS12MasterKeyDeriveParameters;
import iaik.pkcs.pkcs11.parameters.VersionParameters;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;

/**
 * This demo program shows how to use the SSL mechanisms. Ensure that your token
 * supports these features.
 */
public class TLS12Mechanisms extends TestBase {

  @Test
  public void main() throws TokenException, NoSuchAlgorithmException {
    // check whether supported in current JDK
    try {
      SSL3RandomDataParameters randomInfo =
          new SSL3RandomDataParameters(new byte[28], new byte[28]);
      new TLS12MasterKeyDeriveParameters(randomInfo, new VersionParameters(),
          PKCS11Constants.CKM_SHA256);
    } catch (IllegalStateException ex) {
      System.err.println("Unsupported in current JDK, skip");
      return;
    }

    Token token = getNonNullToken();
    Session session = openReadWriteSession(token);
    try {
      main0(token, session);
    } finally {
      session.closeSession();
    }
  }

  private void main0(Token token, Session session)
      throws TokenException, NoSuchAlgorithmException {
    ValuedSecretKey premasterSecret = null;
    ValuedSecretKey masterSecret = null;

    if (Util.supports(token, PKCS11Constants.CKM_SSL3_PRE_MASTER_KEY_GEN)) {
      LOG.info("##################################################");
      LOG.info("Generating premaster secret");

      VersionParameters versionParameters =
          new VersionParameters((byte) 3, (byte) 0);

      Mechanism sslPremasterKeyGenerationMechanism = Mechanism
          .get(PKCS11Constants.CKM_SSL3_PRE_MASTER_KEY_GEN);
      sslPremasterKeyGenerationMechanism.setParameters(versionParameters);

      ValuedSecretKey premasterSecretTemplate =
          ValuedSecretKey.newGenericSecretKey();
      premasterSecretTemplate.getDerive().setBooleanValue(Boolean.TRUE);

      premasterSecret = (ValuedSecretKey) session.generateKey(
          sslPremasterKeyGenerationMechanism, premasterSecretTemplate);

      LOG.info("the premaster secret is\n{}", premasterSecret.toString());
      LOG.info("##################################################");
    }

    final long prfHash = PKCS11Constants.CKM_SHA256;

    SecureRandom randomSource = SecureRandom.getInstance("SHA1PRNG");
    if (Util.supports(token, PKCS11Constants.CKM_TLS12_MASTER_KEY_DERIVE)
        && (premasterSecret != null)) {
      LOG.info("##################################################");
      LOG.info("Deriving master secret");

      byte[] clientRandom = new byte[28];
      byte[] serverRandom = new byte[28];

      LOG.info("generating client random... ");
      randomSource.nextBytes(clientRandom);
      LOG.info("finished");
      LOG.info("generating server random... ");
      randomSource.nextBytes(serverRandom);
      LOG.info("finished");

      VersionParameters clientVersion = new VersionParameters();
      SSL3RandomDataParameters randomInfo =
          new SSL3RandomDataParameters(clientRandom, serverRandom);
      TLS12MasterKeyDeriveParameters masterKeyDeriveParameters =
          new TLS12MasterKeyDeriveParameters(randomInfo, clientVersion,
              prfHash);

      Mechanism sslMasterKeyDerivationMechanism = Mechanism
          .get(PKCS11Constants.CKM_TLS12_MASTER_KEY_DERIVE);
      sslMasterKeyDerivationMechanism.setParameters(masterKeyDeriveParameters);

      ValuedSecretKey masterSecretTemplate =
          ValuedSecretKey.newGenericSecretKey();
      masterSecretTemplate.getDerive().setBooleanValue(Boolean.TRUE);

      masterSecret = (ValuedSecretKey) session.deriveKey(
          sslMasterKeyDerivationMechanism, premasterSecret,
          masterSecretTemplate);

      LOG.info("the client version is\n{}",
          masterKeyDeriveParameters.getVersion());
      LOG.info("the master secret is\n{}", masterSecret);
      LOG.info("##################################################");
    }

    if (Util.supports(token, PKCS11Constants.CKM_TLS12_KEY_AND_MAC_DERIVE)
        && (masterSecret != null)) {
      LOG.info("##################################################");
      System.out.println("Deriving key material");

      byte[] clientRandom = new byte[28];
      byte[] serverRandom = new byte[28];

      LOG.info("generating client random... ");
      randomSource.nextBytes(clientRandom);
      LOG.info("finished");
      LOG.info("generating server random... ");
      randomSource.nextBytes(serverRandom);
      LOG.info("finished");

      SSL3RandomDataParameters randomInfo =
          new SSL3RandomDataParameters(clientRandom, serverRandom);

      byte[] clientIVBuffer = new byte[16];
      byte[] serverIVBuffer = new byte[16];
      SSL3KeyMaterialOutParameters returedKeyMaterial =
          new SSL3KeyMaterialOutParameters(clientIVBuffer, serverIVBuffer);
      TLS12KeyMaterialParameters keyAndMACDeriveParameters =
          new TLS12KeyMaterialParameters(
              80, 128, 128, false, randomInfo, returedKeyMaterial, prfHash);

      Mechanism sslKeyAndMACDerivationMechanism = Mechanism
          .get(PKCS11Constants.CKM_TLS12_KEY_AND_MAC_DERIVE);
      sslKeyAndMACDerivationMechanism.setParameters(keyAndMACDeriveParameters);

      SecretKey derivedSecret = (SecretKey) session.deriveKey(
          sslKeyAndMACDerivationMechanism, masterSecret, null);

      LOG.info("the key material is\n{}", returedKeyMaterial);
      LOG.info("the derivedSecret is\n{}", derivedSecret);
      LOG.info("##################################################");
    }
  }

}
