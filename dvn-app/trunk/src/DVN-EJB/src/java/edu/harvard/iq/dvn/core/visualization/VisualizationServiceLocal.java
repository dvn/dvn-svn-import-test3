/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skraffmiller
 */
public interface VisualizationServiceLocal extends java.io.Serializable {

    public List getGroupings(Long dataTableId);

    public void updateGroupings(List <VarGrouping> groupings);
    public boolean validateGroupings(Long dataTableId);
    public boolean validateVariableMappings(Long dataTableId);
    public List getVariableMappings(Long dataTableId);
    public List getGroupsFromGroupTypeId(Long groupTypeId);
    public List getGroupTypesFromGroupId(Long groupId);
    public List getGroupsFromGroupingId(Long groupingId);
    public List getGroupTypesFromGroupingId(Long groupingId);
    public List<VarGroupType> getGroupTypes(Long dataTableId);
    public List<VarGroupType> getFilterGroupTypes(Long dataTableId);
    public VarGrouping getGroupingFromId(Long groupingId);
    public VarGroup getGroupFromId(Long groupId);
    public DataVariable getXAxisVariable(Long dataTableId);
    public List getDataVariableMappingsFromGroupId(Long groupId);
    public void saveMeasureGrouping(VarGrouping varGrouping);
    public List getFilterGroupsFromMeasureId(Long measureId);
    public List getFilterGroupingsFromMeasureId(Long measureId);

    public void removeCollectionElement(Collection coll, Object elem);

    public void removeCollectionElement(List list, int index);

    public void removeCollectionElement(Iterator iter, Object elem);

    public void setDataTable(java.lang.Long dataTableId);
    public DataTable getDataTable();


}
