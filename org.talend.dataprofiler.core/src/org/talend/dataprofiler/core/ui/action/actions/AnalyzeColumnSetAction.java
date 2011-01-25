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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.relational.MeatadataColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnSetMasterPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class AnalyzeColumnSetAction extends Action {

    private List<IRepositoryNode> catalogs = new ArrayList<IRepositoryNode>();

    TreeSelection selection;

    MeatadataColumn[] columns;

    private DBConnectionRepNode connNode = null;

    private DBCatalogRepNode cataNode = null;

    private DBSchemaRepNode schemaNode = null;

    IRepositoryNode nodeColumns;

    boolean needselection = true;

    public AnalyzeColumnSetAction() {
        super("Analyze Column Set"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
    }

    public AnalyzeColumnSetAction(MeatadataColumn[] columns) {
        super("Analyze Column Set"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
        needselection = false;
        this.columns = columns;
    }

    public AnalyzeColumnSetAction(IRepositoryNode columns) {
        super("Analyze Column Set"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
        needselection = false;
        this.nodeColumns = columns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        PackagesAnalyisParameter packaFilterParameter = new PackagesAnalyisParameter();
        DBTableFolderRepNode tFolder = (DBTableFolderRepNode) nodeColumns.getParent();
        if (tFolder != null) {
            IRepositoryNode node = tFolder.getParent();

            if (node instanceof DBCatalogRepNode) {
                IRepositoryNode connNode = ((DBCatalogRepNode) node).getParent();
                packaFilterParameter.setConnectionRepNode((DBConnectionRepNode) connNode);
                catalogs.add(node);
                packaFilterParameter.setPackages(catalogs);
            } else if (node instanceof DBSchemaRepNode) {
                schemaNode = (DBSchemaRepNode) node;
                RepositoryNode parent = schemaNode.getParent();
                if (parent instanceof DBCatalogRepNode) {
                    catalogs.add(parent);
                } else {
                    catalogs.add(schemaNode);
                }
                packaFilterParameter.setPackages(catalogs);
            }

        }
        if (opencolumnSetAnalysisDialog(packaFilterParameter) == Window.OK) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            List<IRepositoryNode> column = new ArrayList<IRepositoryNode>();
            if (editor != null) {
                ColumnSetMasterPage page = (ColumnSetMasterPage) editor.getMasterPage();
                if (this.needselection && !this.selection.isEmpty()) {
                    MeatadataColumn[] tdColumns = new MeatadataColumn[selection.size()];
                    Iterator it = this.selection.iterator();

                    int i = 0;
                    while (it.hasNext()) {
                        tdColumns[i] = (MeatadataColumn) it.next();
                        i++;
                    }
                    page.getTreeViewer().setInput(tdColumns);
                } else if (!this.needselection && null != this.nodeColumns) {
                    for (IRepositoryNode columnFolder : nodeColumns.getChildren()) {
                        if (columnFolder instanceof DBColumnFolderRepNode) {
                            column.addAll(columnFolder.getChildren());
                        }
                    }
                    page.getTreeViewer().setInput(column.toArray());
                    page.doSave(null);
                }
            }
        }
    }

    public void setColumnSelection(TreeSelection selection) {
        this.selection = selection;
    }

    private int opencolumnSetAnalysisDialog(PackagesAnalyisParameter packaFilterParameter) {
        Wizard wizard = WizardFactory.createAnalysisWizard(AnalysisType.COLUMN_SET, packaFilterParameter);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        return dialog.open();
    }

}
