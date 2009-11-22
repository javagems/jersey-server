/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
final class PrimitiveMapper {
    
    static final Map<Class, Class> primitiveToClassMap = 
            getPrimitiveToClassMap();
    
    static final Map<Class, Object> primitiveToDefaultValueMap = 
            getPrimitiveToDefaultValueMap();

    private static Map<Class, Class> getPrimitiveToClassMap() {
        Map<Class, Class> m = new WeakHashMap<Class, Class>();
        // Put all primitive to wrapper class mappings except
        // that for Character
        m.put(Boolean.TYPE, Boolean.class);
        m.put(Byte.TYPE, Byte.class);
        m.put(Short.TYPE, Short.class);
        m.put(Integer.TYPE, Integer.class);
        m.put(Long.TYPE, Long.class);
        m.put(Float.TYPE, Float.class);
        m.put(Double.TYPE, Double.class);
        
        return Collections.unmodifiableMap(m);
    }
    
    private static Map<Class, Object> getPrimitiveToDefaultValueMap() {
        Map<Class, Object> m = new WeakHashMap<Class, Object>();        
        m.put(Boolean.class, Boolean.valueOf(false));
        m.put(Byte.class, Byte.valueOf((byte)0));
        m.put(Short.class, Short.valueOf((short)0));
        m.put(Integer.class, Integer.valueOf(0));
        m.put(Long.class, Long.valueOf(0l));
        m.put(Float.class, Float.valueOf(0.0f));
        m.put(Double.class, Double.valueOf(0.0d));
        
        return Collections.unmodifiableMap(m);
    }
}
