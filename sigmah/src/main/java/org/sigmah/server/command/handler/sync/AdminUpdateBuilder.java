/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.command.handler.sync;

import java.util.List;

import org.json.JSONException;
import org.sigmah.server.database.hibernate.dao.AdminDAO;
import org.sigmah.server.database.hibernate.entity.AdminEntity;
import org.sigmah.server.database.hibernate.entity.User;
import org.sigmah.shared.command.GetSyncRegionUpdates;
import org.sigmah.shared.command.result.SyncRegionUpdate;

import com.bedatadriven.rebar.sync.server.JpaUpdateBuilder;
import com.google.inject.Inject;

public class AdminUpdateBuilder implements UpdateBuilder {
    private AdminDAO dao;
    protected int levelId;
    private AdminLocalState localState;
    private static final int LAST_VERSION_NUMBER = 1;
    protected JpaUpdateBuilder builder;


    @Inject
    public AdminUpdateBuilder(AdminDAO dao) {
        this.dao = dao;
    }

    public SyncRegionUpdate build(User user, GetSyncRegionUpdates request) throws JSONException {
        parseLevelId(request);
        localState = new AdminLocalState(request.getLocalVersion());

        SyncRegionUpdate update = new SyncRegionUpdate();
        builder = new JpaUpdateBuilder();

        if(localState.version < LAST_VERSION_NUMBER) {
            /**
             * This level is out of date, delete all on the client and send all from the server
             */
            builder.createTableIfNotExists(AdminEntity.class);
            builder.executeStatement("delete from AdminEntity where AdminLevelId=" + levelId);

            List<AdminEntity> entities = dao.query().level(levelId).execute();
            update.setSql(makeJson(entities));
            localState.complete = true;
            localState.version = LAST_VERSION_NUMBER;
        }

        update.setComplete(localState.complete);
        update.setVersion(localState.toString());

        return update;
    }

    private void parseLevelId(GetSyncRegionUpdates request) {
        levelId = Integer.parseInt(request.getRegionId().substring("admin/".length()));
    }

    private String makeJson(List<AdminEntity> entities) throws JSONException {
        builder.insert(AdminEntity.class, entities);
        return builder.asJson();
    }

}