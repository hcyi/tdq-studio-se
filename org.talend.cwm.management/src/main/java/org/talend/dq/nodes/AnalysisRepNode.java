// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class AnalysisRepNode extends RepositoryNode {

    /**
     * DOC klliu AnalysisRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public AnalysisRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {

        return buildChildren();
    }

    public List<IRepositoryNode> buildChildren() {
        List<IRepositoryNode> anaElement = new ArrayList<IRepositoryNode>();
        RepositoryNode parent = this.getParent();
        if (!(parent instanceof ReportSubFolderRepNode)) {
            AnalysisSubFolderRepNode childNodeFolder = new AnalysisSubFolderRepNode(null, this, ENodeType.SIMPLE_FOLDER);
            childNodeFolder.setProperties(EProperties.LABEL, "analyzed elements");
            childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
            anaElement.add(childNodeFolder);
        }
        return anaElement;
    }

}