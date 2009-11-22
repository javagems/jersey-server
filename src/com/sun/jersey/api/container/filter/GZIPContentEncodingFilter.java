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
package com.sun.jersey.api.container.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.core.HttpHeaders;

/**
 * A GZIP content encoding filter.
 * <p>
 * If the request contains a Content-Encoding header of "gzip"
 * then the request entity (if any) is uncompressed using gzip.
 * <p>
 * If the request contains a Accept-Encoding header that contains
 * "gzip" then the response entity (if any) is compressed using gzip and a
 * Content-Encoding header of "gzip" is added to the response.
 * <p>
 * When an application is deployed as a Servlet or Filter this Jersey filter can be
 * registered using the following initialization parameters:
 * <blockquote><pre>
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;com.sun.jersey.spi.container.ContainerRequestFilters&lt;/param-name&gt;
 *         &lt;param-value&gt;com.sun.jersey.api.container.filter.GZIPContentEncodingFilter&lt;/param-value&gt;
 *     &lt;/init-param&gt
 *     &lt;init-param&gt
 *         &lt;param-name&gt;com.sun.jersey.spi.container.ContainerResponseFilters&lt;/param-name&gt;
 *         &lt;param-value&gt;com.sun.jersey.api.container.filter.GZIPContentEncodingFilter&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * </pre></blockquote>
 *
 * @author Paul.Sandoz@Sun.Com
 * @see com.sun.jersey.api.container.filter
 */
public class GZIPContentEncodingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public ContainerRequest filter(ContainerRequest request) {
        if (request.getRequestHeaders().containsKey(HttpHeaders.CONTENT_ENCODING)) {
            if (request.getRequestHeaders().getFirst(HttpHeaders.CONTENT_ENCODING).trim().equals("gzip")) {
                request.getRequestHeaders().remove(HttpHeaders.CONTENT_ENCODING);
                try {
                    request.setEntityInputStream(
                            new GZIPInputStream(request.getEntityInputStream()));
                } catch (IOException ex) {
                    throw new ContainerException(ex);
                }
            }
        }
        return request;
    }

    private static final class Adapter implements ContainerResponseWriter {
        private final ContainerResponseWriter crw;

        private GZIPOutputStream gos;

        Adapter(ContainerResponseWriter crw) {
            this.crw = crw;
        }
        
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
           gos = new GZIPOutputStream(crw.writeStatusAndHeaders(-1, response));
           return gos;
        }

        public void finish() throws IOException {
            gos.finish();
        }
    }

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (response.getEntity() != null && 
                request.getRequestHeaders().containsKey(HttpHeaders.ACCEPT_ENCODING) &&
                !response.getHttpHeaders().containsKey(HttpHeaders.CONTENT_ENCODING)) {
            if (request.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING).contains("gzip")) {
                response.getHttpHeaders().add(HttpHeaders.CONTENT_ENCODING, "gzip");
                response.setContainerResponseWriter(
                        new Adapter(response.getContainerResponseWriter()));
            }
        }        
        return response;
    }
}