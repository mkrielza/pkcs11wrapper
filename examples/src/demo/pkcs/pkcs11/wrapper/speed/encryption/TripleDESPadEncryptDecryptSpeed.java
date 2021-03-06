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

package demo.pkcs.pkcs11.wrapper.speed.encryption;

import org.junit.Test;
import org.xipki.util.BenchmarkExecutor;

import demo.pkcs.pkcs11.wrapper.TestBase;
import demo.pkcs.pkcs11.wrapper.util.Util;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.ValuedSecretKey;
import iaik.pkcs.pkcs11.parameters.InitializationVectorParameters;
import iaik.pkcs.pkcs11.wrapper.Functions;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import junit.framework.Assert;

/**
 * This demo program uses a PKCS#11 module to encrypt and decrypt via
 * CKM_AES_CBC_PAD.
 *
 */
public class TripleDESPadEncryptDecryptSpeed extends TestBase {

  private class MyEncryptExecutor extends EncryptExecutor {

    public MyEncryptExecutor(Token token, char[] pin) throws TokenException {
      super(Functions.mechanismCodeToString(encryptMechanism)
              + " Encrypt Speed",
          getKeyGenMech(token), token, pin,
          getEncryptionMech(token), inputLen);
    }

    @Override
    protected ValuedSecretKey getMinimalKeyTemplate() {
      return getMinimalKeyTemplate0();
    }

  }

  private class MyDecryptExecutor extends DecryptExecutor {

    public MyDecryptExecutor(Token token, char[] pin) throws TokenException {
      super(Functions.mechanismCodeToString(encryptMechanism)
              + " Decrypt Speed",
          getKeyGenMech(token), token, pin,
          getEncryptionMech(token), inputLen);
    }

    @Override
    protected ValuedSecretKey getMinimalKeyTemplate() {
      return getMinimalKeyTemplate0();
    }

  }

  private static final long keyGenMechanism = PKCS11Constants.CKM_DES3_KEY_GEN;

  private static final long encryptMechanism = PKCS11Constants.CKM_DES3_CBC_PAD;

  private static final int inputLen = 1024;

  private static final String inputUnit = "KiB";

  private final byte[] iv;

  public TripleDESPadEncryptDecryptSpeed() {
    iv = randomBytes(8);
  }

  private Mechanism getKeyGenMech(Token token) throws TokenException {
    return getSupportedMechanism(token, keyGenMechanism);
  }

  private Mechanism getEncryptionMech(Token token) throws TokenException {
    Mechanism mech = getSupportedMechanism(token, encryptMechanism);
    InitializationVectorParameters encryptIVParameters =
        new InitializationVectorParameters(iv);
    mech.setParameters(encryptIVParameters);
    return mech;
  }

  private ValuedSecretKey getMinimalKeyTemplate0() {
    ValuedSecretKey keyTemplate = ValuedSecretKey.newDES3SecretKey();
    return keyTemplate;
  }

  @Test
  public void main() throws TokenException {
    Token token = getNonNullToken();
    if (!Util.supports(token, keyGenMechanism)) {
      System.out.println(Functions.mechanismCodeToString(keyGenMechanism)
          + " is not supported, skip test");
      return;
    }

    if (!Util.supports(token, encryptMechanism)) {
      System.out.println(Functions.mechanismCodeToString(encryptMechanism)
          + " is not supported, skip test");
      return;
    }

    BenchmarkExecutor executor = new MyEncryptExecutor(token, getModulePin());
    executor.setThreads(getSpeedTestThreads());
    executor.setDuration(getSpeedTestDuration());
    executor.setUnit(inputUnit);
    executor.execute();
    Assert.assertEquals("Encrypt speed", 0, executor.getErrorAccout());

    executor = new MyDecryptExecutor(token, getModulePin());
    executor.setThreads(getSpeedTestThreads());
    executor.setDuration(getSpeedTestDuration());
    executor.setUnit(inputUnit);
    executor.execute();
    Assert.assertEquals("Decrypt speed", 0, executor.getErrorAccout());
  }

}
