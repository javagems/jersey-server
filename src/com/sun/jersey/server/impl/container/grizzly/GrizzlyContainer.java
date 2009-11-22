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

package com.sun.jersey.server.impl.container.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;
import java.io.OutputStream;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.core.header.InBoundHeaders;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marc.Hadley@Sun.Com
 */
public final class GrizzlyContainer extends GrizzlyAdapter implements ContainerListener {
    
    private WebApplication application;
    
    public GrizzlyContainer(WebApplication app) throws ContainerException {
        this.application = app;
    }

    private final static class Writer implements ContainerResponseWriter {
        final GrizzlyResponse response;
        
        Writer(GrizzlyResponse response) {
            this.response = response;
        }
        
        public OutputStream writeStatusAndHeaders(long contentLength, 
                ContainerResponse cResponse) throws IOException {
            response.setStatus(cResponse.getStatus());

            if (contentLength != -1 && contentLength < Integer.MAX_VALUE) 
                response.setContentLength((int)contentLength);

            for (Map.Entry<String, List<Object>> e : cResponse.getHttpHeaders().entrySet()) {
                for (Object value : e.getValue()) {
                    response.addHeader(e.getKey(), ContainerResponse.getHeaderValue(value));
                }
            }

            String contentType = response.getHeader("Content-Type");
            if (contentType != null) {
                response.setContentType(contentType);
            }
                
            return response.getOutputStream();
        }
        
        public void finish() throws IOException {            
        }
    }
    
    public void service(GrizzlyRequest request, GrizzlyResponse response) {
        WebApplication _application = application;
        
        final URI baseUri = getBaseUri(request);
        /*
         * request.unparsedURI() is a URI in encoded form that contains
         * the URI path and URI query components.
         */
        final URI requestUri = baseUri.resolve(
                request.getRequest().unparsedURI().toString());
        
        try {
            final ContainerRequest cRequest = new ContainerRequest(
                    _application, 
                    request.getMethod(), 
                    baseUri, 
                    requestUri, 
                    getHeaders(request), 
                    request.getInputStream());
            
            _application.handleRequest(cRequest, new Writer(response));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void afterService(GrizzlyRequest request, GrizzlyResponse response) 
            throws Exception {
    }

    private URI getBaseUri(GrizzlyRequest request) {
        try {
            return new URI(
                    request.getScheme(), 
                    null, 
                    request.getServerName(), 
                    request.getServerPort(), 
                    "/", 
                    null, null);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private InBoundHeaders getHeaders(GrizzlyRequest request) {
        InBoundHeaders rh = new InBoundHeaders();
        
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String value = request.getHeader(name);
            rh.add(name, value);
        }
        
        return rh;
    }
    
    // ContainerListener
    
    public void onReload() {
        WebApplication oldApplication = application;
        application = application.clone();
        oldApplication.destroy();
    }    
}