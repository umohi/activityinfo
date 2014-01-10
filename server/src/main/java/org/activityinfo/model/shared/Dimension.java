package org.activityinfo.model.shared;

/**
 * A Dimension in a set of 
 */
public interface Dimension {
	
	/**
	 * 
	 * @return true if the dimensions have a meaningful, natural order
	 */
	boolean isOrdered();

	/**
	 * 
	 * @return this dimension's parent or null if this dimension is not part of a heirarchy
	 */
	DimensionRef getParentDimension();

}
