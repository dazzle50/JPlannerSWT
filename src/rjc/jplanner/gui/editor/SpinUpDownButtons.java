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

package rjc.jplanner.gui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/*************************** Simple custom widget with up & down button **************************/
/*************************************************************************************************/

public class SpinUpDownButtons extends Composite
{

  /**************************************** constructor ******************************************/
  public SpinUpDownButtons( Composite parent )
  {
    super( parent, SWT.NO_FOCUS );

    // add paint listener to paint the up & down buttons
    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent event )
      {
        Composite area = SpinUpDownButtons.this;
        GC gc = event.gc;
        int w = area.getSize().x - 3;
        int h = ( area.getSize().y - 3 ) / 2;

        // draw the button dividing lines
        gc.setForeground( JPlanner.gui.COLOR_GANTT_DIVIDER );
        gc.drawRectangle( 1, 1, w, h );
        gc.drawRectangle( 1, h + 2, w, h );

        // draw up & down triangles
        gc.setForeground( JPlanner.gui.COLOR_BLACK );
        int y1 = 3;
        int y2 = h;
        if ( y2 - y1 > w ) // prevent triangles being taller than wide
        {
          int delta = y2 - y1 - w;
          y1 += delta / 2;
          y2 -= delta / 2;
        }
        for ( int y = y1; y < y2; y++ )
        {
          int size = ( y - y1 ) * ( w - 3 ) / ( y2 - y1 ) / 2;
          int x1 = w / 2 - size + 1;
          int x2 = w / 2 + size + 1;
          gc.drawLine( x1, y, x2, y );
          gc.drawLine( x1, h * 2 - y + 3, x2, h * 2 - y + 3 );
        }
      }
    } );

  }

  @Override
  public Point computeSize( int wHint, int hHint, boolean changed )
  {
    // only horizontal size is important, as vertically it stretches
    return new Point( 17, 1 );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
