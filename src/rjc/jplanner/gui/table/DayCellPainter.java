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

import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.Color;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/************************ Responsible for painting a Day-type table cell *************************/
/*************************************************************************************************/

public class DayCellPainter extends XCellPainter
{

  /**************************************** getBackground ****************************************/
  @Override
  protected Color getBackground( ILayerCell cell )
  {
    // cell colour depends on if cell editable or not
    int col = cell.getColumnIndex();
    Day day = JPlanner.plan.day( cell.getRowIndex() );

    if ( col < day.numPeriods() * 2 + Day.SECTION_START1 )
      return JPlanner.gui.COLOR_CELL_ENABLED;

    return JPlanner.gui.COLOR_CELL_DISABLED;
  }

  /************************************** getTextAlignment ***************************************/
  @Override
  protected Alignment getTextAlignment( ILayerCell cell )
  {
    // text alignment depends on column
    int col = cell.getColumnIndex();

    if ( col == Day.SECTION_NAME )
      return Alignment.LEFT;

    return Alignment.MIDDLE;
  }

}
