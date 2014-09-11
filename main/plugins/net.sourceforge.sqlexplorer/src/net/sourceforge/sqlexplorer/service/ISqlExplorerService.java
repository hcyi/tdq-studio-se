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
package net.sourceforge.sqlexplorer.service;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.talend.core.model.properties.Item;

/**
 * Add qiongli class global comment. create item and propropty file.
 */
public interface ISqlExplorerService extends IService {

    public Item createFile(String content, IPath path, String label, String extension);

    public IProject getRootProject();
}
