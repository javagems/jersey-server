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

package com.sun.jersey.api.core;

import com.sun.jersey.server.impl.container.config.AnnotatedClassScanner;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * A mutable implementation of {@link DefaultResourceConfig} that dynamically 
 * searches for root resource and provider classes in a given a set of
 * declared package and in all (if any) sub-packages of those declared packages.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public class PackagesResourceConfig extends DefaultResourceConfig {
    /**
     * The property value MUST be an instance String or String[]. Each String
     * instance represents one or more package names that MUST be separated by ';'.
     */
    public static final String PROPERTY_PACKAGES
            = "com.sun.jersey.config.property.packages";
    
    private static final Logger LOGGER = 
            Logger.getLogger(PackagesResourceConfig.class.getName());

    private final String[] packages;
    
    /**
     * Search for root resource classes declaring the packages as an 
     * array of package names.
     * 
     * @param packages the array package names.
     */
    public PackagesResourceConfig(String... packages) {
        if (packages == null || packages.length == 0)
            throw new IllegalArgumentException("Array of packages must not be null or empty");
        
        this.packages = packages.clone();
        init(packages);
    }

    /**
     * Search for root resource classes declaring the packages as a
     * property of {@link ResourceConfig}.
     * 
     * @param props the property bag that contains the property 
     *        {@link PackagesResourceConfig#PROPERTY_PACKAGES}. 
     */
    public PackagesResourceConfig(Map<String, Object> props) {
        this(getPackages(props));
        
        setPropertiesAndFeatures(props);
    }
    
    /**
     * Perform a new search for resource classes and provider classes.
     */
    public void reload() {
        getClasses().clear();
        init(packages);
    }
    
    private void init(String[] packages) {
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the packages:");
            for (String p : packages)
                b.append('\n').append("  ").append(p);
            
            LOGGER.log(Level.INFO, b.toString());
        }
        
        AnnotatedClassScanner scanner = new AnnotatedClassScanner(
                Path.class, Provider.class);
        scanner.scan(packages);        
        
        getClasses().addAll(scanner.getMatchingClasses());
        
        if (LOGGER.isLoggable(Level.INFO) && !getClasses().isEmpty()) {
            StringBuilder b = new StringBuilder();
            b.append("Root resource classes found:");
            for (Class c : getClasses())
                if (c.isAnnotationPresent(Path.class))
                    b.append('\n').append("  ").append(c);
            
            LOGGER.log(Level.INFO, b.toString());
            
            b = new StringBuilder();
            b.append("Provider classes found:");
            for (Class c : getClasses())
                if (c.isAnnotationPresent(Provider.class))
                    b.append('\n').append("  ").append(c);
            
            LOGGER.log(Level.INFO, b.toString());            
        }
    }
    
    private static String[] getPackages(Map<String, Object> props) {
        Object v = props.get(PROPERTY_PACKAGES);
        if (v == null)
            throw new IllegalArgumentException(PROPERTY_PACKAGES + 
                    " property is missing");
        
        String[] packages = getPackages(v);
        if (packages.length == 0)
            throw new IllegalArgumentException(PROPERTY_PACKAGES + 
                    " contains no packages");
        
        return packages;
    }
    
    private static String[] getPackages(Object param) {
        if (param instanceof String) {
            return getElements(new String[] { (String)param });
        } else if (param instanceof String[]) {
            return getElements((String[])param);
        } else {
            throw new IllegalArgumentException(PROPERTY_PACKAGES + " must " +
                    "have a property value of type String or String[]");
        }
    }
}