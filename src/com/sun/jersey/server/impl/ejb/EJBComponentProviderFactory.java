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
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class EJBComponentProviderFactory implements
        IoCComponentProviderFactory,
        IoCComponentProcessorFactoryInitializer {
    
    private static final Logger LOGGER = Logger.getLogger(
            EJBComponentProviderFactory.class.getName());

    private final EJBInjectionInterceptor interceptor;

    public EJBComponentProviderFactory(Object interceptorBinder, Method interceptorBinderMethod) {
        this.interceptor = new EJBInjectionInterceptor();

        try {
            interceptorBinderMethod.invoke(interceptorBinder, interceptor);
        } catch (Exception ex) {
            throw new ContainerException(ex);
        }
    }

    // IoCComponentProviderFactory
    
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        return getComponentProvider(null, c);
    }

    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c) {
        if (c.isAnnotationPresent(Stateless.class)) {
            try {
                InitialContext ic = new InitialContext();

                String name = getName(c);
                Object o = ic.lookup(name);

                LOGGER.info("Binding the EJB class " + c.getName() +
                        " with the module name " + name +
                        " to EJBManagedComponentProvider");
                return new EJBManagedComponentProvider(o);
            } catch (NamingException ne) {
                throw new EJBException(ne);
            }
        } else {
            return null;
        }
    }

    private static class EJBManagedComponentProvider implements IoCFullyManagedComponentProvider {
        private final Object o;

        EJBManagedComponentProvider(Object o) {
            this.o = o;
        }

        public Object getInstance() {
            return o;
        }
    }
    
    // IoCComponentProcessorFactoryInitializer
    
    public void init(IoCComponentProcessorFactory cpf) {
        interceptor.setFactory(cpf);
    }

    private static class EJBInjectionInterceptor {
        private IoCComponentProcessorFactory cpf;

        public void setFactory(IoCComponentProcessorFactory cpf) {
            this.cpf = cpf;
        }

        @PostConstruct
        private void init(InvocationContext context) throws Exception {
            if (cpf == null) {
                // Not initialized
                return;
            }
            
            Object beanInstance = context.getTarget();

            cpf.get(beanInstance.getClass(), ComponentScope.Singleton).
                    postConstruct(beanInstance);

            // Invoke next interceptor in chain
            context.proceed();
        }
    }

    private String getName(Class<?> c) {
        Stateless s = c.getAnnotation(Stateless.class);

        String simpleName = s.name();
        if (simpleName == null || simpleName.length() == 0) {
            simpleName = c.getSimpleName();
        }

        return "java:module/" + simpleName + "!" + c.getName();
    }
}