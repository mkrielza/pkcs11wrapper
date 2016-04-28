// Copyright (c) 2002 Graz University of Technology. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
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
//    Technology" must not be used to endorse or promote products derived from this
//    software without prior written permission.
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
import sun.security.pkcs11.wrapper.Constants;

/**
 * Objects of this class represent ECDSA private keys as specified by PKCS#11
 * v2.11.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 * @invariants (ecdsaParams_ <> null)
 *             and (value_ <> null)
 */
public class ECDSAPrivateKey extends PrivateKey {

	/**
	 * The DER-encoding of an X9.62 ECParameters value of this ECDSA key.
	 */
	protected ByteArrayAttribute ecdsaParams_;

	/**
	 * The X9.62 private value (d) of this ECDSA key.
	 */
	protected ByteArrayAttribute value_;

	/**
	 * Deafult Constructor.
	 *
	 * @preconditions
	 * @postconditions
	 */
	public ECDSAPrivateKey() {
		super();
		keyType_.setLongValue(KeyType.ECDSA);
	}

	/**
	 * Called by getInstance to create an instance of a PKCS#11 ECDSA private key.
	 *
	 * @param session The session to use for reading attributes.
	 *                This session must have the appropriate rights; i.e.
	 *                it must be a user-session, if it is a private object.
	 * @param objectHandle The object handle as given from the PKCS#111 module.
	 * @exception TokenException If getting the attributes failed.
	 * @preconditions (session <> null)
	 * @postconditions
	 */
	protected ECDSAPrivateKey(Session session, long objectHandle)
	    throws TokenException
	{
		super(session, objectHandle);
		keyType_.setLongValue(KeyType.ECDSA);
	}

	/**
	 * The getInstance method of the PrivateKey class uses this method to create
	 * an instance of a PKCS#11 ECDSA private key.
	 *
	 * @param session The session to use for reading attributes.
	 *                This session must have the appropriate rights; i.e.
	 *                it must be a user-session, if it is a private object.
	 * @param objectHandle The object handle as given from the PKCS#111 module.
	 * @return The object representing the PKCS#11 object.
	 *         The returned object can be casted to the
	 *         according sub-class.
	 * @exception TokenException If getting the attributes failed.
	 * @preconditions (session <> null)
	 * @postconditions (result <> null) 
	 */
	public static Object getInstance(Session session, long objectHandle)
	    throws TokenException
	{
		return new ECDSAPrivateKey(session, objectHandle);
	}

	/**
	 * Put all attributes of the given object into the attributes table of this
	 * object. This method is only static to be able to access invoke the
	 * implementation of this method for each class separately (see use in
	 * clone()).
	 *
	 * @param object The object to handle.
	 * @preconditions (object <> null)
	 * @postconditions
	 */
	protected static void putAttributesInTable(ECDSAPrivateKey object) {
		if (object == null) {
			throw new NullPointerException("Argument \"object\" must not be null.");
		}

		object.attributeTable_.put(Attribute.ECDSA_PARAMS, object.ecdsaParams_);
		object.attributeTable_.put(Attribute.VALUE, object.value_);
	}

	/**
	 * Allocates the attribute objects for this class and adds them to the
	 * attribute table.
	 *
	 * @preconditions
	 * @postconditions
	 */
	protected void allocateAttributes() {
		super.allocateAttributes();

		ecdsaParams_ = new ByteArrayAttribute(Attribute.ECDSA_PARAMS);
		value_ = new ByteArrayAttribute(Attribute.VALUE);

		putAttributesInTable(this);
	}

	/**
	 * Create a (deep) clone of this object.
	 *
	 * @return A clone of this object.
	 * @preconditions
	 * @postconditions (result <> null)
	 *                 and (result instanceof ECDSAPrivateKey)
	 *                 and (result.equals(this))
	 */
	public java.lang.Object clone() {
		ECDSAPrivateKey clone = (ECDSAPrivateKey) super.clone();

		clone.ecdsaParams_ = (ByteArrayAttribute) this.ecdsaParams_.clone();
		clone.value_ = (ByteArrayAttribute) this.value_.clone();

		putAttributesInTable(clone); // put all cloned attributes into the new table

		return clone;
	}

	/**
	 * Compares all member variables of this object with the other object.
	 * Returns only true, if all are equal in both objects.
	 *
	 * @param otherObject The other object to compare to.
	 * @return True, if other is an instance of this class and all member
	 *         variables of both objects are equal. False, otherwise.
	 * @preconditions
	 * @postconditions
	 */
	public boolean equals(java.lang.Object otherObject) {
		boolean equal = false;

		if (otherObject instanceof ECDSAPrivateKey) {
			ECDSAPrivateKey other = (ECDSAPrivateKey) otherObject;
			equal = (this == other)
			    || (super.equals(other) && this.ecdsaParams_.equals(other.ecdsaParams_) && this.value_
			        .equals(other.value_));
		}

		return equal;
	}

	/**
	 * Gets the ECDSA parameters attribute of this ECDSA key.
	 *
	 * @return The ECDSA parameters attribute.
	 * @preconditions
	 * @postconditions (result <> null)
	 */
	public ByteArrayAttribute getEcdsaParams() {
		return ecdsaParams_;
	}

	/**
	 * Gets the value attribute of this ECDSA key.
	 *
	 * @return The value attribute.
	 * @preconditions
	 * @postconditions (result <> null)
	 */
	public ByteArrayAttribute getValue() {
		return value_;
	}

	/**
	 * Read the values of the attributes of this object from the token.
	 *
	 * @param session The session handle to use for reading attributes.
	 *                This session must have the appropriate rights; i.e.
	 *                it must be a user-session, if it is a private object.
	 * @exception TokenException If getting the attributes failed.
	 * @preconditions (session <> null)
	 * @postconditions
	 */
	public void readAttributes(Session session)
	    throws TokenException
	{
		super.readAttributes(session);

		Object.getAttributeValue(session, objectHandle_, ecdsaParams_);
		Object.getAttributeValue(session, objectHandle_, value_);
	}

	/**
	 * This method returns a string representation of the current object. The
	 * output is only for debugging purposes and should not be used for other
	 * purposes.
	 *
	 * @return A string presentation of this object for debugging output.
	 * @preconditions
	 * @postconditions (result <> null)
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(1024);

		buffer.append(super.toString());

		buffer.append(Constants.NEWLINE);
		buffer.append(Constants.INDENT);
		buffer.append("ECDSA Params (DER, hex): ");
		buffer.append(ecdsaParams_.toString());

		buffer.append(Constants.NEWLINE);
		buffer.append(Constants.INDENT);
		buffer.append("Private Value d (hex): ");
		buffer.append(value_.toString());

		return buffer.toString();
	}

}