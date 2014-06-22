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
package org.talend.dataprofiler.core.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * 
 * @author xtan
 * 
 */
public class DelimitedFileReader {

    private boolean debug = false;

    /*--------------1------------------- */
    private BufferedReader inputStream = null;

    private StreamBuffer streamBuffer = null;

    private ColumnBuffer4Joiner columnBuffer = null;

    /*--------------2------------------- */

    private String[] values = new String[StaticSettings.INITIAL_COLUMN_COUNT];

    private int columnsCount = 0;

    private long currentRecord = 0;

    private boolean skipEmptyRecord = false;

    /*--------------3------------------- */
    private boolean hasReadRecord = false;

    private boolean autoReallocateForHuge = true;

    private boolean initialized = false;

    private boolean closed = false;

    /*--------------4------------------- */

    public boolean splitRecord = false;

    /*--------------5------------------- */

    public DelimitedFileReader(String fileName, String encoding, String fieldDelimiter, String recordDelimiter,
            boolean needSkipEmptyRecord) throws IOException {
        if (fileName == null || fieldDelimiter == null || recordDelimiter == null) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString("DelimitedFileReader_NullPara")); //$NON-NLS-1$
        }

        inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));

        columnBuffer = new ColumnBuffer4Joiner();
        streamBuffer = new StreamBuffer(fieldDelimiter, recordDelimiter);

        skipEmptyRecord = needSkipEmptyRecord;

        initialized = true;

    }

    public boolean readRecord() throws IOException {
        if (splitRecord) {
            return readRecordSplitRecord();

        } else {
            return readRecordSplitField();
        }
    }

    private boolean readRecordSplitField() throws IOException {

        checkClosed();

        boolean in = false;

        hasReadRecord = false;

        columnsCount = 0;
        streamBuffer.columnStart = streamBuffer.currentPosition;

        while (streamBuffer.hasMoreData() && !hasReadRecord) {

            if (debug) {
                System.out.println(streamBuffer);
            }
            in = true;

            if (streamBuffer.needJoinReadNextBuffer()) {
                joinAndRead();
            }

            if (streamBuffer.isStartFieldDelimited()) {
                endColumn();
                streamBuffer.skipFieldDelimiter();
            } else if (streamBuffer.isStartRecordDelimited()) {
                endColumn();
                endRecord();
            } else {

                streamBuffer.currentPosition++;

                // for checking one column can support the max number of chars
                if (!autoReallocateForHuge
                        && streamBuffer.currentPosition - streamBuffer.columnStart + columnBuffer.position > StaticSettings.MAX_CHARS_IN_ONE_COLUMN) {
                    close();
                    throw new IOException(DefaultMessagesImpl.getString(
                            "DelimitedFileReader_OverLimitColumn", StaticSettings.MAX_CHARS_IN_ONE_COLUMN) //$NON-NLS-1$
                    );
                }
            }

        }

        if (!hasReadRecord) {
            if (in) {// aaa;bbb#111;222
                endColumn();
                endRecord();
            }
        }

        return hasReadRecord;

    }

    private boolean readRecordSplitRecord() throws IOException {

        checkClosed();

        boolean in = false;

        hasReadRecord = false;

        columnsCount = 0;
        streamBuffer.columnStart = streamBuffer.currentPosition;

        while (streamBuffer.hasMoreData() && !hasReadRecord) {

            if (debug) {
                System.out.println(streamBuffer);
            }
            in = true;

            if (streamBuffer.needJoinReadNextBuffer()) {
                joinAndRead();
            }

            if (streamBuffer.isStartRecordDelimited()) {
                endColumn();
                endRecord();

            } else if (streamBuffer.isStartFieldDelimited()) {
                endColumn();
                streamBuffer.skipFieldDelimiter();
            } else {

                streamBuffer.currentPosition++;

                // for checking one column can support the max number of chars
                if (!autoReallocateForHuge
                        && streamBuffer.currentPosition - streamBuffer.columnStart + columnBuffer.position > StaticSettings.MAX_CHARS_IN_ONE_COLUMN) {
                    close();
                    throw new IOException(DefaultMessagesImpl.getString(
                            "DelimitedFileReader_OverLimitColumn", StaticSettings.MAX_CHARS_IN_ONE_COLUMN) //$NON-NLS-1$
                    );
                }
            }

        }

        if (!hasReadRecord) {
            if (in) {// aaa;bbb#111;222
                endColumn();
                endRecord();
            }
        }

        return hasReadRecord;
    }

    private void joinAndRead() throws IOException {
        columnBuffer.saveCharInJoiner();
        streamBuffer.joinReadNextBuffer();
    }

    private void endRecord() throws IOException {
        streamBuffer.skipRecordDelimiter();

        if (skipEmptyRecord) {
            if (columnsCount == 0 || (columnsCount == 1 && values[0].equals(""))) { //$NON-NLS-1$
                columnsCount = 0;// reset the columnsCount = 0 is a must
                streamBuffer.columnStart = streamBuffer.currentPosition;
                return;
            }
        }

        // this flag is used as a loop exit condition during parsing
        hasReadRecord = true;

        currentRecord++;
    }

    /**
     * @exception IOException Thrown if a very rare extreme exception occurs during parsing, normally resulting from
     * improper data format.
     */
    private void endColumn() throws IOException {
        String currentValue = ""; //$NON-NLS-1$

        if (columnBuffer.position == 0) {
            currentValue = new String(streamBuffer.buffer, streamBuffer.columnStart, streamBuffer.currentPosition
                    - streamBuffer.columnStart);
        } else {
            // add the areadly datas in buffer
            columnBuffer.saveCharInJoiner();
            currentValue = new String(columnBuffer.buffer, 0, columnBuffer.position);
        }

        columnBuffer.position = 0;

        // for checking one record can support the max number of columns
        if (!autoReallocateForHuge && columnsCount >= StaticSettings.MAX_COLUMNS_IN_ONE_RECORD) {
            close();
            throw new IOException(DefaultMessagesImpl.getString(
                    "DelimitedFileReader_OverLimitRecord", StaticSettings.MAX_COLUMNS_IN_ONE_RECORD) //$NON-NLS-1$
            );
        }

        if (columnsCount == values.length) {

            int newLength = values.length * 2;

            String[] holder = new String[newLength];

            System.arraycopy(values, 0, holder, 0, values.length);

            values = holder;
        }

        values[columnsCount] = currentValue;

        columnsCount++;
    }

    private void close(boolean closing) {
        if (!closed) {
            if (closing) {
                streamBuffer.buffer = null;
                columnBuffer.buffer = null;
            }

            try {
                if (initialized) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // just ignore the exception
            }

            inputStream = null;

            closed = true;
        }
    }

    /**
     * when read a new record or get content of the column, there should check the stream first.
     */
    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException(DefaultMessagesImpl.getString("DelimitedFileReader_StreamClosed")); //$NON-NLS-1$
        }
    }

    /**
     * get one column value with a given column index for the current record.
     */
    public String get(int columnIndex) throws IOException {
        checkClosed();

        if (columnIndex > -1 && columnIndex < columnsCount) {
            return values[columnIndex];
        } else {
            return ""; //$NON-NLS-1$
        }
    }

    public String getRowRecord() {
        // here can fast for the common record seperator function with fieldDelimiter=""
        // becase there is only one field.
        if (columnsCount == 1) {
            return values[0];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnsCount; i++) {
            if (i > 0) {
                sb.append(streamBuffer.fieldDelimiter);// add the fieldDelimiter
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    /**
     * get the result how many records have been read.
     */
    public long getProcessedRecordCount() {
        return currentRecord;
    }

    /**
     * <p>
     * when skip the some records of the beginning and some records of the end in the file, then compute how many
     * records are there.
     * </p>
     * <p>
     * it can use after the skipHeaders();
     * </p>
     */
    public long getAvailableRowCount(int footer) throws IOException {
        checkClosed();
        boolean flag = true;
        do {
            flag = readRecord();
        } while (flag);
        return currentRecord - footer;
    }

    public int getAvailableColumnsCount() throws IOException {
        return columnsCount;
    }

    /**
     * skip the some records of the beginning in the file, and set the "currentRecord = 0"
     * 
     * 
     */
    public void skipHeaders(int header) throws IOException {
        checkClosed();
        if (header <= 0) {
            return;
        }
        for (int i = 0; i < header; i++) {
            readRecord();
        }
        currentRecord = 0;
    }

    /**
     * <p>
     * the default limit is:
     * </p>
     * <li>public static final int MAX_CHARS_IN_ONE_COLUMN = 100000;</li> <li>public static final int
     * MAX_COLUMNS_IN_ONE_RECORD = 100000;</li>
     * 
     */
    public void setAutoReallocateForHuge(boolean autoReallocateForHuge) {
        this.autoReallocateForHuge = autoReallocateForHuge;
    }

    /**
     * <p>
     * if the input datas like this: 111;222;#aaa;bbb;# (row separator: ;# field separator: ; )
     * </p>
     * <li>if FirstSplit_RecordSeparator, there will get (2 records, 2 columns)</li> <li>if FirstSplit_FieldSeparator,
     * there will get (2 records, 3 columns)</li>
     * <p>
     * The default value is false, it means split the field first.
     * </p>
     */
    public void setSplitRecord(boolean splitRecord) {
        this.splitRecord = splitRecord;
    }

    /**
     * Closes and releases all related resources.
     */
    public void close() {
        if (!closed) {
            close(true);
            closed = true;
        }
    }

    @Override
    protected void finalize() {
        close(false);
    }

    /**
     * a buffer: save the end chars of the last buffer and begin chars of the current buffer in the memory
     * 
     * @author xtan
     * 
     */
    private class ColumnBuffer4Joiner {

        char[] buffer;

        int position;

        public ColumnBuffer4Joiner() {
            buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
            position = 0;
        }

        /**
         * join TWO buffer together, save the end chars of the last buffer and begin chars of the current buffer in one
         * place
         */
        public void saveCharInJoiner() {

            int columnBufferBlankSpaceNum = columnBuffer.buffer.length - columnBuffer.position;
            int streamBufferFieldCharNum = 0;
            if (streamBuffer.count > 0) {// a must
                streamBufferFieldCharNum = streamBuffer.currentPosition - streamBuffer.columnStart;
            }
            // check and expand more memory for buffer
            if (columnBufferBlankSpaceNum < streamBufferFieldCharNum) {
                int newLength = columnBuffer.buffer.length + Math.max(streamBufferFieldCharNum, columnBuffer.buffer.length);

                char[] holder = new char[newLength];

                System.arraycopy(columnBuffer.buffer, 0, holder, 0, columnBuffer.position);

                columnBuffer.buffer = holder;
            }

            // copy datas from streamBuffer to columnBuffer for save it temporarily
            System.arraycopy(streamBuffer.buffer, streamBuffer.columnStart, columnBuffer.buffer, columnBuffer.position,
                    streamBufferFieldCharNum);

            columnBuffer.position += streamBufferFieldCharNum;
        }
    }

    /**
     * <b> a buffer with funtion: join the last data and read the next buffer, for supporting the multi-separator</b>
     * Notice: "count, currentPosition, currentPosition, columnStart", they are import here
     * 
     * @author xtan
     */
    private class StreamBuffer {

        public boolean needJoinReadNextBuffer() {
            // 5-2=3, when currentPostion=3; or 5-0=5, when currentPosition=5, and file not end, so read next buffer
            return currentPosition >= lastIndexToRead && !streamEndMeet;
            /* notice: here is >=, not > */
        }

        // when count=5, currentPosition=4 ===> hasMoreData()=true, it mean buffer[4] still need process
        public boolean hasMoreData() {
            return !streamEndMeet || currentPosition < count;
        }

        private void moveTailToHead() {
            count = count - currentPosition;
            for (int i = 0; i < count; i++) {
                buffer[i] = buffer[currentPosition + i];
            }

            lastIndexToRead = count - maxLimit;
            currentPosition = 0;
            columnStart = 0;
        }

        public void joinReadNextBuffer() throws IOException {
            moveTailToHead();
            int readCount = 0;
            int maxReadLength = buffer.length - count;
            try {
                readCount = inputStream.read(buffer, count, maxReadLength);
            } catch (IOException ex) {
                close();
                throw ex;
            }

            if (debug) {
                System.out.println("##maxReadLength=" + maxReadLength + "   newReadCount=" + readCount); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println(streamBuffer);
            }

            /* @see bug:http://talendforge.org/bugs/view.php?id=4554 */
            if (readCount < maxReadLength) {
                if (readCount == -1) {
                    streamEndMeet = true;
                }
            }

            if (readCount > -1) {
                count += readCount;
                lastIndexToRead = count - maxLimit;
            }
        }

        /*--------------1------------------- */
        private char[] buffer;

        private char[] fieldDelimiter;

        private char[] recordDelimiter;

        /*--------------2------------------- */

        private boolean lineFeedAll = false;

        // if LineMode.LINEFEED_ALL, if "\r\n", variableLineFeed = 2, if "\r" or "\n", variableLineFeed = 1
        // only used to adjust the skipRecordDelimiter()
        private int variableLineFeed = 0;

        /*--------------3------------------- */

        private int maxLimit;// maxLimit = Math.max(fieldDelimiter.length, recordDelimiter.length);

        private int currentPosition;

        // for join read
        private int lastIndexToRead;// lastIndexToRead = count - maxLimit, special: 5 = 5 - 0

        private int count;

        private int columnStart;

        /*--------------4------------------- */
        // end of the file
        private boolean streamEndMeet = false;

        public StreamBuffer(String fieldDelimiterPara, String recordDelimiterPara) throws IOException {
            buffer = new char[StaticSettings.MAX_BUFFER_SIZE];

            if (recordDelimiterPara.equals("\\n")) { //$NON-NLS-1$
                if (StaticSettings.LINEMODE == LineMode.LINEFEED_ALL) {
                    // notice: here we set it "\r\n", neither "\n" nor "\r", becase there want max length
                    recordDelimiter = "\r\n".toCharArray(); //$NON-NLS-1$
                    lineFeedAll = true;
                } else {
                    recordDelimiter = recordDelimiterPara.toCharArray();
                }

            } else {
                recordDelimiter = recordDelimiterPara.toCharArray();
            }

            fieldDelimiter = fieldDelimiterPara.toCharArray();

            maxLimit = Math.max(fieldDelimiter.length, recordDelimiter.length);

            // read one buffer datas first when initial
            try {
                count = inputStream.read(buffer, 0, buffer.length);
            } catch (IOException ex) {
                close();
                throw ex;
            }

            currentPosition = 0;
            columnStart = 0;
            lastIndexToRead = count - maxLimit;
            streamEndMeet = (count < buffer.length);
        }

        public boolean isStartFieldDelimited() {
            int maxLengthCanCheck = count - currentPosition;

            if (fieldDelimiter.length != 0 && fieldDelimiter.length <= maxLengthCanCheck) {// test here is a must
                for (int i = 0; i < fieldDelimiter.length; i++) {
                    if (buffer[currentPosition + i] != fieldDelimiter[i]) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            return true;
        }

        public boolean isStartRecordDelimited() {

            int maxLengthCanCheck = count - currentPosition;
            if (lineFeedAll) {// maxLengthCanCheck > 0 is must
                if (maxLengthCanCheck > 0) {
                    if (buffer[currentPosition] == '\n') {
                        variableLineFeed = 1;
                        return true;
                    } else if (buffer[currentPosition] == '\r') {
                        variableLineFeed = 1;
                        if (buffer[currentPosition + 1] == '\n' && maxLengthCanCheck > 1) {
                            variableLineFeed = 2;
                        }
                        return true;
                    }
                }

                return false;

            } else {

                if (recordDelimiter.length != 0 && recordDelimiter.length <= maxLengthCanCheck) {// test here is a
                    // must
                    for (int i = 0; i < recordDelimiter.length; i++) {
                        if (buffer[currentPosition + i] != recordDelimiter[i]) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }

            return true;
        }

        public void skipFieldDelimiter() {
            currentPosition += fieldDelimiter.length;
            columnStart = currentPosition;// means: start a new Column
        }

        public void skipRecordDelimiter() {
            if (lineFeedAll) {
                currentPosition += variableLineFeed;
            } else {
                currentPosition += recordDelimiter.length;
            }
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("count=").append(count).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("maxLimit=").append(maxLimit).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("lastIndexToRead=").append(lastIndexToRead).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("currentPosition=").append(currentPosition).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("columnStart=").append(columnStart).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("streamEndMeet=").append(streamEndMeet).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

            sb.append("overMaxLimit()=").append(needJoinReadNextBuffer()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("hasMoreData()=").append(hasMoreData()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

            sb.append("char[]=").append(buffer).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("char[").append(currentPosition).append("]=").append(buffer[currentPosition]).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            sb.append("fieldDelimiterLength=").append(fieldDelimiter.length).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("recordDelimiterLength=").append(recordDelimiter.length).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

            return sb.toString();
        }
    }

    /**
     * 
     * DOC Administrator DelimitedFileReader class global comment. Detailled comment
     */
    public enum LineMode {
        // it means, if input recordDelimiter is "\n",
        // if JRE on windows, it will be "\r\n",
        // if JRE on linux, it will be "\n",
        // if JRE on Mac, it will be "\r"

        LINEFEED_JRE,

        // it means, if input recordDelimiter is "\n", it will treat all the "\r", "\n", "\r\n" as recordDelimiter
        LINEFEED_ALL,

        // it means, if input recordDelimiter is "\n", just treat it as "\n"
        LINEFEED_NORMAL;
    }

    /**
     * StaticSettings for the DelimitedDataReader. they can be changed in unit test.
     */
    private static class StaticSettings {

        public static final int MAX_BUFFER_SIZE = 1024;

        public static final int INITIAL_COLUMN_COUNT = 10;

        public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;

        public static final int MAX_CHARS_IN_ONE_COLUMN = 100000;

        public static final int MAX_COLUMNS_IN_ONE_RECORD = 100000;

        // how do we process the "\n" as recordDelimiter
        public static final LineMode LINEMODE = LineMode.LINEFEED_ALL;

        // public static final boolean autoReallocateForHuge = true;
    }

    public static void main(String[] args) throws IOException {
        DelimitedFileReader fid = new DelimitedFileReader("D:\\talend\\talendFID\\in.csv", "ISO-8859-15", "", "\n", false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        int rowNum = 0;
        while (fid.readRecord()) {
            System.out.println("*********Row" + rowNum + "***********"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("------Row------\n" + fid.getRowRecord()); //$NON-NLS-1$
            int fieldNum = fid.getAvailableColumnsCount();
            for (int k = 0; k < fieldNum; k++) {
                System.out.println("------" + k + "------\n" + fid.get(k)); //$NON-NLS-1$ //$NON-NLS-2$
            }
            rowNum++;
            System.out.println("\n\n"); //$NON-NLS-1$
        }
    }

}