/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlannerSWT                               *
 *                                                                        *
 *  This program is free software: you can redistribute it and/or modify  *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  This program is distributed in the hope that it will be useful,       *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with this program.  If not, see http://www.gnu.org/licenses/    *
 **************************************************************************/

package rjc.jplanner.gui.table;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/*********************** Label Accumulator for styling of individual cells ***********************/
/*************************************************************************************************/

public class TasksLabelAccumulator implements IConfigLabelAccumulator
{
  @Override
  public void accumulateConfigLabels( LabelStack labels, int col, int row )
  {
    // add config labels to style cell
    Task task = JPlanner.plan.task( row );

    // all cells editable unless task is null
    if ( task.isNull() )
    {
      if ( col == Task.SECTION_TITLE )
      {
        labels.addLabel( XNatTable.LABEL_CELL_EDITABLE );
        labels.addLabel( XNatTable.LABEL_TASK_EDITOR );
      }
      else
        labels.addLabel( XNatTable.LABEL_SHADE );
    }
    else
    {
      if ( task.type().isSectionEditable( col ) )
      {
        labels.addLabel( XNatTable.LABEL_CELL_EDITABLE );
        labels.addLabel( XNatTable.LABEL_TASK_EDITOR );
      }
      else
        labels.addLabel( XNatTable.LABEL_SHADE );
    }

    // left align some columns
    if ( col == Task.SECTION_TITLE || col == Task.SECTION_PRED || col == Task.SECTION_RES
        || col == Task.SECTION_COMMENT )
      labels.addLabel( XNatTable.LABEL_ALIGN_LEFT );

    // right align some columns
    if ( col == Task.SECTION_DURATION || col == Task.SECTION_WORK )
      labels.addLabel( XNatTable.LABEL_ALIGN_RIGHT );
  }
}
