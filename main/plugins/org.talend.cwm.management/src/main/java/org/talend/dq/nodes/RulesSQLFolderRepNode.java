// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class RulesSQLFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(RulesSQLFolderRepNode.class);

    /**
     * DOC klliu RulesFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public RulesSQLFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        return getChildren(false);
    }

    @Override
    public List<IRepositoryNode> getChildren(boolean withDeleted) {
        try {
            super.getChildren().clear();
            RootContainer<String, IRepositoryViewObject> tdqViewObjects = ProxyRepositoryFactory.getInstance()
                    .getTdqRepositoryViewObjects(getContentType(), RepositoryNodeHelper.getPath(this).toString());
            // sub folders
            for (Container<String, IRepositoryViewObject> container : tdqViewObjects.getSubContainer()) {
                Folder folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_RULES_SQL);
                if (!withDeleted && folder.isDeleted()) {
                    continue;
                }
                RulesSQLSubFolderRepNode childNodeFolder = new RulesSQLSubFolderRepNode(folder, this, ENodeType.SIMPLE_FOLDER);
                childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_RULES_SQL);
                childNodeFolder.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_RULES_SQL);
                super.getChildren().add(childNodeFolder);
            }
            // rule files
            for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
                if (!withDeleted && viewObject.isDeleted()) {
                    continue;
                }
                RuleRepNode repNode = new RuleRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                repNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_RULES_SQL);
                repNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_RULES_SQL);
                viewObject.setRepositoryNode(repNode);
                super.getChildren().add(repNode);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return filterResultsIfAny(super.getChildren());
    }
}
