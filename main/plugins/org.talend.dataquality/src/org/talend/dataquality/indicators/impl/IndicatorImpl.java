/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.commons.MapDB.utils.AbstractDB;
import org.talend.commons.MapDB.utils.DBMap;
import org.talend.commons.MapDB.utils.StandardDBName;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.rules.JoinElement;
import org.talend.resource.ResourceManager;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sql.TalendTypeConvert;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.impl.ModelElementImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Indicator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getCount <em>Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getNullCount <em>Null Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getAnalyzedElement <em>Analyzed Element</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getDataminingType <em>Datamining Type</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getIndicatorDefinition <em>Indicator Definition</em>}
 * </li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getInstantiatedExpressions <em>Instantiated
 * Expressions</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#isComputed <em>Computed</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getJoinConditions <em>Join Conditions</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#getMaxNumberRows <em>Max Number Rows</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#isValidRow <em>Valid Row</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#isInValidRow <em>In Valid Row</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.IndicatorImpl#isStoreData <em>Store Data</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class IndicatorImpl extends ModelElementImpl implements Indicator {

    private static Logger log = Logger.getLogger(IndicatorImpl.class);

    /**
     * Decide whether save temp data to file
     */
    protected boolean usedMapDBMode = true;

    /**
     * The limit size of the items which will be store by drillDown
     */
    protected Long drillDownLimitSize = 0l;

    /**
     * The count which how many rows have been store.
     */
    protected Long drillDownRowCount = 0l;

    /**
     * store drill down rows.
     */
    protected DBMap<Object, List<Object>> drillDownMap = null;

    /**
     * The default value of the '{@link #getCount() <em>Count</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getCount()
     * @generated
     * @ordered
     */
    protected static final Long COUNT_EDEFAULT = new Long(0L);

    /**
     * The cached value of the '{@link #getCount() <em>Count</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getCount()
     * @generated
     * @ordered
     */
    protected Long count = COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getNullCount() <em>Null Count</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNullCount()
     * @generated
     * @ordered
     */
    protected static final Long NULL_COUNT_EDEFAULT = new Long(0L);

    /**
     * The cached value of the '{@link #getNullCount() <em>Null Count</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNullCount()
     * @generated
     * @ordered
     */
    protected Long nullCount = NULL_COUNT_EDEFAULT;

    // MOD mzhao feature 12919
    protected boolean mustStoreRow = false;

    /**
     * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getParameters()
     * @generated
     * @ordered
     */
    protected IndicatorParameters parameters;

    /**
     * The cached value of the '{@link #getAnalyzedElement() <em>Analyzed Element</em>}' reference. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getAnalyzedElement()
     * @generated
     * @ordered
     */
    protected ModelElement analyzedElement;

    /**
     * The default value of the '{@link #getDataminingType() <em>Datamining Type</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getDataminingType()
     * @generated
     * @ordered
     */
    protected static final DataminingType DATAMINING_TYPE_EDEFAULT = DataminingType.NOMINAL;

    /**
     * The cached value of the '{@link #getDataminingType() <em>Datamining Type</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getDataminingType()
     * @generated
     * @ordered
     */
    protected DataminingType dataminingType = DATAMINING_TYPE_EDEFAULT;

    /**
     * The cached value of the '{@link #getIndicatorDefinition() <em>Indicator Definition</em>}' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getIndicatorDefinition()
     * @generated
     * @ordered
     */
    protected IndicatorDefinition indicatorDefinition;

    /**
     * The cached value of the '{@link #getInstantiatedExpressions() <em>Instantiated Expressions</em>}' containment
     * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getInstantiatedExpressions()
     * @generated
     * @ordered
     */
    protected EList<Expression> instantiatedExpressions;

    /**
     * The default value of the '{@link #isComputed() <em>Computed</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isComputed()
     * @generated
     * @ordered
     */
    protected static final boolean COMPUTED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isComputed() <em>Computed</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isComputed()
     * @generated
     * @ordered
     */
    protected boolean computed = COMPUTED_EDEFAULT;

    /**
     * The cached value of the '{@link #getJoinConditions() <em>Join Conditions</em>}' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getJoinConditions()
     * @generated
     * @ordered
     */
    protected EList<JoinElement> joinConditions;

    /**
     * The default value of the '{@link #getMaxNumberRows() <em>Max Number Rows</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getMaxNumberRows()
     * @generated
     * @ordered
     */
    protected static final int MAX_NUMBER_ROWS_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getMaxNumberRows() <em>Max Number Rows</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getMaxNumberRows()
     * @generated
     * @ordered
     */
    protected int maxNumberRows = MAX_NUMBER_ROWS_EDEFAULT;

    /**
     * The default value of the '{@link #isValidRow() <em>Valid Row</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isValidRow()
     * @generated
     * @ordered
     */
    protected static final boolean VALID_ROW_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isValidRow() <em>Valid Row</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isValidRow()
     * @generated
     * @ordered
     */
    protected boolean validRow = VALID_ROW_EDEFAULT;

    /**
     * The default value of the '{@link #isInValidRow() <em>In Valid Row</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isInValidRow()
     * @generated
     * @ordered
     */
    protected static final boolean IN_VALID_ROW_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isInValidRow() <em>In Valid Row</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isInValidRow()
     * @generated
     * @ordered
     */
    protected boolean inValidRow = IN_VALID_ROW_EDEFAULT;

    /**
     * The default value of the '{@link #isStoreData() <em>Store Data</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isStoreData()
     * @generated
     * @ordered
     */
    protected static final boolean STORE_DATA_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isStoreData() <em>Store Data</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isStoreData()
     * @generated
     * @ordered
     */
    protected boolean storeData = STORE_DATA_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected IndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Long getCount() {
        return count;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setCount(Long newCount) {
        Long oldCount = count;
        count = newCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__COUNT, oldCount, count));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Long getNullCount() {
        return nullCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setNullCount(Long newNullCount) {
        Long oldNullCount = nullCount;
        nullCount = newNullCount;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__NULL_COUNT, oldNullCount,
                    nullCount));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public IndicatorParameters getParameters() {
        return parameters;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetParameters(IndicatorParameters newParameters, NotificationChain msgs) {
        IndicatorParameters oldParameters = parameters;
        parameters = newParameters;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.INDICATOR__PARAMETERS, oldParameters, newParameters);
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
    public void setParameters(IndicatorParameters newParameters) {
        if (newParameters != parameters) {
            NotificationChain msgs = null;
            if (parameters != null) {
                msgs = ((InternalEObject) parameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.INDICATOR__PARAMETERS, null, msgs);
            }
            if (newParameters != null) {
                msgs = ((InternalEObject) newParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.INDICATOR__PARAMETERS, null, msgs);
            }
            msgs = basicSetParameters(newParameters, msgs);
            if (msgs != null) {
                msgs.dispatch();
            }
        } else if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__PARAMETERS, newParameters,
                    newParameters));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ModelElement getAnalyzedElement() {
        if (analyzedElement != null && analyzedElement.eIsProxy()) {
            InternalEObject oldAnalyzedElement = (InternalEObject) analyzedElement;
            analyzedElement = (ModelElement) eResolveProxy(oldAnalyzedElement);
            if (analyzedElement != oldAnalyzedElement) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT,
                            oldAnalyzedElement, analyzedElement));
                }
            }
        }
        return analyzedElement;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelElement basicGetAnalyzedElement() {
        return analyzedElement;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setAnalyzedElement(ModelElement newAnalyzedElement) {
        ModelElement oldAnalyzedElement = analyzedElement;
        analyzedElement = newAnalyzedElement;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT,
                    oldAnalyzedElement, analyzedElement));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public DataminingType getDataminingType() {
        ModelElement elt = getAnalyzedElement();
        if (elt == null) {
            return getDataminingTypeGen();
        }
        TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(elt);
        if (col == null) {
            return getDataminingTypeGen();
        }
        DataminingType type = MetadataHelper.getDataminingType(col);
        if (type == null) {
            // try the default code
            return getDataminingTypeGen();
        }
        return type;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public DataminingType getDataminingTypeGen() {
        return dataminingType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setDataminingType(DataminingType newDataminingType) {
        DataminingType oldDataminingType = dataminingType;
        dataminingType = newDataminingType == null ? DATAMINING_TYPE_EDEFAULT : newDataminingType;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__DATAMINING_TYPE,
                    oldDataminingType, dataminingType));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public IndicatorDefinition getIndicatorDefinition() {
        if (indicatorDefinition != null && indicatorDefinition.eIsProxy()) {
            InternalEObject oldIndicatorDefinition = (InternalEObject) indicatorDefinition;
            indicatorDefinition = (IndicatorDefinition) eResolveProxy(oldIndicatorDefinition);
            if (indicatorDefinition != oldIndicatorDefinition) {
                if (eNotificationRequired()) {
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION,
                            oldIndicatorDefinition, indicatorDefinition));
                }
            }
        }
        return indicatorDefinition;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public IndicatorDefinition basicGetIndicatorDefinition() {
        return indicatorDefinition;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setIndicatorDefinition(IndicatorDefinition newIndicatorDefinition) {
        IndicatorDefinition oldIndicatorDefinition = indicatorDefinition;
        indicatorDefinition = newIndicatorDefinition;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION,
                    oldIndicatorDefinition, indicatorDefinition));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<Expression> getInstantiatedExpressions() {
        if (instantiatedExpressions == null) {
            instantiatedExpressions = new EObjectContainmentEList<Expression>(Expression.class, this,
                    IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS);
        }
        return instantiatedExpressions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isComputed() {
        return computed;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setComputed(boolean newComputed) {
        boolean oldComputed = computed;
        computed = newComputed;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__COMPUTED, oldComputed, computed));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<JoinElement> getJoinConditions() {
        if (joinConditions == null) {
            joinConditions = new EObjectContainmentEList<JoinElement>(JoinElement.class, this,
                    IndicatorsPackage.INDICATOR__JOIN_CONDITIONS);
        }
        return joinConditions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getMaxNumberRows() {
        return maxNumberRows;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setMaxNumberRows(int newMaxNumberRows) {
        int oldMaxNumberRows = maxNumberRows;
        maxNumberRows = newMaxNumberRows;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__MAX_NUMBER_ROWS, oldMaxNumberRows,
                    maxNumberRows));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isValidRow() {
        return validRow;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setValidRow(boolean newValidRow) {
        boolean oldValidRow = validRow;
        validRow = newValidRow;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__VALID_ROW, oldValidRow, validRow));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isInValidRow() {
        return inValidRow;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setInValidRow(boolean newInValidRow) {
        boolean oldInValidRow = inValidRow;
        inValidRow = newInValidRow;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__IN_VALID_ROW, oldInValidRow,
                    inValidRow));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isStoreData() {
        return storeData;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setStoreData(boolean newStoreData) {
        boolean oldStoreData = storeData;
        storeData = newStoreData;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INDICATOR__STORE_DATA, oldStoreData,
                    storeData));
        }
    }

    /**
     * <!-- begin-user-doc --> Increments counts for each given data. <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean handle(Object data) {
        mustStoreRow = false;
        if (data == null) {
            nullCount++;
        }
        count++;
        return true;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean reset() {
        count = COUNT_EDEFAULT;
        nullCount = NULL_COUNT_EDEFAULT;
        // for MapDB init
        clearDrillDownMap();
        return true;
    }

    /**
     * DOC talend Comment method "clearDrillDownMap".
     */
    protected void clearDrillDownMap() {
        if (this.isUsedMapDBMode()) {
            if (drillDownMap != null) {
                drillDownMap.clear();
            }
            drillDownMap = initValueForDBMap(StandardDBName.drillDown.name());
            drillDownRowCount = 0l;
        }
    }

    /**
     * Create a new DBMap
     * 
     * @return
     */
    private DBMap<Object, List<Object>> initValueForDBMap(String dbName) {
        return new DBMap<Object, List<Object>>(ResourceManager.getMapDBFilePath(this), this.getName(), dbName);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getPurpose() {
        IndicatorDefinition def = this.getIndicatorDefinition();
        return (def != null) ? MetadataHelper.getPurpose(def) : "?? no purpose found for " + this.getName() + " ??";
        // return IndicatorDocumentationHandler.getPurpose(this.eClass().getClassifierID());
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getLongDescription() {
        IndicatorDefinition def = this.getIndicatorDefinition();
        return (def != null) ? MetadataHelper.getDescription(def) : "?? no description found for " + this.getName() + " ??";
        // return IndicatorDocumentationHandler.getLongDescription(this.eClass().getClassifierID());
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean prepare() {
        return this.reset();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean finalizeComputation() {
        return true;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        // nothing to implement here, a generic indicator does not know how to handle a result.
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT scorreia 2008-04-30
     */
    @Override
    public Expression getInstantiatedExpressions(String language) {
        if (language == null) {
            return null;
        }
        EList<Expression> expressions = this.getInstantiatedExpressions();
        for (Expression expression : expressions) {
            if (expression == null) {
                continue;
            }
            if (language.toUpperCase().compareTo(expression.getLanguage().toUpperCase()) == 0) {
                return expression;
            }
        }
        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT scorreia 2008-04-30
     */
    @Override
    public boolean setInstantiatedExpression(Expression expression) {
        if (expression == null) {
            return false;
        }
        String language = expression.getLanguage();
        if (language == null) {
            return false;
        }
        Expression found = getInstantiatedExpressions(language);
        if (found != null) {
            found.setBody(expression.getBody());
        } else {
            getInstantiatedExpressions().add(expression);
        }
        return true;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Long getIntegerValue() {
        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Double getRealValue() {
        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public IndicatorValueType getValueType() {
        return IndicatorValueType.INTEGER_VALUE;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getInstanceValue() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean mustStoreRow() {
        return mustStoreRow;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public void setMustStoreRow(boolean mustStoreRow) {
        this.mustStoreRow = mustStoreRow;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean handle(EList<Object> datas) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see orgomg.cwm.objectmodel.core.impl.ModelElementImpl#getName()
     * 
     * ADDED scorreia 2008-05-02 return a default name if none has been set.
     */
    @Override
    public String getName() {
        String n = super.getName();
        if (n != null) {
            return n;
        }
        // else
        IndicatorDefinition def = getIndicatorDefinition();
        if (def != null) {
            return def.getName();
        }
        // else
        return this.eClass().getName();
    }

    /**
     * Method "checkResults" checks whether the result has the right number of elements (but some elements could be
     * null).
     * 
     * @param objects the results of the query
     * @param expectedSize the expected number of elements in the resulting array "objects"
     * @return true if ok
     */
    protected boolean checkResults(List<Object[]> objects, final int expectedSize) {
        if (objects == null || objects.isEmpty()) {
            log.error("<" + getName() + "> Unexpected result: Result set is null or empty for the query.");
            return false;
        }
        for (Object[] array : objects) {
            if (array == null || expectedSize != array.length) {
                log.error("<" + getName() + "> Unexpected result: " + array + ". Expected " + expectedSize
                        + " columns as a result of the query.");
                return false;
            }
            if (log.isDebugEnabled()) {
                log.debug("<" + getName() + "> Result of query: " + ArrayUtils.toString(array));
            }
            // for (int i = 0; i < array.length; i++) {
            // Object object = array[i];
            // // assume last column is not null (for example in frequency table result)
            // if (object == null && i == array.length - 1) {
            // log.error("Unexpected result: " + object + ". One of the column returned by the query is null!");
            // return false;
            // }
            // }
        }

        return true;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case IndicatorsPackage.INDICATOR__PARAMETERS:
            return basicSetParameters(null, msgs);
        case IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS:
            return ((InternalEList<?>) getInstantiatedExpressions()).basicRemove(otherEnd, msgs);
        case IndicatorsPackage.INDICATOR__JOIN_CONDITIONS:
            return ((InternalEList<?>) getJoinConditions()).basicRemove(otherEnd, msgs);
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
        case IndicatorsPackage.INDICATOR__COUNT:
            return getCount();
        case IndicatorsPackage.INDICATOR__NULL_COUNT:
            return getNullCount();
        case IndicatorsPackage.INDICATOR__PARAMETERS:
            return getParameters();
        case IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT:
            if (resolve) {
                return getAnalyzedElement();
            }
            return basicGetAnalyzedElement();
        case IndicatorsPackage.INDICATOR__DATAMINING_TYPE:
            return getDataminingType();
        case IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION:
            if (resolve) {
                return getIndicatorDefinition();
            }
            return basicGetIndicatorDefinition();
        case IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS:
            return getInstantiatedExpressions();
        case IndicatorsPackage.INDICATOR__COMPUTED:
            return isComputed();
        case IndicatorsPackage.INDICATOR__JOIN_CONDITIONS:
            return getJoinConditions();
        case IndicatorsPackage.INDICATOR__MAX_NUMBER_ROWS:
            return getMaxNumberRows();
        case IndicatorsPackage.INDICATOR__VALID_ROW:
            return isValidRow();
        case IndicatorsPackage.INDICATOR__IN_VALID_ROW:
            return isInValidRow();
        case IndicatorsPackage.INDICATOR__STORE_DATA:
            return isStoreData();
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
        case IndicatorsPackage.INDICATOR__COUNT:
            setCount((Long) newValue);
            return;
        case IndicatorsPackage.INDICATOR__NULL_COUNT:
            setNullCount((Long) newValue);
            return;
        case IndicatorsPackage.INDICATOR__PARAMETERS:
            setParameters((IndicatorParameters) newValue);
            return;
        case IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT:
            setAnalyzedElement((ModelElement) newValue);
            return;
        case IndicatorsPackage.INDICATOR__DATAMINING_TYPE:
            setDataminingType((DataminingType) newValue);
            return;
        case IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION:
            setIndicatorDefinition((IndicatorDefinition) newValue);
            return;
        case IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS:
            getInstantiatedExpressions().clear();
            getInstantiatedExpressions().addAll((Collection<? extends Expression>) newValue);
            return;
        case IndicatorsPackage.INDICATOR__COMPUTED:
            setComputed((Boolean) newValue);
            return;
        case IndicatorsPackage.INDICATOR__JOIN_CONDITIONS:
            getJoinConditions().clear();
            getJoinConditions().addAll((Collection<? extends JoinElement>) newValue);
            return;
        case IndicatorsPackage.INDICATOR__MAX_NUMBER_ROWS:
            setMaxNumberRows((Integer) newValue);
            return;
        case IndicatorsPackage.INDICATOR__VALID_ROW:
            setValidRow((Boolean) newValue);
            return;
        case IndicatorsPackage.INDICATOR__IN_VALID_ROW:
            setInValidRow((Boolean) newValue);
            return;
        case IndicatorsPackage.INDICATOR__STORE_DATA:
            setStoreData((Boolean) newValue);
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
        case IndicatorsPackage.INDICATOR__COUNT:
            setCount(COUNT_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__NULL_COUNT:
            setNullCount(NULL_COUNT_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__PARAMETERS:
            setParameters((IndicatorParameters) null);
            return;
        case IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT:
            setAnalyzedElement((ModelElement) null);
            return;
        case IndicatorsPackage.INDICATOR__DATAMINING_TYPE:
            setDataminingType(DATAMINING_TYPE_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION:
            setIndicatorDefinition((IndicatorDefinition) null);
            return;
        case IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS:
            getInstantiatedExpressions().clear();
            return;
        case IndicatorsPackage.INDICATOR__COMPUTED:
            setComputed(COMPUTED_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__JOIN_CONDITIONS:
            getJoinConditions().clear();
            return;
        case IndicatorsPackage.INDICATOR__MAX_NUMBER_ROWS:
            setMaxNumberRows(MAX_NUMBER_ROWS_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__VALID_ROW:
            setValidRow(VALID_ROW_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__IN_VALID_ROW:
            setInValidRow(IN_VALID_ROW_EDEFAULT);
            return;
        case IndicatorsPackage.INDICATOR__STORE_DATA:
            setStoreData(STORE_DATA_EDEFAULT);
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
        case IndicatorsPackage.INDICATOR__COUNT:
            return COUNT_EDEFAULT == null ? count != null : !COUNT_EDEFAULT.equals(count);
        case IndicatorsPackage.INDICATOR__NULL_COUNT:
            return NULL_COUNT_EDEFAULT == null ? nullCount != null : !NULL_COUNT_EDEFAULT.equals(nullCount);
        case IndicatorsPackage.INDICATOR__PARAMETERS:
            return parameters != null;
        case IndicatorsPackage.INDICATOR__ANALYZED_ELEMENT:
            return analyzedElement != null;
        case IndicatorsPackage.INDICATOR__DATAMINING_TYPE:
            return dataminingType != DATAMINING_TYPE_EDEFAULT;
        case IndicatorsPackage.INDICATOR__INDICATOR_DEFINITION:
            return indicatorDefinition != null;
        case IndicatorsPackage.INDICATOR__INSTANTIATED_EXPRESSIONS:
            return instantiatedExpressions != null && !instantiatedExpressions.isEmpty();
        case IndicatorsPackage.INDICATOR__COMPUTED:
            return computed != COMPUTED_EDEFAULT;
        case IndicatorsPackage.INDICATOR__JOIN_CONDITIONS:
            return joinConditions != null && !joinConditions.isEmpty();
        case IndicatorsPackage.INDICATOR__MAX_NUMBER_ROWS:
            return maxNumberRows != MAX_NUMBER_ROWS_EDEFAULT;
        case IndicatorsPackage.INDICATOR__VALID_ROW:
            return validRow != VALID_ROW_EDEFAULT;
        case IndicatorsPackage.INDICATOR__IN_VALID_ROW:
            return inValidRow != IN_VALID_ROW_EDEFAULT;
        case IndicatorsPackage.INDICATOR__STORE_DATA:
            return storeData != STORE_DATA_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * Method "getColumnType".
     * 
     * @return the column type of the analyzed object (when the analyzed object is a column). Otherwise, it returns
     * Types.JAVA_OBJECT.
     */
    protected int getColumnType() {
        int javaType = Types.JAVA_OBJECT; // default type
        ModelElement elt = this.getAnalyzedElement();
        if (elt != null) {
            TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(elt);
            if (col != null) {
                javaType = col.getSqlDataType().getJavaDataType();
                return javaType;
            }
            MetadataColumn mdCol = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(elt);
            if (mdCol != null) {
                javaType = Java2SqlType.getJavaTypeBySqlType(TalendTypeConvert.convertToJavaType(mdCol.getTalendType()));
            }
        }
        return javaType;
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
        result.append(" (count: ");
        result.append(count);
        result.append(", nullCount: ");
        result.append(nullCount);
        result.append(", dataminingType: ");
        result.append(dataminingType);
        result.append(", computed: ");
        result.append(computed);
        result.append(", maxNumberRows: ");
        result.append(maxNumberRows);
        result.append(", validRow: ");
        result.append(validRow);
        result.append(", inValidRow: ");
        result.append(inValidRow);
        result.append(", storeData: ");
        result.append(storeData);
        result.append(')');
        return result.toString();
    }

    /**
     * Getter for saveTempDataToFile.
     * 
     * @return the saveTempDataToFile
     */
    @Override
    public boolean isUsedMapDBMode() {
        return this.usedMapDBMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#getMapDB(java.lang.String)
     * 
     * Get Map for MapDB by spical dbName
     * 
     * @return null when current is not support MapDB
     */
    @Override
    public AbstractDB getMapDB(String dbName) {
        if (isUsedMapDBMode()) {
            if (StandardDBName.drillDown.name().equals(dbName) && drillDownMap != null && !drillDownMap.isClosed()) {
                return drillDownMap;
            }
            return initValueForDBMap(dbName);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.DataValidation#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(Object inputData) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#handleDrillDownData(java.lang.Object, java.lang.Object, int,
     * int, java.lang.String)
     */
    @Override
    public void handleDrillDownData(Object masterObject, Object currentObject, int columnCount, int currentIndex,
            String currentColumnName) {
        List<Object> rowData = drillDownMap.get(count);
        if (rowData == null) {
            rowData = new ArrayList<Object>();
            drillDownMap.put(count, rowData);
            this.drillDownRowCount++;
        }
        rowData.add(currentObject);
    }

    /**
     * Sets the usedMapDBMode.
     * 
     * @param usedMapDBMode the usedMapDBMode to set
     */
    @Override
    public void setUsedMapDBMode(boolean usedMapDBMode) {
        this.usedMapDBMode = usedMapDBMode;
    }

    /**
     * Getter for dirllDownLimitSize.
     * 
     * @return the dirllDownLimitSize
     */
    @Override
    public Long getDrillDownLimitSize() {
        Analysis analysis = AnalysisHelper.getAnalysis(this);
        if (analysis != null) {
            this.drillDownLimitSize = Long.valueOf(analysis.getParameters().getMaxNumberRows());
        }
        return this.drillDownLimitSize;
    }

    /**
     * Sets the dirllDownLimitSize.
     * 
     * @param dirllDownLimitSize the dirllDownLimitSize to set
     */
    @Override
    public void setDrillDownLimitSize(Long drillDownLimitSize) {
        this.drillDownLimitSize = drillDownLimitSize;
    }

    /**
     * Getter for dirllDownRowCount.
     * 
     * @return the dirllDownRowCount
     */
    public Long getDrillDownRowCount() {
        return this.drillDownRowCount;
    }

    /**
     * Sets the dirllDownRowCount.
     * 
     * @param dirllDownRowCount the dirllDownRowCount to set
     */
    public void setDrillDownRowCount(Long drillDownRowCount) {
        this.drillDownRowCount = drillDownRowCount;
    }

    /**
     * 
     * Reset drillDownRowCount
     */
    public void resetDrillDownRowCount() {
        this.drillDownRowCount = 0l;
    }

    /**
     * 
     * DirllDownRowCount if From the beginning of 0
     */
    protected boolean checkMustStorCurrentRow() {
        return checkMustStorCurrentRow(getDrillDownRowCount());

    }

    /**
     * 
     * DirllDownRowCount if From the beginning of 0
     */
    protected boolean checkMustStorCurrentRow(Long rowCount) {
        Long currentDrillDownLimit = getDrillDownLimitSize();
        if (currentDrillDownLimit == null || currentDrillDownLimit == 0l) {
            return true;
        }
        if (rowCount < currentDrillDownLimit) {
            return true;
        } else {
            return false;
        }
    }
} // IndicatorImpl
