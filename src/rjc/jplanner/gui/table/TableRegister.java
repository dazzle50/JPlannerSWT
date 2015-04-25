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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;

/*************************************************************************************************/
/****************** Distributes table refresh requests to all registered tables ******************/
/*************************************************************************************************/

public class TableRegister
{
  private ArrayList<XNatTable> m_tables = new ArrayList<XNatTable>();

  /***************************************** addListener *****************************************/
  public void register( XNatTable toAdd )
  {
    // register each table in array-list
    m_tables.add( toAdd );
  }

  /******************************************* refresh *******************************************/
  public void refresh()
  {
    // refresh each table that has been registered
    Iterator<XNatTable> iter = m_tables.iterator();
    while ( iter.hasNext() )
    {
      // if table has been disposed, remove from list and skip
      XNatTable table = iter.next();
      if ( table.isDisposed() )
      {
        iter.remove();
        continue;
      }

      // if cell selected, need to re-select after refresh
      PositionCoordinate pc = table.selectionLayer.getLastSelectedCellPosition();
      if ( pc != null )
      {
        int col = pc.columnPosition;
        int row = pc.rowPosition;
        table.refresh();
        table.doCommand( new SelectCellCommand( table.selectionLayer, col, row, false, false ) );
      }
      else
        table.refresh();
    }

  }

}
