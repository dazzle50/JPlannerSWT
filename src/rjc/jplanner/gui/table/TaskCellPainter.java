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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/************************** Responsible for painting a Task table cell ***************************/
/*************************************************************************************************/

public class TaskCellPainter extends XCellPainter
{
  private static int INDENT_SIZE    = 14;
  private static int INDENT_INITIAL = 14;

  /**************************************** getTextBounds ****************************************/
  @Override
  protected Rectangle getTextBounds( ILayerCell cell, Rectangle bounds )
  {
    // adjust text bounds for task title indenting
    Rectangle rect = super.getTextBounds( cell, bounds );

    if ( cell.getColumnIndex() == Task.SECTION_TITLE )
    {
      int indent = JPlanner.plan.task( cell.getRowIndex() ).indent();
      rect.x += INDENT_INITIAL + indent * INDENT_SIZE;
      rect.width -= INDENT_INITIAL + indent * INDENT_SIZE;
    }

    return rect;
  }

  /**************************************** getBackground ****************************************/
  @Override
  protected Color getBackground( ILayerCell cell )
  {
    // cell colour depends on if cell editable or not
    Task task = JPlanner.plan.task( cell.getRowIndex() );

    if ( task.isSectionEditable( cell.getColumnIndex() ) )
      return JPlanner.gui.COLOR_CELL_ENABLED;

    return JPlanner.gui.COLOR_CELL_DISABLED;
  }

  /************************************** paintDecorations ***************************************/
  @Override
  protected void paintDecorations( GC gc, ILayerCell cell, Rectangle bounds )
  {
    // draw summary task expand-collapse marks
    if ( cell.getColumnIndex() != Task.SECTION_TITLE )
      return;

    Task task = JPlanner.plan.task( cell.getRowIndex() );
    if ( !task.isSummary() )
      return;

    int indent = task.indent();
    int x = bounds.x + ( INDENT_INITIAL / 3 ) + indent * INDENT_SIZE;
    int y = bounds.y + ( bounds.height / 2 );

    gc.setForeground( JPlanner.gui.COLOR_GRAY_DARK );
    gc.drawRectangle( x, y - 4, 8, 8 );
    gc.drawLine( x + 2, y, x + 6, y );
    gc.drawLine( x + 4, y - 2, x + 4, y + 2 );
  }

  /************************************** getTextAlignment ***************************************/
  @Override
  protected Alignment getTextAlignment( ILayerCell cell )
  {
    // text alignment depends on column
    int col = cell.getColumnIndex();

    if ( col == Task.SECTION_DURATION || col == Task.SECTION_WORK )
      return Alignment.RIGHT;

    if ( col == Task.SECTION_TITLE || col == Task.SECTION_PRED || col == Task.SECTION_RES
        || col == Task.SECTION_COMMENT )
      return Alignment.LEFT;

    return Alignment.MIDDLE;
  }

}
