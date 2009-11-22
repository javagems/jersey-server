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

package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.server.wadl.WadlGenerator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfigLoader;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.util.logging.Level;
import javax.ws.rs.core.Context;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class WadlFactory {
    
    private static final Logger LOGGER = Logger.getLogger(WadlFactory.class.getName());

    private final boolean isJAXBPresent;

    private final WadlGenerator wadlGenerator;
    
    public WadlFactory(ResourceConfig resourceConfig) {
        isJAXBPresent = checkForJAXB();
        
        if (isJAXBPresent) {
            wadlGenerator = WadlGeneratorConfigLoader.loadWadlGeneratorsFromConfig(resourceConfig);
        }
        else {
            wadlGenerator = null;
        }
    }

    public boolean isSupported() {
        return isJAXBPresent;
    }

    public WadlApplicationContext createWadlApplicationContext(Set<AbstractResource> rootResources) {
        if (!isSupported()) return null;

        return new WadlApplicationContextImpl(rootResources, wadlGenerator);
    }

    public void init(InjectableProviderFactory ipf, Set<AbstractResource> rootResources) {
        if (!isSupported()) return;

        WadlApplicationContext w = new WadlApplicationContextImpl(rootResources, wadlGenerator);

        ipf.add(new SingletonTypeInjectableProvider<Context, WadlApplicationContext>(
                WadlApplicationContext.class, w) {});
    }
    
    /**
     * Create the WADL resource method for OPTIONS.
     * <p>
     * This is created using reflection so that there is no runtime
     * dependency on JAXB. If the JAXB jars are not in the class path
     * then WADL generation will not be supported.
     * 
     * @param resource the resource model
     * @return the WADL resource OPTIONS method
     */
    public ResourceMethod createWadlOptionsMethod(
            Map<String, List<ResourceMethod>> methods, 
            AbstractResource resource, PathPattern p) {
        if (!isSupported()) return null;
        
        if (p == null) {
            return new WadlMethodFactory.WadlOptionsMethod(methods, resource, null, wadlGenerator);
        } else {
            // Remove the '/' from the beginning
            String path = p.getTemplate().getTemplate().substring(1);
            return new WadlMethodFactory.WadlOptionsMethod(methods, resource, path, wadlGenerator);
        }
    }        
    
    /**
     * Check if JAXB is present in the class path
     * @return 
     * 
     * @throws java.lang.ClassNotFoundException
     */
    private static boolean checkForJAXB() {
        try {
            Class.forName("javax.xml.bind.JAXBElement");
            return true;
        } catch(ClassNotFoundException e) {
            LOGGER.log(Level.CONFIG,
                    "WADL generation is disabled " +
                    "because JAXB jars are not " + 
                    "included in the java class path. " +
                    "To enable WADL include JAXB 2.x jars in the java class path.");
            return false;
        }
    }
}
