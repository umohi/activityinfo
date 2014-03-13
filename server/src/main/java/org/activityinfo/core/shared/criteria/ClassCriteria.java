package org.activityinfo.core.shared.criteria;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Iri;
import org.activityinfo.core.shared.form.FormInstance;

import java.util.List;
import java.util.Set;

/**
 * Spike for instance criteria. Defines the criteria
 * for the query as FormInstances that are classes of the
 * given Iri.
 */
public class ClassCriteria implements Criteria {

    private Iri classIri;


    public ClassCriteria(Iri classIri) {
        this.classIri = classIri;
    }

    public ClassCriteria(Cuid cuid) {
        this.classIri = cuid.asIri();
    }

    public Iri getClassIri() {
        return classIri;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitClassCriteria(this);
    }

    @Override
    public boolean apply(FormInstance input) {
        return classIri.equals(input.getClassId().asIri());
    }

    public static Criteria union(Set<Cuid> range) {
        if(range.size() == 1) {
            return new ClassCriteria(range.iterator().next());
        } else {
            List<ClassCriteria> criteriaList = Lists.newArrayList();
            for(Cuid classCuid : range) {
                criteriaList.add(new ClassCriteria(classCuid));
            }
            return new CriteriaUnion(criteriaList);
        }
    }
}
