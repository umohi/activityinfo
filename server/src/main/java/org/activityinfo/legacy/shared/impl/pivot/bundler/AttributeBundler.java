package org.activityinfo.legacy.shared.impl.pivot.bundler;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import org.activityinfo.reports.shared.content.AttributeCategory;
import org.activityinfo.reports.shared.model.Dimension;
import org.activityinfo.legacy.shared.command.result.Bucket;

public class AttributeBundler implements Bundler {
    private final Dimension dimension;
    private final String valueColumnAlias;
    private final String orderColumnAlias;

    public AttributeBundler(Dimension dimension, String valueColumnAlias,
                            String orderColumnAlias) {
        super();
        this.dimension = dimension;
        this.valueColumnAlias = valueColumnAlias;
        this.orderColumnAlias = orderColumnAlias;
    }

    @Override
    public void bundle(SqlResultSetRow row, Bucket bucket) {
        if (!row.isNull(valueColumnAlias)) {
            bucket.setCategory(
                    dimension,
                    new AttributeCategory(row.getString(valueColumnAlias), row
                            .getInt(orderColumnAlias)));
        }
    }

}
