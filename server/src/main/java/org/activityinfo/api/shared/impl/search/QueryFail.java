package org.activityinfo.api.shared.impl.search;

import org.activityinfo.ui.full.client.i18n.I18N;

import java.io.Serializable;

/**
* Created by alex on 1/23/14.
*/
public interface QueryFail extends Serializable {

    public String fail();

    public abstract class Abstract implements QueryFail {
        private String reason;

        public Abstract(String reason) {
            super();
            this.reason = reason;
        }

        @Override
        public String fail() {
            return reason;
        }
    }

    public static final class EmptyQuery extends Abstract {
        public EmptyQuery() {
            super(I18N.MESSAGES.searchQueryEmpty());
        }
    }

}
