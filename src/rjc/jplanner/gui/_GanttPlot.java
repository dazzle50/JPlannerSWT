/**************************************************************************
 *  ######## WRITTEN USING WindowBuilder Editor ########                  *
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  http://code.google.com/p/jplanner/                                    *
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

package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.DateTime;

/*************************************************************************************************/
/***************** GanttPlot provides a view of the plan tasks and dependencies ******************/
/*************************************************************************************************/

public class _GanttPlot extends Composite
{
  private DateTime m_start;
  private double   m_secsPP;

  private int      m_color;

  /**************************************** constructor ******************************************/
  public _GanttPlot( Composite parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );

    m_color = this.hashCode() % 200;

    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent e )
      {
        // TODO Auto-generated method stub
        JPlanner.trace( e.toString() );

        int x = e.x;
        int y = e.y;
        int h = e.height;
        int w = e.width;
        GC gc = e.gc;

        //gc.drawString( "X", x, y, true );
        Color color = new Color( gc.getDevice(), ++m_color, m_color, m_color );
        if ( m_color > 253 )
          m_color = 0;
        gc.setBackground( color );
        gc.fillRectangle( x, y, w, h );

      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }
}
