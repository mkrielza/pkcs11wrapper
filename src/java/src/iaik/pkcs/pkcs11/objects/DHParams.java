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

package iaik.pkcs.pkcs11.objects;

import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.Util;

/**
 * Objects of this class represent DH domain parameters as specified by PKCS#11
 * v2.11.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 */
// CHECKSTYLE:SKIP
public class DHParams extends DomainParameters {

  /**
   * The prime (p) of this DH key.
   */
  protected ByteArrayAttribute prime;

  /**
   * The base (g) of this DH key.
   */
  protected ByteArrayAttribute base;

  /**
   * The length of the prime value in bits.
   */
  protected LongAttribute primeBits;

  /**
   * Default Constructor.
   */
  public DHParams() {
    keyType.setLongValue(Key.KeyType.DH);
  }

  /**
   * Called by getInstance to create an instance of a PKCS#11 DH private key.
   *
   * @param session
   *          The session to use for reading attributes. This session must
   *          have the appropriate rights; i.e. it must be a user-session, if
   *          it is a private object.
   * @param objectHandle
   *          The object handle as given from the PKCS#111 module.
   * @exception TokenException
   *              If getting the attributes failed.
   */
  protected DHParams(Session session, long objectHandle) throws TokenException {
    super(session, objectHandle);
    keyType.setLongValue(Key.KeyType.DH);
  }

  /**
   * The getInstance method of the PrivateKey class uses this method to create
   * an instance of a PKCS#11 DH private key.
   *
   * @param session
   *          The session to use for reading attributes. This session must
   *          have the appropriate rights; i.e. it must be a user-session, if
   *          it is a private object.
   * @param objectHandle
   *          The object handle as given from the PKCS#111 module.
   * @return The object representing the PKCS#11 object.
   *         The returned object can be casted to the
   *         according sub-class.
   * @exception TokenException
   *              If getting the attributes failed.
   */
  public static PKCS11Object getInstance(Session session, long objectHandle)
      throws TokenException {
    return new DHParams(session, objectHandle);
  }

  /**
   * Put all attributes of the given object into the attributes table of this
   * object. This method is only static to be able to access invoke the
   * implementation of this method for each class separately.
   *
   * @param object
   *          The object to handle.
   */
  protected static void putAttributesInTable(DHParams object) {
    Util.requireNonNull("object", object);
    object.attributeTable.put(Attribute.PRIME, object.prime);
    object.attributeTable.put(Attribute.BASE, object.base);
    object.attributeTable.put(Attribute.PRIME_BITS, object.primeBits);
  }

  /**
   * Allocates the attribute objects for this class and adds them to the
   * attribute table.
   */
  @Override
  protected void allocateAttributes() {
    super.allocateAttributes();

    prime = new ByteArrayAttribute(Attribute.PRIME);
    base = new ByteArrayAttribute(Attribute.BASE);
    primeBits = new LongAttribute(Attribute.PRIME_BITS);

    putAttributesInTable(this);
  }

  /**
   * Compares all member variables of this object with the other object.
   * Returns only true, if all are equal in both objects.
   *
   * @param otherObject
   *          The other object to compare to.
   * @return True, if other is an instance of this class and all member
   *         variables of both objects are equal. False, otherwise.
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    } else if (!(otherObject instanceof DHParams)) {
      return false;
    }

    DHParams other = (DHParams) otherObject;
    return super.equals(other)
        && this.prime.equals(other.prime)
        && this.base.equals(other.base)
        && this.primeBits.equals(other.primeBits);
  }

  /**
   * Gets the prime attribute of this DH key.
   *
   * @return The prime attribute.
   */
  public ByteArrayAttribute getPrime() {
    return prime;
  }

  /**
   * Gets the base attribute of this DH key.
   *
   * @return The base attribute.
   */
  public ByteArrayAttribute getBase() {
    return base;
  }

  /**
   * Gets the value length of the prime (in bits).
   *
   * @return The prime bits attribute.
   */
  public LongAttribute getPrimeBits() {
    return primeBits;
  }

  /**
   * Read the values of the attributes of this object from the token.
   *
   * @param session
   *          The session to use for reading attributes. This session must
   *          have the appropriate rights; i.e. it must be a user-session, if
   *          it is a private object.
   * @exception TokenException
   *              If getting the attributes failed.
   */
  @Override
  public void readAttributes(Session session) throws TokenException {
    super.readAttributes(session);

    PKCS11Object.getAttributeValues(session, objectHandle, new Attribute[] {
        prime, base, primeBits });
  }

  /**
   * Returns a string representation of the current object. The
   * output is only for debugging purposes and should not be used for other
   * purposes.
   *
   * @return A string presentation of this object for debugging output.
   */
  @Override
  public String toString() {
    String superToString = super.toString();
    return Util.concatObjectsCap(superToString.length() + 100, superToString,
        "\n  Prime (hex): ", prime,
        "\n  Base (hex): ", base,
        "\n  Prime Bits (dec): ", primeBits.toString(10));
  }

}
