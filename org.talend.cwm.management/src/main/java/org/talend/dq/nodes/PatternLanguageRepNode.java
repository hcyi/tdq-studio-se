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

import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class PatternLanguageRepNode extends RepositoryNode {


    private ENodeType type;

    private String label;


    public ENodeType getType() {
        return this.type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PatternLanguageRepNode(RepositoryNode parent, ENodeType type) {
        super(null, parent, type);
        this.type = type;
    }

    @Override
    public String getLabel() {
        return this.label == null ? "" : this.label;
    }
}
