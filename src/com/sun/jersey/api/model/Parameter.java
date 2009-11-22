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

package com.sun.jersey.api.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Abstraction for a method/constructor parameter
 */
public class Parameter {
    
    public enum Source {ENTITY, QUERY, MATRIX, PATH, COOKIE, HEADER, CONTEXT, UNKNOWN};
    
    private final Annotation[] annotations;
    private final Annotation annotation;
    private final Parameter.Source source;
    private final String sourceName;
    private final boolean encoded;
    private final String defaultValue;
    private final Type type;
    private final Class<?> clazz;
    
    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz) {
        this(as, a, source, sourceName, type, clazz, false, null);
    }

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, boolean encoded) {
        this(as, a, source, sourceName, type, clazz, encoded, null);
    }

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, String defaultValue) {
        this(as, a, source, sourceName, type, clazz, false, defaultValue);
    }
    
    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, boolean encoded, String defaultValue) {
        this.annotations = as;
        this.annotation = a;
        this.source = source;
        this.sourceName = sourceName;
        this.type = type;
        this.clazz = clazz;
        this.encoded = encoded;
        this.defaultValue = defaultValue;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }
    
    public Annotation getAnnotation() {
        return annotation;
    }
    
    public Parameter.Source getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public boolean isEncoded() {
        return encoded;
    }
    
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getParameterClass() {
        return clazz;
    }

    public Type getParameterType() {
        return type;
    }    
}