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

/*************************************************************************************************/
/*********************** Label Accumulator for styling of individual cells ***********************/
/*************************************************************************************************/

public class TasksLabelAccumulator implements IConfigLabelAccumulator
{

  /*********************************** accumulateConfigLabels ************************************/
  @Override
  public void accumulateConfigLabels( LabelStack labels, int col, int row )
  {
    // add config labels to style cell
    labels.addLabel( XNatTable.LABEL_TASK_PAINTER );

    // if cell is editable, add editable and task editor labels
    if ( JPlanner.plan.task( row ).isSectionEditable( col ) )
    {
      labels.addLabel( XNatTable.LABEL_CELL_EDITABLE );
      labels.addLabel( XNatTable.LABEL_TASK_EDITOR );
    }
  }
}
