package org.activityinfo.graph.shared;

import arq.bindings;
import arq.sparql;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Simple DSL for building SPARQL queries
 */
public class Sparql {

    private String[] bindings;
    private List<Matcher> matchers = Lists.newArrayList();


    public static String uri(String uri) {
        return "<" + uri + ">";
    }

    private static class Matcher {
        String subject;
        String property;
        String object;

        private Matcher(String subject, String property, String object) {
            this.subject = subject;
            this.property = property;
            this.object = object;
        }

        @Override
        public String toString() {
            return subject + " " + property + " " + object;
        }

        private String toString(String node) {
            if(node.startsWith("?")) {
                return node;
            } else {
                return "<" + node + ">";
            }
        }
    }

    public static Sparql select(String... bindings) {
        Sparql sparql = new Sparql();
        sparql.bindings = bindings;
        return sparql;
    }

    public Sparql where(String subject, String property, String object) {
        matchers.add(new Matcher(subject, property, object));
        return this;
    }

    public String toString() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        for(String binding : bindings) {
            query.append(" ").append(binding);
        }
        if(!matchers.isEmpty()) {
            query.append(" WHERE { ");
            boolean needsDot = false;
            for(Matcher matcher : matchers) {
                if(needsDot) {
                    query.append(" . ");
                }
                query.append(matcher);
                needsDot = true;
            }
            query.append("}");
        }
        return query.toString();
    }
}
