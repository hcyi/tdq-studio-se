/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.commons.MapDB.utils.AbstractDB;
import org.talend.commons.MapDB.utils.DBSet;
import org.talend.commons.MapDB.utils.StandardDBName;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.resource.ResourceManager;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Unique Count Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.impl.UniqueCountIndicatorImpl#getUniqueValueCount <em>Unique Value Count
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class UniqueCountIndicatorImpl extends IndicatorImpl implements UniqueCountIndicator {

    /**
     * The default value of the '{@link #getUniqueValueCount() <em>Unique Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUniqueValueCount()
     * @generated
     * @ordered
     */
    protected static final Long UNIQUE_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUniqueValueCount() <em>Unique Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUniqueValueCount()
     * @generated
     * @ordered
     */
    protected Long uniqueValueCount = UNIQUE_VALUE_COUNT_EDEFAULT;

    private Set<Object> uniqueObjects = null;

    private Set<Object> duplicateObjects = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected UniqueCountIndicatorImpl() {
        super();
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
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.UNIQUE_COUNT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Set<Object> getUniqueValues() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT:
            return getUniqueValueCount();
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
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT:
            setUniqueValueCount((Long) newValue);
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
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT:
            setUniqueValueCount(UNIQUE_VALUE_COUNT_EDEFAULT);
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
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT:
            return UNIQUE_VALUE_COUNT_EDEFAULT == null ? uniqueValueCount != null : !UNIQUE_VALUE_COUNT_EDEFAULT
                    .equals(uniqueValueCount);
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
        result.append(uniqueValueCount);
        return result.toString();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Long getUniqueValueCount() {
        return uniqueValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setUniqueValueCount(Long newUniqueValueCount) {
        Long oldUniqueValueCount = uniqueValueCount;
        uniqueValueCount = newUniqueValueCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT,
                    oldUniqueValueCount, uniqueValueCount));
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
        this.setUniqueValueCount(IndicatorHelper.getLongFromObject(objects.get(0)[0]));
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
        return this.getUniqueValueCount();
    }

    @Override
    public boolean finalizeComputation() {
        uniqueObjects.removeAll(duplicateObjects);
        clearDrillDownData();
        this.setUniqueValueCount(Long.valueOf(uniqueObjects.size()));
        return super.finalizeComputation();
    }

    /**
     * Remove duplicate object from drill down map
     * 
     * 
     */
    private void clearDrillDownData() {
        if (!isUsedMapDBMode()) {
            return;
        }
        Iterator<Object> iterator = duplicateObjects.iterator();
        while (iterator.hasNext()) {
            drillDownMap.remove(iterator.next());
            drillDownRowCount--;
        }
        // remove some items because limit
        if (!this.checkMustStorCurrentRow()) {
            Iterator<Object> desIterator = drillDownMap.descendingKeySet().iterator();
            // Here is remove operation so that we need to use drillDownRowCount - 1 be parameter
            while (desIterator.hasNext() && !this.checkMustStorCurrentRow(drillDownRowCount - 1)) {
                Object currenKey = desIterator.next();
                drillDownMap.remove(currenKey);
                drillDownRowCount--;
            }
        }

    }

    @Override
    public boolean handle(Object data) {
        super.handle(data);
        if (data != null) {
            if (!this.uniqueObjects.add(data)) {
                // store duplicate objects
                duplicateObjects.add(data);

            } else {
                this.mustStoreRow = true;
            }
        }
        return true;
    }

    @Override
    public boolean reset() {
        this.uniqueValueCount = UNIQUE_VALUE_COUNT_EDEFAULT;
        if (isUsedMapDBMode()) {
            if (uniqueObjects != null) {
                ((DBSet<Object>) uniqueObjects).clear();
            }
            uniqueObjects = initValueForDBSet(StandardDBName.computeProcessSet.name());
            if (duplicateObjects != null) {
                ((DBSet<Object>) duplicateObjects).clear();
            }
            duplicateObjects = initValueForDBSet(StandardDBName.temp.name());
        } else {
            this.uniqueObjects.clear();
            this.duplicateObjects.clear();
        }

        return super.reset();
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
            if (dataFrequency == 1) {
                return true;
            }
        }

        return super.isValid(inputData);
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
                if (uniqueObjects != null && !((DBSet<Object>) uniqueObjects).isClosed()) {
                    return (DBSet<Object>) uniqueObjects;
                } else {
                    // create new DBSet
                    return ((DBSet<Object>) initValueForDBSet(StandardDBName.computeProcessSet.name()));
                }
            }
        }
        return super.getMapDB(dbName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handleDrillDownData(java.lang.Object, java.lang.Object,
     * int, int, java.lang.String)
     */
    @Override
    public void handleDrillDownData(Object masterObject, Object currentObject, int columnCount, int currentIndex,
            String currentColumnName) {
        // this key is a masterObject super method is Long so need override
        List<Object> rowData = drillDownMap.get(masterObject);
        if (rowData == null) {
            rowData = new ArrayList<Object>();
            drillDownMap.put(masterObject, rowData);
            this.drillDownRowCount++;
        }
        rowData.add(currentObject);
    }

} // UniqueCountIndicatorImpl
