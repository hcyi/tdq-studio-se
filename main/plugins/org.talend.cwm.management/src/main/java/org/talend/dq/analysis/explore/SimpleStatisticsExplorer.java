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
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.dbms.HiveDbmsLanguage;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class SimpleStatisticsExplorer extends DataExplorer {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        // MOD zshen feature 12919 adapt to pop-menu for Jave engin on result page
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        // MOD qiongli 2011-3-4,feature 19192:filter menue 'view rows' for columSet AnalysisType.
        AnalysisType analysisType = this.analysis.getParameters().getAnalysisType();
        // MOD qiongli 2012-8-29 hive don't support 'where in...'
        boolean isHive = dbmsLanguage instanceof HiveDbmsLanguage;
        if (!isXml() || !isSqlEngine) {

            switch (this.indicatorEnum) {
            // case RowCountIndicatorEnum:
            case NullCountIndicatorEnum:
            case BlankCountIndicatorEnum:
            case DefValueCountIndicatorEnum:
            case UserDefinedIndicatorEnum:
                // when user define indicator
                IndicatorDefinition indicatorDefinition = this.indicator.getIndicatorDefinition();
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS)
                        + (indicatorDefinition instanceof UDIndicatorDefinition ? getQueryForViewRows(indicatorDefinition)
                                : getRowsStatement()) : null);
                break;

            case UniqueIndicatorEnum:
                if (analysisType != AnalysisType.COLUMN_SET) {
                    if (!isHive) {
                        map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatementWithSubQuery() : null);
                    } else if (!isSqlEngine) {
                        map.put(MENU_VIEW_ROWS, null);
                    }
                }
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES) + getValuesStatement(this.columnName) : null);
                break;
            case DistinctCountIndicatorEnum:
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES)
                        + getDistinctValuesStatement(this.columnName) : null);
                break;

            case DuplicateCountIndicatorEnum:
                if (analysisType != AnalysisType.COLUMN_SET) {
                    if (!isHive) {
                        map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatementWithSubQuery() : null);
                    } else if (!isSqlEngine) {
                        map.put(MENU_VIEW_ROWS, null);
                    }
                }
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES) + getValuesStatement(this.columnName) : null);
                break;
            default:
            }
        }

        return map;
    }

    /**
     * get Query For View Rows.
     * 
     * @param indicatorDefinition
     * @return
     */
    private String getQueryForViewRows(IndicatorDefinition indicatorDefinition) {
        String sql = PluginConstant.EMPTY_STRING;
        IndicatorCategory category = IndicatorCategoryHelper.getCategory(indicatorDefinition);
        EList<TdExpression> list = ((UDIndicatorDefinition) indicatorDefinition).getViewRowsExpression();
        TdExpression tdExp = DbmsLanguage.getSqlExpression(indicatorDefinition, dbmsLanguage.getDbmsName(), list,
                dbmsLanguage.getDbVersion());
        sql = tdExp.getBody();
        String dataFilterClause = getDataFilterClause();
        if (!dataFilterClause.equals(PluginConstant.EMPTY_STRING)) {
            sql = sql.replace(GenericSQLHandler.WHERE_CLAUSE, dbmsLanguage.where() + "(" + dataFilterClause + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            sql = sql.replace(GenericSQLHandler.AND_WHERE_CLAUSE, dbmsLanguage.and() + "(" + dataFilterClause + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            sql = sql.replace(GenericSQLHandler.WHERE_CLAUSE, PluginConstant.EMPTY_STRING);
            sql = sql.replace(GenericSQLHandler.AND_WHERE_CLAUSE, PluginConstant.EMPTY_STRING);
        }
        String tableName = getFullyQualifiedTableName(this.indicator.getAnalyzedElement());
        sql = sql.replace(GenericSQLHandler.TABLE_NAME, tableName);
        sql = sql.replace(GenericSQLHandler.COLUMN_NAMES, this.indicator.getAnalyzedElement().getName());

        if (sql.indexOf(GenericSQLHandler.UDI_INDICATOR_VALUE) != -1) {
            if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                // replace <%=__INDICATOR_VALUE__%>
                sql = sql.replace(GenericSQLHandler.UDI_INDICATOR_VALUE, this.indicator.getRealValue().toString());

            } else {
                sql = sql.replace(GenericSQLHandler.UDI_INDICATOR_VALUE,
                        (String.valueOf(this.indicator.getIntegerValue().intValue())));
            }
        }
        return sql;
    }
}
