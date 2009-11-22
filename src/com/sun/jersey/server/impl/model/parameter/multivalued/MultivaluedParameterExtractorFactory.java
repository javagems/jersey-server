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

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class MultivaluedParameterExtractorFactory implements MultivaluedParameterExtractorProvider {

    private final StringReaderWorkers w;

    public MultivaluedParameterExtractorFactory(StringReaderWorkers w) {
        this.w = w;
    }

    public MultivaluedParameterExtractor getWithoutDefaultValue(Parameter p) {
        return process(
                w,
                null,
                p.getParameterClass(),
                p.getParameterType(),
                p.getAnnotations(),
                p.getSourceName());
    }

    public MultivaluedParameterExtractor get(Parameter p) {
        return process(
                w,
                p.getDefaultValue(),
                p.getParameterClass(),
                p.getParameterType(),
                p.getAnnotations(),
                p.getSourceName());
    }

    private MultivaluedParameterExtractor process(
            StringReaderWorkers w,
            String defaultValue,
            Class<?> parameter,
            Type parameterType,
            Annotation[] annotations,
            String parameterName) {

        if (parameter == List.class ||
                parameter == Set.class ||
                parameter == SortedSet.class) {
            // Get the generic type of the list
            // If none default to String
            Class c = ReflectionHelper.getGenericClass(parameterType);
            if (c == null || c == String.class) {
                return CollectionStringExtractor.getInstance(
                        parameter, parameterName, defaultValue);
            } else {
                final StringReader sr = w.getStringReader(c, c, annotations);
                if (sr == null)
                    return null;

                try {
                    return CollectionStringReaderExtractor.getInstance(
                            parameter, sr, parameterName, defaultValue);
                } catch (Exception e) {
                    throw new ContainerException("Could not process parameter type " + parameter, e);
                }
            }
        } else if (parameter == String.class) {
            return new StringExtractor(parameterName, defaultValue);
        } else if (parameter.isPrimitive()) {
            // Convert primitive to wrapper class
            parameter = PrimitiveMapper.primitiveToClassMap.get(parameter);
            if (parameter == null) {
                // Primitive type not supported
                return null;
            }

            // Check for static valueOf(String )
            Method valueOf = ReflectionHelper.getValueOfStringMethod(parameter);
            if (valueOf != null) {
                try {
                    Object defaultDefaultValue = PrimitiveMapper.primitiveToDefaultValueMap.get(parameter);
                    return new PrimitiveValueOfExtractor(valueOf, parameterName,
                            defaultValue, defaultDefaultValue);
                } catch (Exception e) {
                    throw new ContainerException(ImplMessages.DEFAULT_COULD_NOT_PROCESS_METHOD(defaultValue, valueOf));
                }
            }

        } else {
            final StringReader sr = w.getStringReader(parameter, parameterType, annotations);
            if (sr == null)
                return null;

            try {
                return new StringReaderExtractor(sr, parameterName, defaultValue);
            } catch (Exception e) {
                throw new ContainerException("Could not process parameter type " + parameter, e);
            }
        }

        return null;
    }

}
