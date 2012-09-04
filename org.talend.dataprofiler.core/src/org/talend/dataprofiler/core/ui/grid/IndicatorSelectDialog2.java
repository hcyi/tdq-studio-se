// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.grid;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.PhoneNumbStatisticsIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.IndicatorTreeModelBuilder;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * This dialog use to select the indictor object for different columns.
 */
public class IndicatorSelectDialog2 extends TrayDialog {

    private static final String DESCRIPTION = DefaultMessagesImpl.getString("IndicatorSelectDialog.description"); //$NON-NLS-1$

    private static final String PURPOSE = DefaultMessagesImpl.getString("IndicatorSelectDialog.purpose"); //$NON-NLS-1$

    private static final int COLUMN_WIDTH = 50;

    private ModelElementIndicator[] modelElementIndicators;

    private final String title;

    // ADD by zshen:need language to decide DatePatternFrequencyIndicator whether can be choose bu user.
    private ExecutionLanguage language;

    private DbmsLanguage dbms;

    private List<Button> checkButtons = new ArrayList<Button>();

    private int allColumnsCountSize = 0;// Record the total number of Columns.

    private Button okButton = null;

    Composite parent = null;

    Composite buttomComp = null;

    /**
     * IndicatorSelectDialog2 constructor.
     * 
     * @param parentShell
     * @param title
     * @param modelElementIndicators
     */
    public IndicatorSelectDialog2(Shell parentShell, String title, ModelElementIndicator[] modelElementIndicators,
            ExecutionLanguage language) {
        super(parentShell);
        this.title = title;
        this.modelElementIndicators = modelElementIndicators;
        this.language = language;
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);

        this.allColumnsCountSize = this.modelElementIndicators.length;
    }

    /**
     * IndicatorSelectDialog2 constructor.
     * 
     * @param parentShell
     * @param title
     * @param modelElementIndicators
     */
    public IndicatorSelectDialog2(Shell parentShell, String title, ModelElementIndicator[] modelElementIndicators) {
        this(parentShell, title, modelElementIndicators, null);
        // MOD zshen: obtain language.
        Object editorPart = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editorPart instanceof AnalysisEditor) {
            AnalysisEditor analyEditor = (AnalysisEditor) editorPart;
            if (analyEditor.getMasterPage() instanceof ColumnMasterDetailsPage) {
                this.language = ExecutionLanguage.get(((ColumnMasterDetailsPage) analyEditor.getMasterPage()).getExecCombo()
                        .getText());
                DataManager connection = ((ColumnMasterDetailsPage) analyEditor.getMasterPage()).getAnalysis().getContext()
                        .getConnection();
                this.dbms = DbmsLanguageFactory.createDbmsLanguage(connection);
            }
        }
        // ~

        for (ModelElementIndicator modelElementIndicator : getResult()) {
            modelElementIndicator.copyOldIndicatorEnum();
        }
    }

    /*
     * (non-Javadoc) Method declared in Window.
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        this.parent = comp;

        int style = SWT.NONE;
        style |= SWT.V_SCROLL;
        style |= SWT.H_SCROLL;
        style |= SWT.BORDER;
        style |= SWT.SINGLE;

        IndicatorSelectGrid grid = new IndicatorSelectGrid(comp, style);
        initializeGrid(grid);

        return comp;
    }

    private void initializeGrid(IndicatorSelectGrid grid) {
        GridData controlGridData = new GridData();
        controlGridData.minimumWidth = 500;
        controlGridData.heightHint = 600;

        controlGridData.verticalAlignment = SWT.FILL;
        controlGridData.grabExcessVerticalSpace = true;

        controlGridData.horizontalAlignment = SWT.FILL;
        controlGridData.grabExcessHorizontalSpace = true;

        grid.setLayoutData(controlGridData);

        GridColumn col = new GridColumn(grid, SWT.NONE);
        col.setTree(true);
        col.setWidth(200);
        // hide first column
        grid.getColumn(0).setVisible(false);

        int groupStyle = SWT.NONE;
        groupStyle |= SWT.TOGGLE;
        int colStyle = SWT.NONE;
        colStyle |= SWT.CENTER;
        colStyle |= SWT.CHECK;
        TdColumnHeaderRenderer renderer = null;

        GridColumn rowSelectCol = new GridColumn(grid, SWT.CHECK);
        renderer = new TdColumnHeaderRenderer();
        rowSelectCol.setHeaderRenderer(renderer);
        rowSelectCol.setCellRenderer(new TdCellRenderer());
        rowSelectCol.setText("Select All");
        rowSelectCol.setWidth(COLUMN_WIDTH);

        for (int i = 0; i < this.allColumnsCountSize; i++) {
            GridColumn newCol = new GridColumn(grid, SWT.CHECK);
            renderer = new TdColumnHeaderRenderer();
            newCol.setHeaderRenderer(renderer);
            newCol.setCellRenderer(new TdCellRenderer());
            newCol.setText(getResult()[i].getElementName());
            newCol.setWidth(COLUMN_WIDTH);
            newCol.setData(getResult()[i]);
        }

        // for (int i = 0; i < 10; i++) {
        // GridColumn newCol = new GridColumn(grid, SWT.NONE);
        // renderer = new TdColumnHeaderRenderer();
        // newCol.setHeaderRenderer(renderer);
        // newCol.setCellRenderer(new TdCellRenderer());
        // newCol.setText("Column #" + i + 1);
        // newCol.setWidth(COLUMN_WIDTH);
        // newCol.setData(getResult()[0]);
        // }

        IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();
        for (int i = 0; i < branchNodes.length; i++) {
            // indicator category row
            IIndicatorNode indicatorNode = branchNodes[i];

            GridItem item = new GridItem(grid, SWT.NONE);
            item.setText(indicatorNode.getLabel());
            createChildNodes(grid, item, indicatorNode);
        }

        grid.setHeaderVisible(true);
        grid.setEmptyColumnHeaderRenderer(new TdEmptyColumnHeaderRenderer());
        // show fixed row header
        grid.setRowHeaderRenderer(new TdRowHeaderRenderer());
        grid.setRowHeaderVisible(true);
        ((TdRowHeaderRenderer) grid.getRowHeaderRenderer()).setTree(true);

        grid.setHeaderVisible(true);
        grid.setLinesVisible(true);
        grid.setColumnScrolling(true);
        for (int i = 0; i < grid.getColumns().length; i++) {
            grid.getColumn(i).setMoveable(true);
        }
        for (int i = 0; i < grid.getColumns().length; i++) {
            grid.getColumn(i).setResizeable(true);
        }
        grid.setSelectionEnabled(false);
        grid.setCellSelectionEnabled(false);
        grid.getColumn(0).setCellSelectionEnabled(true);
        grid.getColumn(0).setWordWrap(false);
        grid.setRowsResizeable(false);

        grid.setItemHeight(18);

        grid.setLineColor(IndicatorSelectGrid.lightBlue);
        grid.setColumnScrolling(true);
        grid.setSelectionEnabled(false);
        grid.setRowsResizeable(false);

        grid.setFocusRenderer(null);
        grid.setColumnScrolling(true);

        grid.setCellHeaderSelectionBackground(new Color(Display.getCurrent(), 255, 255, 40));

        for (GridItem gridItem : grid.getItems()) {
            gridItem.setBackground(0, IndicatorSelectGrid.gray);
        }
    }

    /**
     * recursively create tree nodes
     * 
     * @param grid
     * @param currentItem
     * @param indicatorNode
     */
    private void createChildNodes(IndicatorSelectGrid grid, GridItem currentItem, IIndicatorNode indicatorNode) {

        currentItem.setExpanded(true);

        for (IIndicatorNode childNode : indicatorNode.getChildren()) {

            GridItem childItem = new GridItem(currentItem, SWT.NONE);
            childItem.setText(childNode.getLabel());

            for (int j = 0; j < grid.getColumnCount(); j++) {

                IndicatorEnum indicatorEnum = childNode.getIndicatorEnum();
                if (j == 0) {
                    // Indicator title column
                    if (indicatorEnum != null) {
                        // childItem.setData(INDICATORITEM, childNode);
                    }
                    continue;
                } else if (j == 1/* && grid.getColumnCount() > 2 */) {
                    // "Select All" column
                } else {

                    // DB columns
                    ModelElementIndicator meIndicator = null;
                    if (j - 2 < getResult().length) {
                        meIndicator = getResult()[j - 2];
                    } else {
                        meIndicator = getResult()[0];
                    }

                    childItem.setData(indicatorEnum);

                    // Enable/disable the check box
                    boolean isMatch = isMatchCurrentIndicator(meIndicator, childNode);
                    childItem.setCheckable(j, isMatch);

                    if (isMatch) {
                        // Check the box if it is already selected
                        if (meIndicator != null && meIndicator.tempContains(indicatorEnum)) {
                            childItem.setChecked(j, true);
                        }
                    }

                }
            }

            if (childNode.hasChildren()) {
                createChildNodes(grid, childItem, childNode);
            }
        }
    }

    private boolean isMatchCurrentIndicator(ModelElementIndicator currentIndicator, IIndicatorNode indicatorNode) {
        boolean returnCurrentIndicator = true;
        IIndicatorNode parentNode = indicatorNode.getParent();
        boolean isParentPhoneStatistics = parentNode != null && parentNode.getIndicatorInstance() != null
                && parentNode.getIndicatorInstance() instanceof PhoneNumbStatisticsIndicator;
        if (!ModelElementIndicatorRule.match(indicatorNode, currentIndicator, this.language)) {
            returnCurrentIndicator = false;
        }
        if (null != indicatorNode.getIndicatorInstance()
                && !(indicatorNode.getIndicatorInstance() instanceof DatePatternFreqIndicator)
                && null != indicatorNode.getIndicatorInstance().getIndicatorDefinition()
                && indicatorNode.getIndicatorInstance().getIndicatorDefinition().getSqlGenericExpression().size() < 1
                && !indicatorNode.hasChildren() && !(currentIndicator instanceof DelimitedFileIndicator)
                && !isParentPhoneStatistics) {
            returnCurrentIndicator = false;
        }
        return returnCurrentIndicator;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        parent.setLayout(new GridLayout(4, false));
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public ModelElementIndicator[] getResult() {
        return modelElementIndicators;
    }

}