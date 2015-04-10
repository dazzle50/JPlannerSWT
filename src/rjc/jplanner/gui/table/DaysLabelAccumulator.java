/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlanner                                  *
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
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/*********************** Label Accumulator for styling of individual cells ***********************/
/*************************************************************************************************/

public class DaysLabelAccumulator implements IConfigLabelAccumulator
{
  @Override
  public void accumulateConfigLabels( LabelStack labels, int col, int row )
  {
    // add config labels to style cell
    Day day = JPlanner.plan.day( row );

    // all cells editable except shaded unused start/end cells
    if ( col < day.numPeriods() * 2 + Day.SECTION_START1 )
    {
      labels.addLabel( XNatTable.LABEL_CELL_EDITABLE );
      labels.addLabel( XNatTable.LABEL_DAY_EDITOR );
    }
    else
      labels.addLabel( XNatTable.LABEL_SHADE );

    // left align name
    if ( col == Day.SECTION_NAME )
      labels.addLabel( XNatTable.LABEL_ALIGN_LEFT );
  }

}
