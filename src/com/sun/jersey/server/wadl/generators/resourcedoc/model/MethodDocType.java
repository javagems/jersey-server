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
 * The documentation type for methods.<br>
 * Created on: Jun 12, 2008<br>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "methodDoc", propOrder = {

})
public class MethodDocType {
    
    private String methodName;

    protected String commentText;

    /**
     * Gets the value of the commentText property.
     * 
     * @return the commentText
     *     
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the value of the commentText property.
     * 
     * @param value the commentText
     *     
     */
    public void setCommentText(String value) {
        this.commentText = value;
    }
    
    private String returnDoc;
    private String returnTypeExample;

    private RequestDocType requestDoc;
    private ResponseDocType responseDoc;

    @XmlElementWrapper(name = "paramDocs")
    protected List<ParamDocType> paramDoc;

    public List<ParamDocType> getParamDocs() {
        if (paramDoc == null) {
            paramDoc = new ArrayList<ParamDocType>();
        }
        return this.paramDoc;
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
     * @return the className
     * @author Martin Grotzke
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the className to set
     * @author Martin Grotzke
     */
    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }

    /**
     * @return the returnDoc
     * @author Martin Grotzke
     */
    public String getReturnDoc() {
        return returnDoc;
    }

    /**
     * @param returnDoc the returnDoc to set
     * @author Martin Grotzke
     */
    public void setReturnDoc( String returnDoc ) {
        this.returnDoc = returnDoc;
    }

    /**
     * @return the returnTypeExample
     * @author Martin Grotzke
     */
    public String getReturnTypeExample() {
        return returnTypeExample;
    }

    /**
     * @param returnTypeExample the returnTypeExample to set
     * @author Martin Grotzke
     */
    public void setReturnTypeExample( String returnTypeExample ) {
        this.returnTypeExample = returnTypeExample;
    }

    /**
     * @return the requestDoc
     * @author Martin Grotzke
     */
    public RequestDocType getRequestDoc() {
        return requestDoc;
    }

    /**
     * @param requestDoc the requestDoc to set
     * @author Martin Grotzke
     */
    public void setRequestDoc( RequestDocType requestDoc ) {
        this.requestDoc = requestDoc;
    }

    /**
     * @return the responseDoc
     * @author Martin Grotzke
     */
    public ResponseDocType getResponseDoc() {
        return responseDoc;
    }

    /**
     * @param responseDoc the responseDoc to set
     * @author Martin Grotzke
     */
    public void setResponseDoc( ResponseDocType responseDoc ) {
        this.responseDoc = responseDoc;
    }
    
}
