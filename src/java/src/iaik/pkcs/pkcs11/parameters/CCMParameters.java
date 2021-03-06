/*
 *
 * Copyright (c) 2019 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iaik.pkcs.pkcs11.parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import iaik.pkcs.pkcs11.Util;
import iaik.pkcs.pkcs11.wrapper.Functions;

/**
 * CK_CCM_PARAMS
 *
 * @author Lijun Liao
 * @since 1.4.5
 *
 */
public class CCMParameters implements Parameters {

  public static final String CLASS_CK_PARAMS =
      "sun.security.pkcs11.wrapper.CK_CCM_PARAMS";

  private static final Constructor<?> constructor;

  private final int dataLen;
  private final byte[] iv;
  private final byte[] aad;
  private final int tagLen;

  static {
    constructor = Util.getConstructor(CLASS_CK_PARAMS,
        int.class, byte[].class, byte[].class, int.class);
  }

  public CCMParameters(int tagLen, byte[] iv, byte[] aad, int dataLen) {
    if (constructor == null) {
      throw new IllegalStateException(
          CLASS_CK_PARAMS + " is not available in the JDK");
    }
    this.dataLen = dataLen;
    this.iv = iv;
    this.aad = aad;
    this.tagLen = tagLen;
  }

  @Override
  public String toString() {
    return Util.concatObjectsCap(100,
        "\n  ulDataLen: ", dataLen,
        "\n  iv: ", Functions.toHexString(iv),
        "\n  aad: ", Functions.toHexString(aad),
        "\n  tagLen: ", tagLen);
  }

  @Override
  public int hashCode() {
    return dataLen ^ Util.hashCode(iv) ^ Util.hashCode(aad) ^ tagLen;
  }

  @Override
  public Object getPKCS11ParamsObject() {
    try {
      return constructor.newInstance(tagLen, iv, aad, dataLen);
    } catch (SecurityException | InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(
          "Could not create new instance of " + CLASS_CK_PARAMS, ex);
    }
  }

}
