package org.activityinfo.ui.full.client.importer.ui.validation.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Renders the difference between two strings, ignoring case
 */
public class StringDiffRenderer {


    private enum Style {
        MATCH,
        DELETED,
        SUBSTITUTED,
        INSERTED
    }

    private class DiffRenderer {

        private SafeHtmlBuilder html;
        private Style currentStyle = null;
        private StringBuilder span = new StringBuilder();
        private StringBuilder substituted = new StringBuilder();

        public DiffRenderer(SafeHtmlBuilder html) {
            this.html = html;
        }

        public void append(char c, Style style) {
            if(style != currentStyle) {
                appendSpan();
            }
            span.append(c);
            currentStyle = style;
        }

        public void finish() {
            appendSpan();
        }

        private void appendSpan() {
            if(span.length() > 0) {
                switch(currentStyle) {
                    case MATCH:
                        html.appendEscaped(span.toString());
                        break;
                    case DELETED:
                        html.append(templates.deleted(span.toString()));
                        break;
                    case INSERTED:
                        html.append(templates.inserted(span.toString()));
                        break;
                    case SUBSTITUTED:
                        html.append(templates.deleted(span.toString()));
                        html.append(templates.inserted(substituted.toString()));
                }
                span.setLength(0);
                substituted.setLength(0);
            }
        }

        public void append(char s, char t, Style style) {
            if(style != currentStyle) {
                appendSpan();
            }
            switch(style) {
                case MATCH:
                    span.append(s);
                    break;
                case INSERTED:
                    span.append(t);
                    break;
                case DELETED:
                    span.append(s);
                    break;
                case SUBSTITUTED:
                    span.append(s);
                    substituted.append(t);
                    break;
            }
            currentStyle = style;
        }
    }

    private final ValidationCellTemplates templates;

    public StringDiffRenderer() {
        this.templates = GWT.create(ValidationCellTemplates.class);
    }

    public StringDiffRenderer(ValidationCellTemplates templates) {
        this.templates = templates;
    }

    public void render(SafeHtmlBuilder html, String s, String t) {


        // for all i and j, d[i,j] will hold the Levenshtein distance between
        // the first i characters of s and the first j characters of t;
        // note that d has (m+1)*(n+1) values
        int m = s.length();
        int n = t.length();
        int d[][] = new int[m+1][n+1];
        Style[][] op = new Style[m+1][n+1];
        //declare int d[0..m, 0..n]

        //clear all elements in d // set each element to zero

        // source prefixes can be transformed into empty string by
        // dropping all characters
        for(int i=0;i<m;++i) {
            d[i+1][0] = i;
        }

        // target prefixes can be reached from empty source prefix
        // by inserting every characters
        for(int j=0;j<n;++j) {
            d[0][j+1] = j;
        }

        for(int j0=0;j0<n;++j0) {
            int j = j0 + 1;
            for(int i0=0;i0 < m;++i0) {
                int i = i0+1;

                if(Character.toLowerCase(s.charAt(i0)) == Character.toLowerCase(t.charAt(j0))) {
                    d[i][j] = d[i-1][j-1];
                    op[i][j] = Style.MATCH;
                } else {
                    int deletion = d[i-1][j] + 1;     // a deletion
                    int insertion = d[i][j-1] + 1;    // an insertion
                    int substitution = d[i-1][j-1] + 1; // a substitution
                    int min = min(deletion, insertion, substitution);

                    if(substitution == min) {
                        op[i][j] = Style.SUBSTITUTED;
                    } else if(deletion == min) {
                        op[i][j] = Style.DELETED;
                    } else if(insertion == min) {
                        op[i][j] = Style.INSERTED;
                    }
                    d[i][j] = min;
                }
            }
        }

        for(int i =1;i<=m;++i) {
            for(int j=1;j<n;++j) {
                System.out.print(d[i][j]);
            }
            System.out.print("  ");
            for(int j=1;j<n;++j) {
                System.out.print(op[i][j].name().substring(0,1));
            }
            System.out.println();
        }


        DiffRenderer diff = new DiffRenderer(html);

        int deltas[][] = new int[][] {
                new int[] { 1, 1 },
                new int[] { 1, 0 },
                new int[] { 0, 1 }
        };


        int i = 1;
        int j = 1;
        while(true) {

            diff.append(s.charAt(i-1), t.charAt(j-1), op[i][j]);

            int min = Integer.MAX_VALUE;
            for(int di=0;di!=deltas.length;++di) {
                if( i + deltas[di][0] <= m && j + deltas[di][1] <= n) {
                    int newI = i + deltas[di][0];
                    int newJ = j + deltas[di][1];
                    min = Math.min(min, d[newI][newJ] );
                }
            }

            if(min == Integer.MAX_VALUE) {
                break;
            }

            for(int di=0;di!=deltas.length;++di) {
                if( i + deltas[di][0] <= m && j + deltas[di][1] <= n) {
                    int newI = i + deltas[di][0];
                    int newJ = j + deltas[di][1];
                    if( d[newI][newJ] == min ) {
                        i = newI;
                        j = newJ;
                        break;
                    }
                }
            }

        }

        diff.finish();
    }

    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}
