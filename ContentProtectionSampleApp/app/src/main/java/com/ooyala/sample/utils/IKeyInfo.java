/**
 * **********************************************************************
 * Copyright 2011, Adobe Systems Incorporated
 * All rights reserved.
 *  
 * Permission to copy, use, modify and distribute this software is granted provided
 * this copyright notice appears in all copies. This software is provided "as is"
 * without express or implied warranty, and with no claim as to its suitability for
 * any purpose.
 * ************************************************************************
 */
package com.ooyala.sample.utils;

import java.security.PrivateKey;

public interface IKeyInfo {
    public PrivateKey getPrivateKey();
    public boolean isValid();
}
