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

package com.sun.jersey.server.impl.model;

import com.sun.jersey.impl.ImplMessages;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.HttpMethod;
import com.sun.jersey.api.container.ContainerException;
import java.lang.reflect.Method;

/**
 * Error helper class for reporting errors related to processing a Web resource.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class ErrorHelper {
    
    public static ContainerException objectNotAWebResource(Class resourceClass) {
        return new ContainerException(ImplMessages.OBJECT_NOT_A_WEB_RESOURCE(resourceClass.getName()));
    }
    
    public static ContainerException badClassConsumes(Exception e, Class resourceClass, Consumes c) {
        return new ContainerException(ImplMessages.BAD_CLASS_CONSUMEMIME(resourceClass,
                                                                                           c.value()), e);
    }
    
    public static ContainerException badClassProduces(Exception e, Class resourceClass, Produces p) {
        return new ContainerException(ImplMessages.BAD_CLASS_PRODUCEMIME(resourceClass,
                                                                                           p.value()), e);
    }
    
    public static ContainerException badMethodHttpMethod(Class resourceClass, Method m, HttpMethod hm) {
        return new ContainerException(ImplMessages.BAD_METHOD_HTTPMETHOD(resourceClass,
                                                                                           hm.value(),
                                                                                           m.toString()));
    }
    
    public static ContainerException badMethodConsumes(Exception e, Class resourceClass, Method m, Consumes c) {
        return new ContainerException(ImplMessages.BAD_METHOD_CONSUMEMIME(resourceClass,
                                                                                   c.value(),
                                                                                   m.toString()), e);
    }
    
    public static ContainerException badMethodProduces(Exception e, Class resourceClass, Method m, Produces p) {
        return new ContainerException(ImplMessages.BAD_METHOD_PRODUCEMIME(resourceClass,
                                                                                   p.value(),
                                                                                   m.toString()), e);
    }
}
