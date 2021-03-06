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
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * The documentation type for classes.<br>
 * Created on: Jun 12, 2008<br>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classDoc", propOrder = {

})
public class ClassDocType {
    
    private String className;
    private String commentText;

    @XmlElementWrapper(name = "methodDocs")
    private List<MethodDocType> methodDoc;

    public List<MethodDocType> getMethodDocs() {
        if (methodDoc == null) {
            methodDoc = new ArrayList<MethodDocType>();
        }
        return this.methodDoc;
    }

    @XmlAnyElement(lax = true)
    private List<Object> any;
    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * @return the commentText
     * @author Martin Grotzke
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * @param commentText the commentText to set
     * @author Martin Grotzke
     */
    public void setCommentText( String commentText ) {
        this.commentText = commentText;
    }

    /**
     * @return the className
     * @author Martin Grotzke
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     * @author Martin Grotzke
     */
    public void setClassName( String className ) {
        this.className = className;
    }
    
}
