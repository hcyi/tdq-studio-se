// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.pattern.actions;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.ExportPatternsWizard;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportPatternsAction extends Action {

    protected static Logger log = Logger.getLogger(ExportPatternsAction.class);

    private IFolder folder;

    private boolean isForExchange;

    /**
     * DOC zqin ExportPatternsAction constructor comment.
     */
    public ExportPatternsAction(IFolder folder, boolean isForExchange) {
        if (isForExchange) {
            setText("Export for Talend Exchange");
        } else {
            setText(DefaultMessagesImpl.getString("ExportPatternsAction.exportPattern")); //$NON-NLS-1$
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.PATTERN_REG));

        this.folder = folder;
        this.isForExchange = isForExchange;
    }

    @Override
    public void run() {

        ExportPatternsWizard wizard = new ExportPatternsWizard(folder, isForExchange);
        WizardDialog dialog = null;
        // MOD hcheng 2009-07-07,for 8122.Add an help file in the "Export patterns for Talend exchange wizard".
        if (isForExchange) {
            IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getPatternHelpContextID());
            IHelpResource[] relatedTopics = context.getRelatedTopics();
            String href = relatedTopics[3].getHref();
            dialog = new OpeningHelpWizardDialog(null, wizard, href);
        } else {
            // MOD yyi 2009-08-20,for 8689.Add help in "export pattern" wizard.
            IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getPatternHelpContextID());
            IHelpResource[] relatedTopics = context.getRelatedTopics();
            String href = relatedTopics[4].getHref();
            dialog = new OpeningHelpWizardDialog(null, wizard, href);
        }
        wizard.setWindowTitle(getText());
        if (WizardDialog.OK == dialog.open()) {
            try {
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
    }

}
