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

package com.sun.jersey.server.impl;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.header.OutBoundHeaders;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class ResponseImpl extends Response {
    private final int status;

    private final Object entity;

    private final Type entityType;
    
    private final Object[] values;
    
    private final List<Object> nameValuePairs;
    
    private MultivaluedMap<String, Object> headers;
    
    ResponseImpl(int status, Object entity, Type entityType,
            Object[] values, List<Object> nameValuePairs) {
        this.status = status;
        this.entity = entity;
        this.entityType = entityType;
        this.values = values;
        this.nameValuePairs = nameValuePairs;
    }

    public Type getEntityType() {
        return entityType;
    }
    
    // Response 
    
    public Object getEntity() {
        return entity;
    }

    public int getStatus() {
        return status;
    }

    public MultivaluedMap<String, Object> getMetadata() {
        if (headers != null)
            return headers;
        
        headers = new OutBoundHeaders();
        
        for (int i = 0; i < values.length; i++)
            if (values[i] != null)
                headers.putSingle(ResponseBuilderImpl.getHeader(i), values[i]);

        Iterator i = nameValuePairs.iterator();
        while (i.hasNext()) {
            headers.add((String)i.next(), i.next());
        }
        
        return headers;
    }
    
    public MultivaluedMap<String, Object> getMetadataOptimal(
            HttpRequestContext request) {
        if (headers != null)
            return headers;
        
        headers = new OutBoundHeaders();

        for (int i = 0; i < values.length; i++) {
            switch(i) {
                case ResponseBuilderImpl.CONTENT_TYPE:
                    if (values[i] != null)
                        headers.putSingle(ResponseBuilderImpl.getHeader(i), values[i]);
                    break;
                case ResponseBuilderImpl.LOCATION:
                    Object location = values[i];
                    if (location != null) {
                        if (location instanceof URI) {
                            final URI locationUri = (URI)location;
                            if (!locationUri.isAbsolute()) {
                                final URI base = (status == 201) 
                                        ? request.getAbsolutePath() 
                                        : request.getBaseUri();
                                location = UriBuilder.fromUri(base).
                                        path(locationUri.getRawPath()).
                                        replaceQuery(locationUri.getRawQuery()).
                                        fragment(locationUri.getRawFragment()).
                                        build();
                            }
                        }
                        headers.putSingle(ResponseBuilderImpl.getHeader(i), location);
                    }
                    break;
                default:
                    if (values[i] != null)
                        headers.putSingle(ResponseBuilderImpl.getHeader(i), values[i]);
            }
        }

        Iterator i = nameValuePairs.iterator();
        while (i.hasNext()) {
            headers.add((String)i.next(), i.next());
        }
        
        return headers;
    }
}