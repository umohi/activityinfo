package org.activityinfo.server.database.hibernate.entity;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alex Bertram
 */
@Entity
@org.hibernate.annotations.Filter(
        name = "hideDeleted",
        condition = "DateDeleted is null")
public class AttributeGroup implements Serializable, Deleteable, Orderable {

    private int id;

    private String name;
    private Set<Attribute> attributes = new HashSet<Attribute>(0);

    private Set<Activity> activities = new HashSet<Activity>(0);

    private int sortOrder;
    private boolean multipleAllowed;

    private String category;

    private Date dateDeleted;

    private boolean mandatory;


    public AttributeGroup() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "AttributeGroupId")
    public int getId() {
        return this.id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "AttributeGroupInActivity",
            joinColumns = {
                    @JoinColumn(name = "AttributeGroupId", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "ActivityId", nullable = false, updatable = false)})
    public Set<Activity> getActivities() {
        return this.activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @org.hibernate.annotations.OrderBy(clause = "SortOrder")
    @org.hibernate.annotations.Filter(name = "hideDeleted", condition = "DateDeleted is null")
    public Set<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    @Column(nullable = false)
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(int order) {
        this.sortOrder = order;
    }

    @Column(nullable = false)
    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    public void setMultipleAllowed(boolean allowed) {
        this.multipleAllowed = allowed;
    }

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getDateDeleted() {
        return this.dateDeleted;
    }

    public void setDateDeleted(Date date) {
        this.dateDeleted = date;
    }

    /**
     * Indicates if the attributegroup is mandatory in the new and edit site forms.
     *
     * @return True if attributegroup is mandatory, false otherwise
     */
    @Column(name = "mandatory", nullable = false)
    public boolean isMandatory() {
        return this.mandatory;
    }

    /**
     * Sets the mandatory flag
     *
     * @param mandatory True if the attributegroup is mandatory in the new and edit site forms.
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public void delete() {
        this.setDateDeleted(new Date());
        getActivities().iterator().next().getDatabase()
                .setLastSchemaUpdate(new Date());
    }

    @Column(name = "category", length = 50, nullable = true)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    @Transient
    public boolean isDeleted() {
        return getDateDeleted() == null;
    }
}
