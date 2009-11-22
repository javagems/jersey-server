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
package com.sun.jersey.server.wadl.generators;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.RepresentationType;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;

/**
 * This {@link WadlGenerator} adds all doc elements provided by {@link ApplicationDocs#getDocs()}
 * to the generated wadl-file.
 * <p>
 * The {@link ApplicationDocs} content can either be provided via a {@link File} reference
 * ({@link #setApplicationDocsFile(File)}) or
 * via an {@link InputStream} ({@link #setApplicationDocsStream(InputStream)}).<br/>
 * Only one at a time can be set, otherwise an {@link IllegalStateException}
 * will be thrown.
 * </p>
 * Created on: Jun 16, 2008<br>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id: WadlGeneratorApplicationDoc.java 2004 2009-02-12 23:50:05Z magrokosmos $
 */
public class WadlGeneratorApplicationDoc implements WadlGenerator {
    
    private WadlGenerator _delegate;
    private File _applicationDocsFile;
    private InputStream _applicationDocsStream;
    private ApplicationDocs _applicationDocs;

    public WadlGeneratorApplicationDoc() {
    }

    public WadlGeneratorApplicationDoc(WadlGenerator wadlGenerator, ApplicationDocs applicationDocs) {
        _delegate = wadlGenerator;
        _applicationDocs = applicationDocs;
    }

    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        _delegate = delegate;
    }

    public String getRequiredJaxbContextPath() {
        return _delegate.getRequiredJaxbContextPath();
    }

    public void setApplicationDocsFile(File applicationDocsFile) {
        if ( _applicationDocsStream != null ) {
            throw new IllegalStateException( "The applicationDocsStream property is already set," +
                    " therefore you cannot set the applicationDocsFile property. Only one of both can be set at a time." );
        }
        _applicationDocsFile = applicationDocsFile;
    }

    public void setApplicationDocsStream(InputStream applicationDocsStream) {
        if ( _applicationDocsFile != null ) {
            throw new IllegalStateException( "The applicationDocsFile property is already set," +
                    " therefore you cannot set the applicationDocsStream property. Only one of both can be set at a time." );
        }
        _applicationDocsStream = applicationDocsStream;
    }

    public void init() throws Exception {
        if ( _applicationDocsFile == null && _applicationDocsStream == null ) {
            throw new IllegalStateException( "Neither the applicationDocsFile nor the applicationDocsStream" +
                    " is set, one of both is required." );
        }
        _delegate.init();
        String name = ApplicationDocs.class.getName();
        final int i = name.lastIndexOf('.');
        name = (i != -1) ? name.substring(0, i) : "";
        final JAXBContext c = JAXBContext.newInstance(name,
                Thread.currentThread().getContextClassLoader());
        final Unmarshaller m = c.createUnmarshaller();
        final Object obj = _applicationDocsFile != null
            ? m.unmarshal( _applicationDocsFile ) : m.unmarshal( _applicationDocsStream );
        _applicationDocs = ApplicationDocs.class.cast(obj);
    }

    /**
     * @return the application
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createApplication()
     */
    public Application createApplication() {
        final Application result = _delegate.createApplication();
        if (_applicationDocs != null && _applicationDocs.getDocs() != null &&
                !_applicationDocs.getDocs().isEmpty()) {
            result.getDoc().addAll(_applicationDocs.getDocs());
        }
        return result;
    }

    /**
     * @param r
     * @param m
     * @return the method
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createMethod(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        return _delegate.createMethod(r, m);
    }

    /**
     * @param r
     * @param m
     * @param mediaType
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createRequestRepresentation(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod, javax.ws.rs.core.MediaType)
     */
    public RepresentationType createRequestRepresentation(AbstractResource r,
            AbstractResourceMethod m, MediaType mediaType) {
        return _delegate.createRequestRepresentation(r, m, mediaType);
    }

    /**
     * @param r
     * @param m
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createRequest(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Request createRequest(AbstractResource r, AbstractResourceMethod m) {
        return _delegate.createRequest(r, m);
    }

    /**
     * @param r
     * @param m
     * @param p
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createParam(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractMethod, com.sun.jersey.api.model.Parameter)
     */
    public Param createParam(AbstractResource r,
            AbstractMethod m, Parameter p) {
        return _delegate.createParam(r, m, p);
    }

    /**
     * @param r
     * @param path
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResource(com.sun.jersey.api.model.AbstractResource, java.lang.String)
     */
    public Resource createResource(AbstractResource r, String path) {
        return _delegate.createResource(r, path);
    }

    /**
     * @param r
     * @param m
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResponse(com.sun.jersey.api.model.AbstractResource, com.sun.jersey.api.model.AbstractResourceMethod)
     */
    public Response createResponse(AbstractResource r, AbstractResourceMethod m) {
        return _delegate.createResponse(r, m);
    }

    /**
     * @return
     * @see com.sun.jersey.server.impl.wadl.WadlGenerator#createResources()
     */
    public Resources createResources() {
        return _delegate.createResources();
    }
}