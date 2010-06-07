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
package org.talend.dataprofiler.core.ui.editor.analysis.drilldown;

import java.util.Hashtable;
import java.util.List;

import net.sourceforge.sqlexplorer.dataset.actions.ExportCSVAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 * 
 * Display result for drill down operation
 */
public class DrillDownResultEditor extends EditorPart {

    private TableViewer tableView;

    private ExportCSVAction exportAction;

    private Hashtable<String, Action> actionList;

    public static final String[] ACTION_NAME = { "export" };

    public DrillDownResultEditor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.setSite(site);
        this.setInput(input);
        DrillDownEditorInput ddEditorInput = (DrillDownEditorInput) input;
        this.setPartName(ddEditorInput.getMenuType());
        this.setTitleToolTip(ddEditorInput.getToolTipText());

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        // layout.marginTop = 10;
        parent.setLayout(layout);
        makeAction();
        createCoolBar(parent);
        // createPopupMenu();
        tableView = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = tableView.getTable();
        MenuManager popupMenu = new MenuManager();
        // IAction newRowAction = new NewRowAction();
        popupMenu.add(exportAction);
        Menu menu = popupMenu.createContextMenu(table);
        table.setMenu(menu);

        exportAction.setTable(table);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(table);
        if (this.getEditorInput() instanceof DrillDownEditorInput) {
            DrillDownEditorInput ddEditorInput = (DrillDownEditorInput) this.getEditorInput();

            addTableColumn(ddEditorInput, table);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setData(ddEditorInput.getDataSet());
            tableView.setLabelProvider(new DrillDownResultLabelProvider());
            tableView.setContentProvider(new DrillDownResultContentProvider());
            tableView.setInput(this.getEditorInput());

            for (TableColumn packColumn : table.getColumns()) {
                packColumn.pack();
            }

        }

    }

    private void createCoolBar(Composite parent) {
        CoolBar coolBar = new CoolBar(parent, SWT.HORIZONTAL | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(coolBar);
        actionList = new Hashtable<String, Action>();

        actionList.put(ACTION_NAME[0], exportAction);
        ToolBar toolBar = new ToolBar(coolBar, SWT.RIGHT | SWT.FLAT);
        toolBar.pack();

        for (int i = 0; i < ACTION_NAME.length; i++) {
            ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setImage(actionList.get(ACTION_NAME[i]).getImageDescriptor().createImage());

            item.setToolTipText(actionList.get(ACTION_NAME[i]).getText());
            item.addSelectionListener(new ListenToTheAction(actionList.get(ACTION_NAME[i])));

        }

        CoolItem coolEdit = new CoolItem(coolBar, SWT.HORIZONTAL | SWT.DROP_DOWN);
        coolEdit.setControl(toolBar);

        CoolItem[] coolItems = coolBar.getItems();
        for (int i = 0; i < coolItems.length; i++) {
            CoolItem coolItem = coolItems[i];
            Control control = coolItem.getControl();
            Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Point coolSize = coolItem.computeSize(size.x, size.y);
            if (control instanceof ToolBar) {
                ToolBar bar = (ToolBar) control;
                if (bar.getItemCount() > 0) {

                    size.x = bar.getItem(0).getWidth();

                }
            }
            coolItem.setMinimumSize(size);
            coolItem.setPreferredSize(coolSize);
            coolItem.setSize(coolSize);
        }
        Point p = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        toolBar.setSize(p);
        Point p2 = coolEdit.computeSize(p.x, p.y);
        coolEdit.setControl(toolBar);
        coolEdit.setSize(p2);

        coolBar.setLocked(true);
    }

    /*
     * init Action save as,export
     */
    private void makeAction() {

        // export
        exportAction = new ExportCSVAction();
        exportAction.setImageDescriptor(CorePlugin.getImageDescriptor("icons/export_wiz.gif"));
        exportAction.setEnabled(true);
    }

    private void addTableColumn(DrillDownEditorInput ddEditorInput, Table table) {

        List<TdColumn> columnElementList = ddEditorInput.filterAdaptColumnHeader();
        for (TdColumn columnElement : columnElementList) {
            TableColumn column = new TableColumn(table, SWT.CENTER);
            column.setText(columnElement.getName());
        }
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     * 
     */
    private class DrillDownResultLabelProvider implements ITableLabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object element, int columnIndex) {
            // TODO Auto-generated method stub

            Object object = ((Object[]) element)[columnIndex];
            if (object != null) {
                return object.toString();
            }
            return "(null)";
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        public void dispose() {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     */
    private class DrillDownResultContentProvider implements IStructuredContentProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof DrillDownEditorInput) {
                DrillDownEditorInput ddEditorInput = ((DrillDownEditorInput) inputElement);
                List<Object[]> newColumnElementList = ddEditorInput.filterAdaptDataList();

                if (ddEditorInput.isDataSpill()) {
                    Object[] leaveOutData = new Object[newColumnElementList.get(0).length];
                    for (int i = 0; i < newColumnElementList.get(0).length; i++) {
                        leaveOutData[i] = "...";
                    }
                    newColumnElementList.add(leaveOutData);
                }
                return newColumnElementList.toArray();
            }
            return new Object[0];
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         * java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub
            viewer.getControl().pack();
        }

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     */
    private class ListenToTheAction extends SelectionAdapter {

        Action theAction;

        public ListenToTheAction(Action action) {
            super();
            this.theAction = action;
        }

        public void widgetSelected(SelectionEvent e) {
            this.theAction.run();
            // changeCoolBarState();

        }
    }

}
