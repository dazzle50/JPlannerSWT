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
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeDetectUtil;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/*************************************************************************************************/
/********** Based on ColumnResizeDragMode but smooth column resize instead of overlay ************/
/*************************************************************************************************/

public class XColumnResizeDragMode implements IDragMode
{
  private int              m_column;
  private int              m_originalWidth;
  private int              m_originalX;

  private static final int MIN_COLUMN_WIDTH = 25;

  /****************************************** mouseDown ******************************************/
  @Override
  public void mouseDown( NatTable nattable, MouseEvent event )
  {
    // determine which column is being resized
    nattable.forceFocus();
    m_column = CellEdgeDetectUtil.getColumnPositionToResize( nattable, new Point( event.x, event.y ) );

    if ( m_column >= 0 )
    {
      m_originalWidth = nattable.getColumnWidthByPosition( m_column );
      m_originalX = event.x;
    }
  }

  /****************************************** mouseMove ******************************************/
  @Override
  public void mouseMove( NatTable nattable, MouseEvent event )
  {
    // determine new width
    int width = m_originalWidth + event.x - m_originalX;

    // don't allow width to go below minimum
    if ( width < MIN_COLUMN_WIDTH )
      width = MIN_COLUMN_WIDTH;

    // update table column width and trigger redraw
    nattable.doCommand( new ColumnResizeCommand( nattable, m_column, width ) );
    nattable.redraw();
  }

  /******************************************* mouseUp *******************************************/
  @Override
  public void mouseUp( NatTable nattable, MouseEvent event )
  {
    // nothing to do
  }

}
