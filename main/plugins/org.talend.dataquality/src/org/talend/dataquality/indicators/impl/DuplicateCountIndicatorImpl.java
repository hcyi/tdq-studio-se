/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.commons.MapDB.utils.AbstractDB;
import org.talend.commons.MapDB.utils.DBSet;
import org.talend.commons.MapDB.utils.DBValueListMap;
import org.talend.commons.MapDB.utils.StandardDBName;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.resource.ResourceManager;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Duplicate Count Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.impl.DuplicateCountIndicatorImpl#getDuplicateValueCount <em>Duplicate
 * Value Count</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DuplicateCountIndicatorImpl extends IndicatorImpl implements DuplicateCountIndicator {

    /**
     * The default value of the '{@link #getDuplicateValueCount() <em>Duplicate Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDuplicateValueCount()
     * @generated
     * @ordered
     */
    protected static final Long DUPLICATE_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDuplicateValueCount() <em>Duplicate Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDuplicateValueCount()
     * @generated
     * @ordered
     */
    protected Long duplicateValueCount = DUPLICATE_VALUE_COUNT_EDEFAULT;

    private Set<Object> duplicateObjects = null;

    // Added yyin 20120607 TDQ-3589
    // private Map<Object, Object[]> uniqueMap = new HashMap<Object, Object[]>();

    // store all duplicate rows by one key, in its list.
    private Map<Object, List<Object[]>> duplicateMap = null;

    // ~

    /**
     * Create a new DBMap
     * 
     * @return
     */
    private Map<Object, List<Object[]>> initValueForDBMap(String dbName) {
        if (isUsedMapDBMode()) {
            return new DBValueListMap<Object>(ResourceManager.getMapDBFilePath(this), this.getName(), dbName);
        } else {
            return new HashMap<Object, List<Object[]>>();
        }
    }

    /**
     * Create a new DBSet
     * 
     * @return
     */
    private Set<Object> initValueForDBSet(String dbName) {
        if (isUsedMapDBMode()) {
            return new DBSet<Object>(ResourceManager.getMapDBFilePath(this), this.getName(), dbName);
        } else {
            return new HashSet<Object>();
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected DuplicateCountIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.DUPLICATE_COUNT_INDICATOR;
    }

    /**
     * MOD yyin 20120608 TDQ-3589 use this set for "view values", not all dup rows, but only different values <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Set<Object> getDuplicateValues() {
        return this.duplicateObjects;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT:
            return getDuplicateValueCount();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT:
            setDuplicateValueCount((Long) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT:
            setDuplicateValueCount(DUPLICATE_VALUE_COUNT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT:
            return DUPLICATE_VALUE_COUNT_EDEFAULT == null ? duplicateValueCount != null : !DUPLICATE_VALUE_COUNT_EDEFAULT
                    .equals(duplicateValueCount);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(this.getName());
        result.append("= ");
        result.append(duplicateValueCount);
        return result.toString();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Long getDuplicateValueCount() {
        return duplicateValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setDuplicateValueCount(Long newDuplicateValueCount) {
        Long oldDuplicateValueCount = duplicateValueCount;
        duplicateValueCount = newDuplicateValueCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT, oldDuplicateValueCount,
                    duplicateValueCount));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.lang.Object[])
     * 
     * ADDED scorreia 2008-04-30 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!checkResults(objects, 1)) {
            return false;
        }
        // MOD gdbu 2011-4-14 bug : 18975
        this.setDuplicateValueCount(IndicatorHelper.getLongFromObject(objects.get(0)[0]));
        // ~18975
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     * 
     * ADDED scorreia 2008-05-12 getIntegerValue()
     */
    @Override
    public Long getIntegerValue() {
        return this.getDuplicateValueCount();
    }

    @Override
    public boolean finalizeComputation() {
        // Mod yyin 20120608 TDQ-3589
        // at the end: remove the list.size()=1 , only remain the list.size()>1
        Iterator<Object> iterator = duplicateMap.keySet().iterator();
        long dupSize = 0;
        while (iterator.hasNext()) {
            Object key = iterator.next();
            List<Object[]> valuelist = duplicateMap.get(key);

            if (valuelist.size() > 1) {
                dupSize++;
                if (needStoreDrillDownData()) {
                    addDrillDownData(valuelist);
                }
                this.duplicateObjects.add(key);
            }
        }
        this.setDuplicateValueCount(Long.valueOf(dupSize));
        return super.finalizeComputation();
    }

    /**
     * Add drill down data
     * 
     * @param valuelist
     */
    private void addDrillDownData(List<Object[]> valuelist) {

        for (int i = 0; i < valuelist.size(); i++) {
            List<Object> rowData = Arrays.asList(valuelist.get(i));
            if (this.checkMustStorCurrentRow()) {
                drillDownMap.put(this.drillDownRowCount++, rowData);
            }
        }

    }

    /**
     * Judge whether we should store current data. It should be mapDB mode and keyIndex is not more than limit
     * 
     * @param keyIndex
     * @return
     */
    private boolean needStoreDrillDownData() {
        return isUsedMapDBMode() && this.checkMustStorCurrentRow();
    }

    @Override
    public boolean handle(Object data) {
        super.handle(data);
        // MOD yyi 2009-09-22 8769
        // if (!this.uniqueObjects.add(data)) {
        // // store duplicate objects
        // if (duplicateObjects.add(data)) {
        // this.mustStoreRow = true;
        // }
        // }
        return true;
    }

    @Override
    public boolean reset() {
        this.duplicateValueCount = DUPLICATE_VALUE_COUNT_EDEFAULT;
        if (isUsedMapDBMode()) {
            if (duplicateMap != null) {
                ((DBValueListMap<Object>) duplicateMap).clear();
            }
            duplicateMap = initValueForDBMap(StandardDBName.computeProcess.name());
            if (duplicateObjects != null) {
                ((DBSet<Object>) duplicateObjects).clear();
            }
            duplicateObjects = initValueForDBSet(StandardDBName.computeProcessSet.name());
        } else {
            this.duplicateMap.clear();
        }
        return super.reset();
    }

    /*
     * store the colValue as key, get the whole row from resultset as value. when duplicate, also get the first row from
     * uMap into dMap Added yyin 20120608 TDQ-3589
     * 
     * @see org.talend.dataquality.indicators.DuplicateCountIndicator#handle(java.lang.Object, java.sql.ResultSet, int)
     */
    @Override
    public void handle(Object colValue, ResultSet resultSet, int columnSize) throws SQLException {
        super.handle(colValue);
        // first get the whole row from resultset
        Object[] valueObject = new Object[columnSize];

        for (int i = 0; i < columnSize; i++) {
            valueObject[i] = resultSet.getObject(i + 1);
        }

        if (duplicateMap.containsKey(colValue)) {
            duplicateMap.get(colValue).add(valueObject);
        } else {
            List<Object[]> temp = new ArrayList<Object[]>();
            temp.add(valueObject);
            duplicateMap.put(colValue, temp);
        }

    }

    /*
     * (non-Javadoc) Added yyin 20120608 TDQ-3589
     * 
     * @see org.talend.dataquality.indicators.DuplicateCountIndicator#getDuplicateMap()
     */
    @Override
    public Map<Object, List<Object[]>> getDuplicateMap() {

        return this.duplicateMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.DuplicateCountIndicator#handle(java.lang.Object, java.lang.String[])
     */
    @Override
    public void handle(Object object, String[] rowValues) {
        super.handle(object);
        if (duplicateMap.containsKey(object)) {
            duplicateMap.get(object).add(rowValues);
        } else {
            List<Object[]> temp = new ArrayList<Object[]>();
            temp.add(rowValues);
            duplicateMap.put(object, temp);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getMapDB(java.lang.String)
     */
    @Override
    public AbstractDB getMapDB(String dbName) {
        if (isUsedMapDBMode()) {
            // is get computeProcess map
            if (StandardDBName.computeProcess.name().equals(dbName)) {
                // current set is valid
                if (duplicateObjects != null && !((DBSet<Object>) duplicateObjects).isClosed()) {
                    return (DBSet<Object>) duplicateObjects;
                } else {
                    // create new DBSet
                    return ((DBSet<Object>) initValueForDBSet(StandardDBName.computeProcess.name()));
                }
            }
        }
        return super.getMapDB(dbName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#isValid(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(Object inputData) {
        if (Long.class.isInstance(inputData)) {
            Long dataFrequency = Long.valueOf(inputData.toString());
            if (dataFrequency > 1) {
                return true;
            }
        }

        return super.isValid(inputData);
    }

} // DuplicateCountIndicatorImpl
