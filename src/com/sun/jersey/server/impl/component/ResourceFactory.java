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
package com.sun.jersey.server.impl.component;

import com.sun.jersey.server.spi.component.*;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentConstructor;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.server.impl.resource.PerRequestFactory;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ResourceFactory {
    private final ResourceConfig config;

    private final InjectableProviderContext ipc;

    public ResourceFactory(ResourceConfig config, InjectableProviderContext ipc) {
        this.config = config;
        this.ipc = ipc;
    }

    public InjectableProviderContext getInjectableProviderContext() {
        return ipc;
    }

    public ResourceComponentProvider getComponentProvider(Class c) {
        return getComponentProviderFactory(c).getComponentProvider(c);
    }

    protected ResourceComponentProviderFactory getComponentProviderFactory(Class c) {
        Class<? extends ResourceComponentProviderFactory> providerFactoryClass = null;

        // Use annotations to identify the correct provider, note that
        // @ResourceComponentProviderClass is a meta-annotation so we look for annotations
        // on the annotations of the resource class
        Annotation annotations[] = c.getAnnotations();
        for (Annotation a: annotations) {
            Class<?> annotationClass = a.annotationType();
            ResourceComponentProviderFactoryClass rf = annotationClass.getAnnotation(
                    ResourceComponentProviderFactoryClass.class);
            if (rf != null && providerFactoryClass == null)
                providerFactoryClass = rf.value();
            else if (rf != null && providerFactoryClass != null)
                throw new ContainerException(c.toString()+
                        " has multiple ResourceComponentProviderClass annotations");
        }

        if (providerFactoryClass == null) {
            Object v = config.getProperties().
                    get(ResourceConfig.PROPERTY_DEFAULT_RESOURCE_COMPONENT_PROVIDER_FACTORY_CLASS);
            if (v == null) {
                // Use default provider if none specified
                providerFactoryClass = PerRequestFactory.class;
            } else if (v instanceof Class) {
                Class<?> _c = (Class<?>)v;
                if (ResourceComponentProviderFactory.class.isAssignableFrom(_c)) {
                    providerFactoryClass = _c.asSubclass(ResourceComponentProviderFactory.class);
                } else {
                    throw new IllegalArgumentException("Property value for "
                            + ResourceConfig.PROPERTY_DEFAULT_RESOURCE_COMPONENT_PROVIDER_FACTORY_CLASS
                            + " of type " + v.getClass() + " not of a subclass of " + ResourceComponentProviderFactory.class);
                }
            } else {
                throw new IllegalArgumentException("Property value for "
                        + ResourceConfig.PROPERTY_DEFAULT_RESOURCE_COMPONENT_PROVIDER_FACTORY_CLASS
                        + " of type " + v.getClass() + " not of a subclass of " + ResourceComponentProviderFactory.class);
            }
        }

        try {
            ComponentInjector<ResourceComponentProviderFactory> ci =
                    new ComponentInjector(ipc, providerFactoryClass);

            ComponentConstructor<ResourceComponentProviderFactory> cc =
                    new ComponentConstructor(ipc, providerFactoryClass, ci);

            return cc.getInstance();
        } catch (IllegalArgumentException ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        } catch (InvocationTargetException ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        } catch (IllegalAccessException ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        } catch (InstantiationException ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        }
    }
}