// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseAction extends Action {

    private static Logger log = Logger.getLogger(ReloadDatabaseAction.class);

    private Object selectedObject;

    public ReloadDatabaseAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.UPDATE_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(selectedObject);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {
                            creatComparisonLevel.reloadCurrentLevelElement();
                        } catch (ReloadCompareException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        try {
            ProgressUI.popProgressDialog(op, shell);
            ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
        } catch (InvocationTargetException e) {
            MessageDialog.openInformation(shell, Messages.getString("ReloadDatabaseAction.connectionFailured"), Messages.getString("ReloadDatabaseAction.checkConnectionFailured") + e.getCause().getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }

    }
}
