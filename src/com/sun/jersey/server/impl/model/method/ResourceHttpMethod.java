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

package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.server.impl.application.ResourceMethodDispatcherFactory;
import com.sun.jersey.spi.container.ResourceFilter;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class ResourceHttpMethod extends ResourceMethod {
    private final Method m;

    public ResourceHttpMethod(
            ResourceMethodDispatcherFactory df,
            FilterFactory ff,
            AbstractResourceMethod method) {
        this(df, ff, UriTemplate.EMPTY, method);
    }
    
    public ResourceHttpMethod(
            ResourceMethodDispatcherFactory df,
            FilterFactory ff,
            UriTemplate template,
            AbstractResourceMethod method) {
        this(df, ff, ff.getResourceFilters(method), template, method);
    }

    public ResourceHttpMethod(
            ResourceMethodDispatcherFactory df,
            FilterFactory ff,
            List<ResourceFilter> resourceFilters,
            UriTemplate template,
            AbstractResourceMethod method) {
        super(method.getHttpMethod(),
                template,
                method.getSupportedInputTypes(), 
                method.getSupportedOutputTypes(),
                method.areInputTypesDeclared(),
                df.getDispatcher(method),
                ff.getRequestFilters(resourceFilters),
                ff.getResponseFilters(resourceFilters));

        this.m = method.getMethod();
        
        if (getDispatcher() == null) {
            String msg = ImplMessages.NOT_VALID_HTTPMETHOD(m,
                    method.getHttpMethod(), m.getDeclaringClass());
            throw new ContainerException(msg);
        }
    }
    
    @Override
    public String toString() {
        return ImplMessages.RESOURCE_METHOD(m.getDeclaringClass(), m.getName());
    }
}