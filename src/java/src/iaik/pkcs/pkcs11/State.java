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

package iaik.pkcs.pkcs11;

import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;

/**
 * Objects of this class show the state of a session. This state is only a
 * snapshot of the session's state at the time this state object was created.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 */
public class State {

  /**
   * Constant for a read-only public session.
   */
  public static final State RO_PUBLIC_SESSION =
      new State(PKCS11Constants.CKS_RO_PUBLIC_SESSION);

  /**
   * Constant for a read-only user session.
   */
  public static final State RO_USER_FUNCTIONS =
      new State(PKCS11Constants.CKS_RO_USER_FUNCTIONS);

  /**
   * Constant for a read-write public session.
   */
  public static final State RW_PUBLIC_SESSION =
      new State(PKCS11Constants.CKS_RW_PUBLIC_SESSION);

  /**
   * Constant for a read-write user session.
   */
  public static final State RW_USER_FUNCTIONS =
      new State(PKCS11Constants.CKS_RW_USER_FUNCTIONS);

  /**
   * Constant for a read-write security officer session.
   */
  public static final State RW_SO_FUNCTIONS =
      new State(PKCS11Constants.CKS_RW_SO_FUNCTIONS);

  /**
   * The status code of this state as defined in PKCS#11.
   */
  protected long code;

  /**
   * Constructor that simply takes the status code as defined in PKCS#11.
   *
   * @param code
   *          One of: PKCS11Constants.CKS_RO_PUBLIC_SESSION,
   *                  PKCS11Constants.CKS_RO_USER_FUNCTIONS,
   *                  PKCS11Constants.CKS_RW_PUBLIC_SESSION,
   *                  PKCS11Constants.CKS_RW_USER_FUNCTIONS or
   *                  PKCS11Constants.CKS_RW_SO_FUNCTIONS.
   */
  protected State(long code) {
    this.code = code;
  }

  /**
   * Compares the state code of this object with the other
   * object. Returns only true, if those are equal in both objects.
   *
   * @param otherObject
   *          The other State object.
   * @return True, if other is an instance of State and the state code
   *         of both objects are equal. False, otherwise.
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    } else if (!(otherObject instanceof State)) {
      return false;
    }

    State other = (State) otherObject;
    return (this.code == other.code);
  }

  /**
   * The overriding of this method should ensure that the objects of this
   * class work correctly in a hashtable.
   *
   * @return The hash code of this object. Gained from the state code.
   */
  @Override
  public int hashCode() {
    return (int) code;
  }

  /**
   * Returns the string representation of this object.
   *
   * @return The string representation of object
   */
  @Override
  public String toString() {
    if (code == PKCS11Constants.CKS_RO_PUBLIC_SESSION) {
      return "Read-Only Public Session";
    } else if (code == PKCS11Constants.CKS_RO_USER_FUNCTIONS) {
      return "Read-Only User Session";
    } else if (code == PKCS11Constants.CKS_RW_PUBLIC_SESSION) {
      return "Read/Write Public Session";
    } else if (code == PKCS11Constants.CKS_RW_USER_FUNCTIONS) {
      return "Read/Write User Functions";
    } else if (code == PKCS11Constants.CKS_RW_SO_FUNCTIONS) {
      return "Read/Write Security Officer Functions";
    } else {
      return "ERROR: unknown session state with code: " + code;
    }
  }

}
