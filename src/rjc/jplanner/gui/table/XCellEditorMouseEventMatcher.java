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

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.swt.events.MouseEvent;

import rjc.jplanner.JPlanner;
import rjc.jplanner.command.CommandSummaryCollapse;
import rjc.jplanner.command.CommandSummaryExpand;
import rjc.jplanner.gui.table.XNatTable.TableType;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/***************** Extended version of CellEditorMouseEventMatcher which handles *****************/
/****************************** tasks summary expand/collapse mark *******************************/
/*************************************************************************************************/

public class XCellEditorMouseEventMatcher extends CellEditorMouseEventMatcher
{

  /**************************************** constructor ******************************************/
  public XCellEditorMouseEventMatcher( String body )
  {
    super( body );
  }

  /****************************************** matches ********************************************/
  @Override
  public boolean matches( NatTable natTable, MouseEvent event, LabelStack regionLabels )
  {
    // if CellEditorMouseEventMatcher says false, no need to check anymore
    if ( !super.matches( natTable, event, regionLabels ) )
      return false;

    // only interested if task table
    XNatTable table = (XNatTable) natTable;
    if ( table.type() != TableType.TASK )
      return true;

    // only interested if title column
    int xPos = table.getColumnPositionByX( event.x );
    int col = table.getColumnIndexByPosition( xPos );
    if ( col != Task.SECTION_TITLE )
      return true;

    // only interested if summary task
    int yPos = table.getRowPositionByY( event.y );
    int row = table.getRowIndexByPosition( yPos );
    Task task = JPlanner.plan.task( row );
    if ( !task.isSummary() )
      return true;

    // only interested if click on summary expand/collapse mark
    if ( !TaskCellPainter.expandCollapseMark( task.indent(), event.x, event.y,
        table.getBoundsByPosition( xPos, yPos ) ) )
      return true;

    // toggle showing summary sub-tasks
    if ( table.isRowHidden( row + 1 ) )
      // expand
      JPlanner.plan.undostack().push( new CommandSummaryExpand( table, row ) );
    else
      // collapse
      JPlanner.plan.undostack().push( new CommandSummaryCollapse( table, row ) );

    // and say no to opening editor
    return false;
  }

}
