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

package com.sun.jersey.server.impl.inject;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.util.List;

/**
 * The server context to obtain {@link Injectable} instances.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public interface ServerInjectableProviderContext extends InjectableProviderContext {
    /**
     * Get an injectable given a parameter.
     * 
     * @param p the parameter.
     * @param s the scope for which the injectable will be used
     * @return the injectable, otherwise null if an injectable could
     *         not be found.
     */
    Injectable getInjectable(Parameter p, ComponentScope s);
    
    /**
     * Get a list of injectable given a list of parameter.
     * 
     * @param ps the list of parameter.
     * @param s the scope for which the injectable will be used
     * @return the list of injectable, if an injectable for a parameter
     *         could not be found the corresponding element in the 
     *         list will be null.
     */
    List<Injectable> getInjectable(List<Parameter> ps, ComponentScope s);
}