/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spring.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

final class DigestAuthUtils {

	public static String encodePasswordInA1Format(String username, String realm, String password) {
	    
		String a1 = username + ":" + realm + ":" + password;

		return md5Hex(a1);
	}

	private static String md5Hex(String data) {
	    
		MessageDigest digest;
		
		try {
			digest = MessageDigest.getInstance("MD5");
			
		} catch (NoSuchAlgorithmException e) {
		    
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(data.getBytes())));
	}
}
