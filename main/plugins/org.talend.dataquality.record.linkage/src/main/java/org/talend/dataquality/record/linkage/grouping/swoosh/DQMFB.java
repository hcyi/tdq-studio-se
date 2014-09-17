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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMerger;

/**
 * created by zhao on Jul 10, 2014 MFB algorithm adapted to DQ grouping API, which will continue matching two different
 * groups.
 * 
 */
public class DQMFB extends MFB {

    private Callback callback;

    private Queue<Record> queue;

    private List<Record> mergedRecords = new ArrayList<Record>();

    /**
     * DOC zhao DQMFB constructor comment.
     * 
     * @param matcher
     * @param merger
     */
    public DQMFB(IRecordMatcher matcher, IRecordMerger merger) {
        super(matcher, merger);
    }

    public DQMFB(IRecordMatcher matcher, IRecordMerger merger, Callback callback) {
        super(matcher, merger);
        this.callback = callback;
        queue = new ArrayDeque<Record>();
        if (callback != null) {
            callback.onBeginProcessing();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matchmerge.mfb.MFB#isMatchDiffGroups()
     */
    @Override
    protected boolean isMatchDiffGroups() {
        return true;
    }

    public void matchOneRecord(Record oneRecord) {
        // queue.offer(oneRecord);
        matchOneRecord(callback, mergedRecords, queue, oneRecord);
    }

    public List<Record> getResult() {
        while (!queue.isEmpty() && !callback.isInterrupted()) {
            Record currentRecord = queue.poll();
            matchOneRecord(callback, mergedRecords, queue, currentRecord);
        }
        callback.onEndProcessing();
        return this.mergedRecords;
    }

}
