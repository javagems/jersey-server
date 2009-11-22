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

package com.sun.jersey.server.impl.template;

import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * A viewable rule that defers the request to a template. If a template
 * is not available then the rule is not accepted.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public class ViewableRule implements UriRule {
    private final List<QualitySourceMediaType> priorityMediaTypes;

    private final List<ContainerRequestFilter> requestFilters;

    private final List<ContainerResponseFilter> responseFilters;

    @Context TemplateContext tc;

    public ViewableRule(
            List<QualitySourceMediaType> priorityMediaTypes,
            List<ContainerRequestFilter> requestFilters,
            List<ContainerResponseFilter> responseFilters) {
        this.priorityMediaTypes = priorityMediaTypes;
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }

    public final boolean accept(CharSequence path, Object resource, UriRuleContext context) {
        final HttpRequestContext request = context.getRequest();
        // Only accept GET requests
        if (!request.getMethod().equals("GET"))
            return false;
        
        // Obtain the template path
        final String templatePath = (path.length() > 0) ? 
            context.getMatchResult().group(1) :
            "";

        // Resolve the viewable
        Viewable v = new Viewable(templatePath, resource);
        ResolvedViewable rv = tc.resolveViewable(v);
        if (rv == null)
            return false;

        // Push the response filters
        context.pushContainerResponseFilters(responseFilters);

        // Process the request filter
        if (!requestFilters.isEmpty()) {
            ContainerRequest containerRequest = context.getContainerRequest();
            for (ContainerRequestFilter f : requestFilters) {
                containerRequest = f.filter(containerRequest);
                context.setContainerRequest(containerRequest);
            }
        }

        final HttpResponseContext response = context.getResponse();

        response.setStatus(200);
        
        response.setEntity(rv);

        if (!response.getHttpHeaders().containsKey("Content-Type")) {
            MediaType contentType = getContentType(request, response);
            response.getHttpHeaders().putSingle("Content-Type", contentType);
        }

        return true;
    }

    private MediaType getContentType(HttpRequestContext request, HttpResponseContext response) {
        List<MediaType> accept = (priorityMediaTypes == null)
                ? request.getAcceptableMediaTypes()
                : request.getAcceptableMediaTypes(priorityMediaTypes);
        if (!accept.isEmpty()) {
            MediaType contentType = accept.get(0);

            if (!contentType.isWildcardType() && !contentType.isWildcardSubtype()) {
                return contentType;
            }
        }
        return null;
    }
}