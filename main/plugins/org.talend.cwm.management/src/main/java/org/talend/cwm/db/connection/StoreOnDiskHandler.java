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
package org.talend.cwm.db.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.genkey.AbstractGenerateKey;
import org.talend.designer.components.lookup.common.ICommonLookup;
import org.talend.designer.components.lookup.persistent.IPersistentLookupManager;
import org.talend.designer.components.lookup.persistent.PersistentLookupManager;
import org.talend.designer.components.lookup.persistent.PersistentSortedLookupManager;
import org.talend.dq.analysis.AnalysisRecordGroupingUtils;
import org.talend.dq.analysis.persistent.BlockKey;
import org.talend.dq.analysis.persistent.MatchRow;

/**
 * created by zhao on Oct 15, 2013 executor with store on disk option.
 * 
 */
public class StoreOnDiskHandler {

    private IPersistentLookupManager<MatchRow> persistentLookupManager;

    private AbstractGenerateKey generateKeyAPI;

    private RecordMatchingIndicator recordMatchingIndicator;

    private Map<BlockKey, String> blockKeys = new TreeMap<BlockKey, String>();

    /**
     * The field map<columnName,columnIndex>
     */
    private Map<MetadataColumn, String> columnMap;

    /**
     * List of block key field, each block key will have a property map<algoName,algoNameValue> etc.
     */
    private List<Map<String, String>> blockKeyDefinitions = new ArrayList<Map<String, String>>();

    /**
     * Getter for blockKeys.
     * 
     * @return the blockKeys
     */
    public Map<BlockKey, String> getBlockKeys() {
        return this.blockKeys;
    }

    /**
     * Getter for persistentLookupManager.
     * 
     * @return the persistentLookupManager
     */
    public IPersistentLookupManager<MatchRow> getPersistentLookupManager() {
        return this.persistentLookupManager;
    }

    public StoreOnDiskHandler(RecordMatchingIndicator recordMatchingIndicator, final Map<MetadataColumn, String> columnMap,
            String container, int buffSize) throws IOException {
        this.columnMap = columnMap;
        this.recordMatchingIndicator = recordMatchingIndicator;
        this.blockKeyDefinitions = AnalysisRecordGroupingUtils.getBlockKeySchema(StoreOnDiskHandler.this.recordMatchingIndicator);
        initPersistentLookupManager(container, buffSize);
        generateKeyAPI = new AbstractGenerateKey();
    }

    /**
     * DOC zhao Comment method "initPersistentLookupManager".
     * 
     * @param columnMap
     * @param container
     * @param buffSize
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initPersistentLookupManager(String container, int buffSize) throws IOException {
        if (blockKeyDefinitions.size() == 0) {
            // No block key specified.
            persistentLookupManager = new PersistentLookupManager(ICommonLookup.MATCHING_MODE.ALL_ROWS, container,
                    new org.talend.designer.components.persistent.IRowCreator<MatchRow>() {

                        public MatchRow createRowInstance() {
                            return new MatchRow(columnMap.size(), blockKeyDefinitions.size());
                        }
                    });
        } else {
            persistentLookupManager = new PersistentSortedLookupManager(ICommonLookup.MATCHING_MODE.ALL_MATCHES, container,
                    new org.talend.designer.components.persistent.IRowCreator<MatchRow>() {

                        public MatchRow createRowInstance() {
                            return new MatchRow(columnMap.size(), blockKeyDefinitions.size());
                        }
                    }, buffSize);
        }
    }

    protected void beginQuery() throws Exception {
        persistentLookupManager.initPut();
        blockKeys.clear();
    }

    protected void handleRow(Object[] oneRow) throws Exception {
        // Store data on disk
        String[] rowArray = new String[oneRow.length];
        int index = 0;
        for (Object obj : oneRow) {
            rowArray[index++] = obj == null ? null : obj.toString();
        }
        MatchRow matchRow = new MatchRow(columnMap.size(), blockKeyDefinitions.size());
        matchRow.setRow(Arrays.asList(rowArray));
        persistentLookupManager.put(matchRow);
        if (blockKeyDefinitions.size() > 0) {
            BlockKey blockKey = genBlockKey(rowArray);
            // Set the block key to match row.
            matchRow.setKey(blockKey.getBlockKey());
            // Add the block key to a map.
            blockKeys.put(blockKey, null);
        }

    }

    private BlockKey genBlockKey(String[] inputRow) {
        Map<String, String> columnValueMap = new HashMap<String, String>();
        for (MetadataColumn columnName : columnMap.keySet()) {
            columnValueMap.put(columnName.getName(), inputRow[Integer.parseInt(columnMap.get(columnName))]);
        }
        BlockKey blockKey = new BlockKey(blockKeyDefinitions.size());
        String[] keys = generateKeyAPI.getGenKeyArray(blockKeyDefinitions, columnValueMap);
        blockKey.setBlockKey(Arrays.asList(keys));
        return blockKey;
    }

    protected void endQuery() throws Exception {
        persistentLookupManager.endPut();
    }
}
