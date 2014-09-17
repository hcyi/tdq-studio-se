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
package org.talend.dq.analysis;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.commons.exception.BusinessException;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.db.connection.DatabaseSQLExecutor;
import org.talend.cwm.db.connection.DelimitedFileSQLExecutor;
import org.talend.cwm.db.connection.ISQLExecutor;
import org.talend.cwm.db.connection.MDMSQLExecutor;
import org.talend.cwm.db.connection.SQLExecutor;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.BlockKeyIndicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.designer.components.lookup.persistent.IPersistentLookupManager;
import org.talend.dq.analysis.match.BlockAndMatchManager;
import org.talend.dq.analysis.match.ExecuteMatchRuleHandler;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.dq.analysis.persistent.BlockKey;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.dq.indicators.Evaluator;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * execute the match analysis
 * 
 */
public class MatchAnalysisExecutor implements IAnalysisExecutor {

    private static Logger log = Logger.getLogger(MatchAnalysisExecutor.class);

    private IProgressMonitor monitor;

    private long usedMemory;

    private volatile boolean isLowMemory = false;

    private long checkContinueCount = 0L;

    private boolean keepRunning = true;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IAnalysisExecutor#execute(org.talend.dataquality.analysis.Analysis)
     */
    public ReturnCode execute(Analysis analysis) {
        // --- preconditions
        ReturnCode preRC = AnalysisExecutorHelper.check(analysis);
        if (!preRC.isOk()) {
            return preRC;
        }
        assert analysis != null;

        // --- creation time
        final long startime = AnalysisExecutorHelper.setExecutionDateInAnalysisResult(analysis);

        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        RecordMatchingIndicator recordMatchingIndicator = null;
        BlockKeyIndicator blockKeyIndicator = null;
        for (Indicator ind : indicators) {
            if (ind instanceof RecordMatchingIndicator) {
                recordMatchingIndicator = (RecordMatchingIndicator) ind;
            } else if (ind instanceof BlockKeyIndicator) {
                blockKeyIndicator = (BlockKeyIndicator) ind;
            }
        }
        if (recordMatchingIndicator == null || blockKeyIndicator == null) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        List<ModelElement> anlayzedElements = analysis.getContext().getAnalysedElements();
        if (anlayzedElements == null || anlayzedElements.size() == 0) {
            rc.setOk(Boolean.FALSE);
            // popup message to notify empty analyzed element
            MessageDialog.openWarning(null, Messages.getString("MatchAnalysisExecutor.warning"), //$NON-NLS-1$
                    Messages.getString("MatchAnalysisExecutor.EmptyAnalyzedElement")); //$NON-NLS-1$

            return rc;
        }

        Map<MetadataColumn, String> columnMap = getColumn2IndexMap(anlayzedElements);

        ISQLExecutor sqlExecutor = null;
        sqlExecutor = getSQLExectutor(analysis, recordMatchingIndicator, columnMap);
        if (sqlExecutor == null) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        monitor.worked(20);

        // Set schema for match key.
        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>();
        ExecuteMatchRuleHandler execHandler = new ExecuteMatchRuleHandler();
        MetadataColumn[] completeColumnSchema = AnalysisRecordGroupingUtils.getCompleteColumnSchema(columnMap);
        String[] colSchemaString = new String[completeColumnSchema.length];
        int idx = 0;
        for (MetadataColumn metadataCol : completeColumnSchema) {
            colSchemaString[idx++] = metadataCol.getName();
        }
        recordMatchingIndicator.setMatchRowSchema(colSchemaString);
        recordMatchingIndicator.reset();

        MatchGroupResultConsumer matchResultConsumer = createMatchGroupResultConsumer(recordMatchingIndicator);
        if (sqlExecutor.getStoreOnDisk()) {

            Map<BlockKey, String> blockKeys = sqlExecutor.getStoreOnDiskHandler().getBlockKeys();
            @SuppressWarnings("rawtypes")
            IPersistentLookupManager persistentLookupManager = (sqlExecutor.getStoreOnDiskHandler()).getPersistentLookupManager();
            try {
                returnCode = execHandler.executeWithStoreOnDisk(columnMap, recordMatchingIndicator, blockKeyIndicator,
                        persistentLookupManager, blockKeys, matchResultConsumer);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            // Added TDQ-9320 , use the result set iterator to replace the list of result in the memory.
            try {
                Iterator<Record> resultSetIterator = sqlExecutor.getResultSetIterator(analysis.getContext().getConnection(),
                        anlayzedElements);
                BlockAndMatchManager bAndmManager = new BlockAndMatchManager(resultSetIterator, matchResultConsumer, columnMap,
                        recordMatchingIndicator, blockKeyIndicator);
                bAndmManager.run();
            } catch (SQLException e) {
                log.error(e, e);
                rc.setOk(Boolean.FALSE);
                rc.setMessage(e.getMessage());
                return rc;
            } catch (BusinessException e) {
                log.error(e, e);
                rc.setOk(Boolean.FALSE);
                rc.setMessage(e.getMessage());
                return rc;
            }
        }

        if (!returnCode.isOk()) {
            return returnCode;
        }
        monitor.worked(100);
        monitor.done();

        // --- set metadata information of analysis
        AnalysisExecutorHelper.setExecutionNumberInAnalysisResult(analysis, true);
        if (isLowMemory) {
            rc.setMessage(Messages.getString("Evaluator.OutOfMomory", usedMemory));//$NON-NLS-1$
        }

        // nodify the master page
        refreshTableWithMatchFullResult(analysis);

        // --- compute execution duration
        if (this.continueRun()) {
            long endtime = System.currentTimeMillis();
            final ExecutionInformations resultMetadata = analysis.getResults().getResultMetadata();
            resultMetadata.setExecutionDuration((int) (endtime - startime));
            resultMetadata.setOutThreshold(false);
        }

        return rc;
    }

    private MatchGroupResultConsumer createMatchGroupResultConsumer(final RecordMatchingIndicator recordMatchingIndicator) {
        MatchGroupResultConsumer matchResultConsumer = new MatchGroupResultConsumer(false) {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping. MatchGroupResultConsumer#handle(java.lang.Object)
             */
            @Override
            public void handle(Object row) {
                recordMatchingIndicator.handle(row);
            }
        };
        return matchResultConsumer;
    }

    /**
     * refresh Table With Match Full Result .
     * 
     * @param analysis
     * 
     * @param matchResultConsumer
     */
    private void refreshTableWithMatchFullResult(Analysis analysis) {
        ITDQRepositoryService tdqRepService = AnalysisExecutorHelper.getTDQService();
        if (tdqRepService != null) {
            tdqRepService.refreshTableWithResult(analysis, null);
        }

    }

    /**
     * get Column2Index Map".
     * 
     * @param anlayzedElements
     * @return
     */
    private Map<MetadataColumn, String> getColumn2IndexMap(List<ModelElement> anlayzedElements) {
        Map<MetadataColumn, String> columnMap = new HashMap<MetadataColumn, String>();
        int index = 0;
        for (ModelElement column : anlayzedElements) {
            columnMap.put((MetadataColumn) column, String.valueOf(index++));
        }
        return columnMap;
    }

    /**
     * getSQLExectutor .
     * 
     * @param modelElement
     * @return
     */
    private ISQLExecutor getSQLExectutor(Analysis analysis, RecordMatchingIndicator recordMatchingIndicator,
            Map<MetadataColumn, String> columnMap) {
        ModelElement modelElement = analysis.getContext().getAnalysedElements().get(0);
        ISQLExecutor sqlExecutor = null;
        if (modelElement instanceof TdColumn) {
            sqlExecutor = new DatabaseSQLExecutor();
        } else if (modelElement instanceof TdXmlElementType) {
            sqlExecutor = new MDMSQLExecutor();
        } else if (modelElement instanceof MetadataColumn) {
            sqlExecutor = new DelimitedFileSQLExecutor();
        }
        // Tune on store on disk option when needed.
        Boolean isStoreOnDisk = TaggedValueHelper.getValueBoolean(SQLExecutor.STORE_ON_DISK_KEY, analysis);
        if (sqlExecutor != null && isStoreOnDisk) {
            sqlExecutor.setStoreOnDisk(Boolean.TRUE);
            sqlExecutor.initStoreOnDiskHandler(analysis, recordMatchingIndicator, columnMap);
        }
        return sqlExecutor;
    }

    // FIXME this method is the same as Evaluator.continueRun(). Factorize code.
    protected boolean continueRun() {
        // MOD scorreia 2013-09-10 avoid checking for each analyzed row. Check only every 1000 rows
        checkContinueCount++;
        if (checkContinueCount % Evaluator.CHECK_EVERY_N_COUNT != 0) {
            return keepRunning;
        }
        if (!Platform.isRunning()) { // Reporting engine is working as library
            return true;
        }

        if (monitor != null && monitor.isCanceled()) {
            keepRunning = false;
        } else if (this.isLowMemory) {
            keepRunning = false;
        } else if (AnalysisThreadMemoryChangeNotifier.getInstance().isUsageThresholdExceeded()) {
            this.usedMemory = AnalysisThreadMemoryChangeNotifier.convertToMB(ManagementFactory.getMemoryMXBean()
                    .getHeapMemoryUsage().getUsed());
            this.isLowMemory = true;
            keepRunning = false;
        }
        return keepRunning;
    } /*
       * (non-Javadoc)
       * 
       * @see org.talend.dq.analysis.IAnalysisExecutor#setMonitor(org.eclipse.core.runtime.IProgressMonitor)
       */

    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
