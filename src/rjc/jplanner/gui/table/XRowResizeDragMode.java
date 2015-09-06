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
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeDetectUtil;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/*************************************************************************************************/
/************* Based on RowResizeDragMode but smooth row resize instead of overlay ***************/
/*************************************************************************************************/

public class XRowResizeDragMode implements IDragMode
{
  private int              m_row;
  private int              m_originalHeight;
  private int              m_originalY;

  private static final int MIN_ROW_HEIGHT = 17;

  /****************************************** mouseDown ******************************************/
  @Override
  public void mouseDown( NatTable nattable, MouseEvent event )
  {
    // determine which row is being resized
    nattable.forceFocus();
    m_row = CellEdgeDetectUtil.getRowPositionToResize( nattable, new Point( event.x, event.y ) );

    if ( m_row >= 0 )
    {
      m_originalHeight = nattable.getRowHeightByPosition( m_row );
      m_originalY = event.y;
    }
  }

  /****************************************** mouseMove ******************************************/
  @Override
  public void mouseMove( NatTable nattable, MouseEvent event )
  {
    // determine new height
    int height = m_originalHeight + event.y - m_originalY;

    // don't allow height to go below minimum
    if ( height < MIN_ROW_HEIGHT )
      height = MIN_ROW_HEIGHT;

    // update table row height and trigger redraw
    nattable.doCommand( new RowResizeCommand( nattable, m_row, height ) );
    nattable.redraw();
  }

  /******************************************* mouseUp *******************************************/
  @Override
  public void mouseUp( NatTable nattable, MouseEvent event )
  {
    // nothing to do
  }

}
