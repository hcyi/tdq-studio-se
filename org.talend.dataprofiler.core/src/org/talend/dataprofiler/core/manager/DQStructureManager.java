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
package org.talend.dataprofiler.core.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.context.Context;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.record.RecordFile;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    protected static Logger log = Logger.getLogger(DQStructureManager.class);

    private static final String DEMO_PATH = "/demo"; //$NON-NLS-1$

    public static final String RULES_PATH = "/dqrules"; //$NON-NLS-1$

    private static final String PATTERN_PATH = "/patterns"; //$NON-NLS-1$

    private static final String SQL_LIKE_PATH = "/sql_like";//$NON-NLS-1$

    public static final String SYSTEM_INDICATOR_PATH = "/indicators";//$NON-NLS-1$

    public static final String PREFIX_TDQ = "TDQ_"; //$NON-NLS-1$

    private static DQStructureManager manager;

    public static DQStructureManager getInstance() {
        if (manager == null) {
            manager = new DQStructureManager();
        }

        return manager;
    }

    /**
     * DOC bZhou DQStructureManager constructor comment.
     */
    private DQStructureManager() {
        ResourceService.refreshStructure();
    }

    /**
     * DOC bZhou Comment method "getCurrentProject".
     * 
     * @return
     */
    public Project getCurrentProject() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        return (Project) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
    }

    /**
     * DOC bZhou Comment method "createDQStructure".
     */
    public void createDQStructure() {

        Plugin plugin = CorePlugin.getDefault();
        try {

            IProject project = ResourceManager.getRootProject();
            if (!project.exists()) {
                project = createNewProject(ResourceManager.getRootProjectName());

            }

            if (!project.getFolder(EResourceConstant.DB_CONNECTIONS.getPath()).exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.DB_CONNECTIONS.getName());
            }

            if (!project.getFolder(EResourceConstant.MDM_CONNECTIONS.getPath()).exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.MDM_CONNECTIONS.getName());
            }

            ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING, Path.EMPTY,
                    EResourceConstant.ANALYSIS.getName());

            if (!ReponsitoryContextBridge.isDefautProject()) {
                Folder reportFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING,
                        Path.EMPTY, EResourceConstant.REPORTS.getName());
            }

            Folder patternFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.PATTERNS.getName());

            Folder rulesFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.RULES.getName());
            Folder rulesSQLFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_RULES, Path.EMPTY,
                    EResourceConstant.RULES_SQL.getName());

            Folder exchangeFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.EXCHANGE.getName());

            Folder indicatorFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.INDICATORS.getName());
            Folder systemIndicatorFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_INDICATORS,
                    Path.EMPTY, EResourceConstant.SYSTEM_INDICATORS.getName());

            Folder udiFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_INDICATORS, Path.EMPTY,
                    EResourceConstant.USER_DEFINED_INDICATORS.getName());

            Folder jrxmlFolder = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.JRXML_TEMPLATE.getName());

            Folder patternRegexFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_PATTERNS,
                    Path.EMPTY, EResourceConstant.PATTERN_REGEX.getName());

            Folder patternSQLFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_PATTERNS,
                    Path.EMPTY, EResourceConstant.PATTERN_SQL.getName());

            Folder sourceFileFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.SOURCE_FILES.getName());

            // use the tos create folder API
            copyFilesToFolder(plugin, SYSTEM_INDICATOR_PATH, true, systemIndicatorFoler, null,
                    ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
            copyFilesToFolder(plugin, PATTERN_PATH, true, patternRegexFoler, null, ERepositoryObjectType.TDQ_PATTERN_REGEX);
            copyFilesToFolder(plugin, SQL_LIKE_PATH, true, patternSQLFoler, null, ERepositoryObjectType.TDQ_PATTERN_SQL);
            copyFilesToFolder(plugin, DEMO_PATH, true, sourceFileFoler, null, ERepositoryObjectType.TDQ_SOURCE_FILES);
            copyFilesToFolder(plugin, RULES_PATH, true, rulesSQLFoler, null, ERepositoryObjectType.TDQ_RULES_SQL);

            WorkspaceVersionHelper.storeVersion();

            ResourceService.refreshStructure();

        } catch (Exception ex) {
            ExceptionHandler.process(ex);
            ProxyRepositoryManager.getInstance().save();
        }
    }

    /**
     * 
     * DOC klliu Comment method "copyFilesToFolder".
     * 
     * @param plugin
     * @param srcPath
     * @param recurse
     * @param desFolder
     * @param suffix
     * @param type
     * @throws IOException
     * @throws CoreException
     */
    public void copyFilesToFolder(Plugin plugin, String srcPath, boolean recurse, Folder desFolder, String suffix,
            ERepositoryObjectType type) throws IOException, CoreException {
        if (plugin == null) {
            return;
        }

        IProject project = ResourceManager.getRootProject();
        Enumeration paths = null;
        paths = plugin.getBundle().getEntryPaths(srcPath);
        if (paths == null) {
            return;
        }

        while (paths.hasMoreElements()) {
            String nextElement = (String) paths.nextElement();
            String currentPath = "/" + nextElement; //$NON-NLS-1$
            URL resourceURL = plugin.getBundle().getEntry(currentPath);
            URL fileURL = null;
            File file = null;
            try {
                fileURL = FileLocator.toFileURL(resourceURL);
                file = new File(fileURL.getFile());
                if (file.isDirectory() && recurse) {
                    if (file.getName().startsWith(".")) { //$NON-NLS-1$
                        continue;
                    }
                    Folder folder = null;
                    if (!project.getFolder(file.getName()).exists()) {
                        folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                    } else {
                        IPath fullPath = new Path(file.getPath());
                        int count = fullPath.segmentCount();
                        FolderItem folderItem = ProxyRepositoryFactory.getInstance().getFolderItem(
                                ProjectManager.getInstance().getCurrentProject(), type, fullPath.removeFirstSegments(count - 1));

                        if (folderItem == null) {
                            folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                        } else {
                            folder = new Folder(folderItem.getProperty(), type);
                        }
                    }
                    copyFilesToFolder(plugin, currentPath, recurse, folder, suffix, type);
                    continue;
                }

                if (suffix != null && !file.getName().endsWith(suffix)) {
                    continue;
                }

                String fileName = new Path(fileURL.getPath()).lastSegment();
                InputStream openStream = null;
                openStream = fileURL.openStream();
                String folderName = null;

                if (type.equals(ERepositoryObjectType.TDQ_PATTERNS)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                }
                if (type.equals(ERepositoryObjectType.TDQ_RULES_SQL)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                } else if (type.equals(ERepositoryObjectType.TDQ_SOURCE_FILES)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                } else {
                    folderName = ERepositoryObjectType.getFolderName(type) + "/" + desFolder.getLabel();
                }
                if (folderName.equals("date")) {
                    continue;
                }
                IFolder folder = project.getFolder(folderName);
                if (type.equals(ERepositoryObjectType.TDQ_SOURCE_FILES)) {
                    String name = file.getName();
                    int indexOf = name.indexOf(".");
                    String label = name.substring(0, indexOf);
                    String extendtion = name.substring(indexOf + 1);
                    createSourceFileItem(file, Path.EMPTY, label, extendtion);
                } else {
                    copyFileToFolder(openStream, fileName, folder);
                }
            } catch (IOException e) {
                log.error(e, e);
            } catch (PersistenceException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * 
     * DOC klliu Comment method "copyFileToFolder".
     * 
     * @param inputStream
     * @param fileName
     * @param folder
     * @throws CoreException
     * @throws IOException
     */
    private void copyFileToFolder(InputStream inputStream, String fileName, IFolder folder) throws CoreException, IOException {
        if (inputStream == null) {
            return;
        }
        IFile element = folder.getFile(fileName);
        if (!element.exists()) {
            element.create(inputStream, false, null);
            ModelElement modelElement = ModelElementFileFactory.getModelElement(element);
            if (modelElement != null) {
                AElementPersistance writer = ElementWriterFactory.getInstance().create(element.getFileExtension());
                if (writer != null) {
                    writer.create(modelElement, folder);
                }
            }
        }
    }

    private TDQSourceFileItem createSourceFileItem(File initFile, IPath path, String label, String extension) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setVersion(VersionUtils.DEFAULT_VERSION);
        property.setStatusCode(""); //$NON-NLS-1$
        property.setLabel(label);

        TDQSourceFileItem sourceFileItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE
                .createTDQSourceFileItem();
        sourceFileItem.setProperty(property);
        sourceFileItem.setName(label);
        sourceFileItem.setExtension(extension);

        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        try {
            byteArray.setInnerContentFromFile(initFile);
        } catch (IOException e) {
            ExceptionHandler.process(e);
        }
        sourceFileItem.setContent(byteArray);
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            property.setId(repositoryFactory.getNextId());
            repositoryFactory.create(sourceFileItem, path);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return sourceFileItem;
    }

    /**
     * Method "isNeedCreateStructure" created by bzhou@talend.com.
     * 
     * @return true if need to create new resource structure.
     */
    public boolean isNeedCreateStructure() {
        if (isSecludedVersion()) {
            return !ResourceService.checkSecludedResource();
        }

        return !ResourceService.checkResource();
    }

    /**
     * DOC bZhou Comment method "isNeedMigration".
     * 
     * @return
     */
    public boolean isNeedMigration() {
        if (isSecludedVersion()) {
            return true;
        }

        ProductVersion wVersion = WorkspaceVersionHelper.getVesion();
        ProductVersion cVersion = CorePlugin.getDefault().getProductVersion();
        return wVersion.compareTo(cVersion) < 0;
    }

    /**
     * Method "isSecludedVersion" created by bzhou@talend.com.
     * 
     * @return true if version is before 3.0.0
     */
    private boolean isSecludedVersion() {
        return !WorkspaceVersionHelper.getVersionFile().exists();
    }

    /**
     * Creates a new project resource with the special name.MOD mzhao 2009-03-18 make this method as public.For
     * {@link org.talend.dataprofiler.core.migration.impl.TDCPFolderMergeTask} use.
     * 
     * @return the created project resource, or <code>null</code> if the project was not created
     * @throws InterruptedException
     * @throws InvocationTargetException
     * @throws CoreException
     */
    public IProject createNewProject(String projectName) throws InvocationTargetException, InterruptedException, CoreException {

        // get a project handle
        final IProject newProjectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());

        // create the new project operation
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                CreateProjectOperation createProjOp = new CreateProjectOperation(description,
                        DefaultMessagesImpl.getString("DQStructureManager.createDataProfile")); //$NON-NLS-1$
                try {
                    PlatformUI.getWorkbench().getOperationSupport().getOperationHistory()
                            .execute(createProjOp, monitor, WorkspaceUndoUtil.getUIInfoAdapter(null));
                } catch (ExecutionException e) {
                    throw new InvocationTargetException(e);
                }
            }
        };

        // run the new project creation o`peration
        // try {
        // MOD mzhao 2009-03-16 Feature 6066 First check whether project with
        // this name already exist or not. For TDCP
        // launching,
        // project always exist.
        if (!newProjectHandle.exists()) {
            ProgressUI.popProgressDialog(op);
        }
        return newProjectHandle;
    }

    /**
     * DOC bzhou Comment method "createNewFolder".
     * 
     * @param parent
     * @param constant
     * @return
     * @throws CoreException
     * @deprecated
     */
    public IFolder createNewFolder(IContainer parent, EResourceConstant constant) throws CoreException {
        return createNewFolder(parent, constant.getName());
    }

    /**
     * Method "createNewFolder" creates a new folder.
     * 
     * @param parent
     * @param folderName
     * @return
     * @throws CoreException
     * @deprecated
     */
    public IFolder createNewFolder(IContainer parent, String folderName) throws CoreException {
        IFolder desFolder = null;

        if (parent instanceof IProject) {
            desFolder = ((IProject) parent).getFolder(folderName);
        } else if (parent instanceof IFolder) {
            desFolder = ((IFolder) parent).getFolder(folderName);
        }
        assert desFolder != null;

        if (!desFolder.exists()) {
            desFolder.create(false, true, null);
        }
        return desFolder;
    }

    /**
     * Copy the files from srcPath to destination folder.
     * 
     * @param srcPath The path name in which to look. The path is always relative to the root of this bundle and may
     * begin with &quot;/&quot;. A path value of &quot;/&quot; indicates the root of this bundle.
     * @param srcPath
     * @param recurse If <code>true</code>, recurse into subdirectories(contains directories). Otherwise only return
     * entries from the specified path.
     * @param desFolder
     * @throws IOException
     * @throws CoreException
     */
    @SuppressWarnings("unchecked")
    public void copyFilesToFolder(Plugin plugin, String srcPath, boolean recurse, IFolder desFolder, String suffix)
            throws IOException, CoreException {
        if (plugin == null) {
            return;
        }

        Enumeration paths = null;
        paths = plugin.getBundle().getEntryPaths(srcPath);
        if (paths == null) {
            return;
        }
        while (paths.hasMoreElements()) {
            String nextElement = (String) paths.nextElement();
            String currentPath = "/" + nextElement; //$NON-NLS-1$
            URL resourceURL = plugin.getBundle().getEntry(currentPath);
            URL fileURL = null;
            File file = null;
            try {
                fileURL = FileLocator.toFileURL(resourceURL);
                file = new File(fileURL.getFile());
                if (file.isDirectory() && recurse) {
                    if (file.getName().startsWith(".")) { //$NON-NLS-1$
                        continue;
                    }
                    IFolder folder = desFolder.getFolder(file.getName());
                    if (!folder.exists()) {
                        folder.create(true, true, null);
                    }

                    copyFilesToFolder(plugin, currentPath, recurse, folder, suffix);
                    continue;
                }

                if (suffix != null && !file.getName().endsWith(suffix)) {
                    continue;
                }

                String fileName = new Path(fileURL.getPath()).lastSegment();
                InputStream openStream = null;
                openStream = fileURL.openStream();
                copyFileToFolder(openStream, fileName, desFolder);
            } catch (IOException e) {
                log.error(e, e);
            }
        }

    }

    public boolean isPathValid(IPath path, String label) {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
        IFolder newFolder = folder.getFolder(label);
        return !newFolder.exists();
    }

    /**
     * ADD mzhao 15750 , build dq metadata tree, get connection root node.
     */

    public List<IRepositoryNode> getConnectionRepositoryNodes() {
        RepositoryNode node = getRootNode(EResourceConstant.METADATA.getName());
        List<IRepositoryNode> connNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (subNode instanceof DBConnectionFolderRepNode || subNode instanceof DFConnectionFolderRepNode) {
                    // don't add mdm connections
                    connNodes.addAll(subNode.getChildren());
                }
            }
        }
        return connNodes;
    }

    public IRepositoryNode getConnectionRepositoryNode(String name) {
        List<IRepositoryNode> connections = getConnectionRepositoryNodes();
        if (connections != null && connections.size() > 0) {
            for (IRepositoryNode conn : connections) {
                boolean equals = conn.getObject().getLabel().equals(name);
                if (equals) {
                    return conn;
                }
            }
        }
        return null;
    }

    /**
     * DOC klliu Comment method "getRootNode".
     * 
     * @return
     */
    private RepositoryNode getRootNode(String nodeName) {
        RepositoryNode node = null;
        CommonViewer commonViewer = getDQCommonViewer();
        if (commonViewer != null) {
            TreeItem[] items = commonViewer.getTree().getItems();
            for (TreeItem item : items) {
                String text = item.getText();
                if (text.equals(nodeName)) {
                    node = (RepositoryNode) item.getData();
                }
            }

        }
        return node;
    }

    /**
     * DOC klliu 15750 Comment method "getDQRespositoryView".
     * 
     * @return
     */
    private CommonViewer getDQCommonViewer() {
        IViewPart part = null;
        CommonViewer commonViewer = null;
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                part = activePage.findView(DQRespositoryView.ID);
                if (part == null) {
                    return null;
                }
                DQRespositoryView dqView = (DQRespositoryView) part;
                commonViewer = dqView.getCommonViewer();
            }
        }
        return commonViewer;
    }

    /**
     * recursive find the RepositoryNode which contain the ModelElement.
     * 
     * @param modelElement
     * @return
     */
    public RepositoryNode recursiveFind(ModelElement modelElement) {
        if (modelElement instanceof Analysis) {

        } else if (modelElement instanceof TdReport) {

        } else if (modelElement instanceof TdColumn) {
            TdColumn column = (TdColumn) modelElement;
            IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsColumnSet(column));
            for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
                TdColumn columnOnUI = (TdColumn) ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
                if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
                    return (RepositoryNode) columnNode;
                }
            }
        } else if (modelElement instanceof TdTable) {
            TdTable table = (TdTable) modelElement;
            IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
            for (IRepositoryNode tableNode : schemaOrCatalogNode.getChildren().get(0).getChildren()) {
                TdTable tableOnUI = (TdTable) ((TdTableRepositoryObject) tableNode.getObject()).getTdTable();
                if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
                    return (RepositoryNode) tableNode;
                }
            }
        } else if (modelElement instanceof TdView) {
            TdView view = (TdView) modelElement;
            IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
            for (IRepositoryNode viewNode : schemaOrCatalogNode.getChildren().get(1).getChildren()) {
                TdView viewOnUI = (TdView) ((TdViewRepositoryObject) viewNode.getObject()).getTdView();
                if (ResourceHelper.getUUID(view).equals(ResourceHelper.getUUID(viewOnUI))) {
                    return (RepositoryNode) viewNode;
                }
            }
        } else if (modelElement instanceof MetadataColumn) {
            // MOD qiongli 2011-1-12 for delimted file
            MetadataColumn column = (MetadataColumn) modelElement;
            IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsMetadataTable(column));
            for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
                MetadataColumn columnOnUI = ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
                if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
                    return (RepositoryNode) columnNode;
                }
            }
        } else if (modelElement instanceof MetadataTable) {
            // MOD qiongli 2011-1-12 for delimted file
            MetadataTable table = (MetadataTable) modelElement;
            if (table.getNamespace() instanceof RecordFile) {
                IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(table));
                for (IRepositoryNode tableNode : connNode.getChildren()) {
                    MetadataTable tableOnUI = (MetadataTable) ((MetadataTableRepositoryObject) tableNode.getObject()).getTable();
                    if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
                        return (RepositoryNode) tableNode;
                    }
                }
            }
        } else if (modelElement instanceof Catalog) {
            Catalog catalog = (Catalog) modelElement;
            IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(catalog));
            for (IRepositoryNode catalogNode : connNode.getChildren()) {
                Catalog catalogOnUI = ((MetadataCatalogRepositoryObject) catalogNode.getObject()).getCatalog();
                if (ResourceHelper.getUUID(catalog).equals(ResourceHelper.getUUID(catalogOnUI))) {
                    return (RepositoryNode) catalogNode;
                }
            }
        } else if (modelElement instanceof Schema) {
            Schema schema = (Schema) modelElement;
            Catalog catalog = CatalogHelper.getParentCatalog(schema);
            // Schema's parent is catalog (MS SQL Server)
            if (catalog != null) {
                IRepositoryNode catalogNode = recursiveFind(catalog);
                for (IRepositoryNode schemaNode : catalogNode.getChildren()) {
                    Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
                    if (ResourceHelper.getUUID(schema).equals(ResourceHelper.getUUID(schemaOnUI))) {
                        return (RepositoryNode) schemaNode;
                    }
                }
            }
            // schema's parent is connection (e.g Oracle)
            IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(schema));
            for (IRepositoryNode schemaNode : connNode.getChildren()) {
                Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
                if (ResourceHelper.getUUID(catalog).equals(ResourceHelper.getUUID(schemaOnUI))) {
                    return (RepositoryNode) schemaNode;
                }
            }
        } else if (modelElement instanceof Connection) {
            Connection connection = (Connection) modelElement;
            List<IRepositoryNode> connsNode = getConnectionRepositoryNodes();
            for (IRepositoryNode connNode : connsNode) {
                Item itemTemp = ((IRepositoryViewObject) connNode.getObject()).getProperty().getItem();
                if (itemTemp instanceof ConnectionItem) {
                    ConnectionItem item = (ConnectionItem) itemTemp;
                    if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(item.getConnection()))) {
                        return (RepositoryNode) connNode;
                    }
                } else if (itemTemp instanceof FolderItem) {
                    List<ConnectionItem> connItems = getConnectionItemsFromFolderItem((FolderItem) itemTemp);
                    for (ConnectionItem connItem : connItems) {
                        if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(connItem.getConnection()))) {
                            return (RepositoryNode) connNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * get all the ConnectionItems from FolderItem (recursive).
     * 
     * @param folderItem
     * @return
     */
    private List<ConnectionItem> getConnectionItemsFromFolderItem(FolderItem folderItem) {
        List<ConnectionItem> list = new ArrayList<ConnectionItem>();
        EList objs = folderItem.getChildren();
        for (Object obj : objs) {
            if (obj instanceof FolderItem) {
                list.addAll(getConnectionItemsFromFolderItem((FolderItem) obj));
            } else if (obj instanceof ConnectionItem) {
                list.add((ConnectionItem) obj);
            }
        }
        return list;
    }
}
