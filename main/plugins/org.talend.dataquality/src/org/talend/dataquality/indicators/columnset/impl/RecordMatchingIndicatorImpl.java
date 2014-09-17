/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Record Matching Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.RecordMatchingIndicatorImpl#getGroupSize2groupFrequency
 * <em>Group Size2group Frequency</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.RecordMatchingIndicatorImpl#getMatchedRecordCount <em>
 * Matched Record Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.RecordMatchingIndicatorImpl#getSuspectRecordCount <em>
 * Suspect Record Count</em>}</li>
 * <li>
 * {@link org.talend.dataquality.indicators.columnset.impl.RecordMatchingIndicatorImpl#getBuiltInMatchRuleDefinition
 * <em>Built In Match Rule Definition</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RecordMatchingIndicatorImpl extends ColumnSetMultiValueIndicatorImpl implements RecordMatchingIndicator {

    // To be syn with RecordMatcherType#T_SwooshAlgorithm
    private final static String T_SWOOSH_ALG_NAME = "T_SwooshAlgorithm";

    /**
     * The default value of the '{@link #getGroupSize2groupFrequency() <em>Group Size2group Frequency</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getGroupSize2groupFrequency()
     * @generate NOT
     * @ordered
     */
    protected static final TreeMap<Object, Long> GROUP_SIZE2GROUP_FREQUENCY_EDEFAULT = new TreeMap<Object, Long>();

    /**
     * The cached value of the '{@link #getGroupSize2groupFrequency() <em>Group Size2group Frequency</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getGroupSize2groupFrequency()
     * @generated
     * @ordered
     */
    protected TreeMap<Object, Long> groupSize2groupFrequency = GROUP_SIZE2GROUP_FREQUENCY_EDEFAULT;

    /**
     * The default value of the '{@link #getMatchedRecordCount() <em>Matched Record Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMatchedRecordCount()
     * @generated
     * @ordered
     */
    protected static final long MATCHED_RECORD_COUNT_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getMatchedRecordCount() <em>Matched Record Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMatchedRecordCount()
     * @generated
     * @ordered
     */
    protected long matchedRecordCount = MATCHED_RECORD_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getSuspectRecordCount() <em>Suspect Record Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getSuspectRecordCount()
     * @generated
     * @ordered
     */
    protected static final long SUSPECT_RECORD_COUNT_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getSuspectRecordCount() <em>Suspect Record Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getSuspectRecordCount()
     * @generated
     * @ordered
     */
    protected long suspectRecordCount = SUSPECT_RECORD_COUNT_EDEFAULT;

    /**
     * The cached value of the '{@link #getBuiltInMatchRuleDefinition() <em>Built In Match Rule Definition</em>}'
     * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getBuiltInMatchRuleDefinition()
     * @generated
     * @ordered
     */
    protected MatchRuleDefinition builtInMatchRuleDefinition;

    private int groupSizeColumnIndex = 0;// Column index of group size

    private int masterColumnIndex = 0; // Column index of master

    private int groupQualityColumnIndex = 0;// Column index of group quality

    private String[] matchRowSchema = null;// Schema of a match row

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected RecordMatchingIndicatorImpl() {
        super();
    }

    @Override
    public void setMatchRowSchema(String[] matchRowSchema) {
        this.matchRowSchema = matchRowSchema;
    }

    @Override
    public String[] getMatchRowSchema() {
        return this.matchRowSchema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#reset()
     */
    @Override
    public boolean reset() {
        List<String> matchRowSchemaList = Arrays.asList(matchRowSchema);
        this.groupSizeColumnIndex = matchRowSchemaList.indexOf(PluginConstant.GRP_SIZE);
        this.masterColumnIndex = matchRowSchemaList.indexOf(PluginConstant.MASTER);
        this.groupQualityColumnIndex = matchRowSchemaList.indexOf(PluginConstant.GRP_QUALITY);

        matchedRecordCount = MATCHED_RECORD_COUNT_EDEFAULT;
        suspectRecordCount = SUSPECT_RECORD_COUNT_EDEFAULT;
        groupSize2groupFrequency = new TreeMap<Object, Long>();

        return super.reset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#handle(org.eclipse.emf.common
     * .util.EList)
     */
    @Override
    public boolean handle(Object data) {
        String[] values = (String[]) data;
        Boolean isMaster = Boolean.valueOf(values[masterColumnIndex]);
        Integer groupSize = StringUtils.isEmpty(values[groupSizeColumnIndex]) ? 0 : Integer.valueOf(values[groupSizeColumnIndex]);
        if (isMaster) { // Find the master row
            if (null == groupSize2groupFrequency.get(values[groupSizeColumnIndex])) {
                groupSize2groupFrequency.put(values[groupSizeColumnIndex], 1l);
            } else {
                long freq = groupSize2groupFrequency.get(values[groupSizeColumnIndex]) + 1;
                groupSize2groupFrequency.put(values[groupSizeColumnIndex], freq);
            }
            // Compute matched record count
            if (builtInMatchRuleDefinition != null && groupSize > 1) {
                // Group quality score >= confidence threshold then it's a confident match group
                double groupScore = Double.valueOf(values[groupQualityColumnIndex]);
                double confidenceThreshold = builtInMatchRuleDefinition.getMatchGroupQualityThreshold();
                if (groupScore >= confidenceThreshold) {
                    matchedRecordCount = matchedRecordCount + groupSize;
                } else {
                    suspectRecordCount = suspectRecordCount + groupSize;
                }
            }
        }
        if (T_SWOOSH_ALG_NAME.equals(getBuiltInMatchRuleDefinition().getRecordLinkageAlgorithm())) {
            // masters with group size greater than 1 should NOT be taken into account when compute row count in case of
            // t-swoosh algorithm.
            if (!(isMaster && groupSize > 1)) {
                count++;
            }
        } else {
            count++;
        }
        return Boolean.TRUE;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.RECORD_MATCHING_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public TreeMap<Object, Long> getGroupSize2groupFrequency() {
        return groupSize2groupFrequency;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setGroupSize2groupFrequency(TreeMap<Object, Long> newGroupSize2groupFrequency) {
        TreeMap<Object, Long> oldGroupSize2groupFrequency = groupSize2groupFrequency;
        groupSize2groupFrequency = newGroupSize2groupFrequency;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.RECORD_MATCHING_INDICATOR__GROUP_SIZE2GROUP_FREQUENCY, oldGroupSize2groupFrequency,
                    groupSize2groupFrequency));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public long getMatchedRecordCount() {
        return matchedRecordCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setMatchedRecordCount(long newMatchedRecordCount) {
        long oldMatchedRecordCount = matchedRecordCount;
        matchedRecordCount = newMatchedRecordCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.RECORD_MATCHING_INDICATOR__MATCHED_RECORD_COUNT, oldMatchedRecordCount, matchedRecordCount));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public long getSuspectRecordCount() {
        return suspectRecordCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setSuspectRecordCount(long newSuspectRecordCount) {
        long oldSuspectRecordCount = suspectRecordCount;
        suspectRecordCount = newSuspectRecordCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.RECORD_MATCHING_INDICATOR__SUSPECT_RECORD_COUNT, oldSuspectRecordCount, suspectRecordCount));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public MatchRuleDefinition getBuiltInMatchRuleDefinition() {
        return builtInMatchRuleDefinition;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetBuiltInMatchRuleDefinition(MatchRuleDefinition newBuiltInMatchRuleDefinition,
            NotificationChain msgs) {
        MatchRuleDefinition oldBuiltInMatchRuleDefinition = builtInMatchRuleDefinition;
        builtInMatchRuleDefinition = newBuiltInMatchRuleDefinition;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION, oldBuiltInMatchRuleDefinition,
                    newBuiltInMatchRuleDefinition);
            if (msgs == null) {
                msgs = notification;
            } else {
                msgs.add(notification);
            }
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setBuiltInMatchRuleDefinition(MatchRuleDefinition newBuiltInMatchRuleDefinition) {
        if (newBuiltInMatchRuleDefinition != builtInMatchRuleDefinition) {
            NotificationChain msgs = null;
            if (builtInMatchRuleDefinition != null) {
                msgs = ((InternalEObject) builtInMatchRuleDefinition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION, null, msgs);
            }
            if (newBuiltInMatchRuleDefinition != null) {
                msgs = ((InternalEObject) newBuiltInMatchRuleDefinition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
                        - ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION, null, msgs);
            }
            msgs = basicSetBuiltInMatchRuleDefinition(newBuiltInMatchRuleDefinition, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION, newBuiltInMatchRuleDefinition,
                    newBuiltInMatchRuleDefinition));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION:
            return basicSetBuiltInMatchRuleDefinition(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__GROUP_SIZE2GROUP_FREQUENCY:
            return getGroupSize2groupFrequency();
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__MATCHED_RECORD_COUNT:
            return getMatchedRecordCount();
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__SUSPECT_RECORD_COUNT:
            return getSuspectRecordCount();
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION:
            return getBuiltInMatchRuleDefinition();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__GROUP_SIZE2GROUP_FREQUENCY:
            setGroupSize2groupFrequency((TreeMap<Object, Long>) newValue);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__MATCHED_RECORD_COUNT:
            setMatchedRecordCount((Long) newValue);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__SUSPECT_RECORD_COUNT:
            setSuspectRecordCount((Long) newValue);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION:
            setBuiltInMatchRuleDefinition((MatchRuleDefinition) newValue);
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
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__GROUP_SIZE2GROUP_FREQUENCY:
            setGroupSize2groupFrequency(GROUP_SIZE2GROUP_FREQUENCY_EDEFAULT);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__MATCHED_RECORD_COUNT:
            setMatchedRecordCount(MATCHED_RECORD_COUNT_EDEFAULT);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__SUSPECT_RECORD_COUNT:
            setSuspectRecordCount(SUSPECT_RECORD_COUNT_EDEFAULT);
            return;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION:
            setBuiltInMatchRuleDefinition((MatchRuleDefinition) null);
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
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__GROUP_SIZE2GROUP_FREQUENCY:
            return GROUP_SIZE2GROUP_FREQUENCY_EDEFAULT == null ? groupSize2groupFrequency != null
                    : !GROUP_SIZE2GROUP_FREQUENCY_EDEFAULT.equals(groupSize2groupFrequency);
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__MATCHED_RECORD_COUNT:
            return matchedRecordCount != MATCHED_RECORD_COUNT_EDEFAULT;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__SUSPECT_RECORD_COUNT:
            return suspectRecordCount != SUSPECT_RECORD_COUNT_EDEFAULT;
        case ColumnsetPackage.RECORD_MATCHING_INDICATOR__BUILT_IN_MATCH_RULE_DEFINITION:
            return builtInMatchRuleDefinition != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) {
            return super.toString();
        }

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (groupSize2groupFrequency: ");
        result.append(groupSize2groupFrequency);
        result.append(", matchedRecordCount: ");
        result.append(matchedRecordCount);
        result.append(", suspectRecordCount: ");
        result.append(suspectRecordCount);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#isSaveTempDataToFile()
     */
    @Override
    public boolean isUsedMapDBMode() {
        return false;
    }

} // RecordMatchingIndicatorImpl
