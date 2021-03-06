package org.talend.cwm.compare.ui.service;

import org.talend.core.ITDQCompareService;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.cwm.compare.ui.actions.ReloadDatabaseAction;
import org.talend.cwm.compare.ui.actions.provider.ReloadDatabaseProvider;
import org.talend.utils.sugars.ReturnCode;

public class TDQCompareService implements ITDQCompareService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ITDQCompareService#reloadDatabase(org.talend.core.model.properties.ConnectionItem)
     */
    /**
     * 
     * Comment method "reloadDatabase".
     * 
     * @param connectionItem
     * 
     */
    public ReturnCode reloadDatabase(ConnectionItem connectionItem) {
        ReturnCode retCode = new ReturnCode(Boolean.TRUE);
        Connection conn = connectionItem.getConnection();
        if (conn instanceof DatabaseConnection) {
            // MOD TDQ-7528 20130627 yyin: no need to popup select compare dialog
            ReloadDatabaseAction reloadDatabaseAction = new ReloadDatabaseAction(conn,
                    ReloadDatabaseProvider.RELOADDATABASE_MENUTEXT, false);
            reloadDatabaseAction.run();
            retCode = reloadDatabaseAction.getReturnCode();

        }
        return retCode;
    }

}
