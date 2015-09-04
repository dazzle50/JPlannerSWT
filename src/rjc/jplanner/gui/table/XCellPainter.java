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

import java.util.ArrayList;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/***************************** Base class for painting table cells *******************************/
/*************************************************************************************************/

public class XCellPainter implements ICellPainter
{
  // struct that contains one line of text to be drawn in cell
  public class TextLine
  {
    public String txt;
    public int    x;
    public int    y;
  }

  private static String ELLIPSIS     = "...";
  private static int    CELL_PADDING = 5;

  public enum Alignment
  {
    LEFT, MIDDLE, RIGHT
  }

  /***************************************** paintCell *******************************************/
  @Override
  public void paintCell( ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry )
  {
    // paint cell background
    if ( cell.getDisplayMode() == DisplayMode.SELECT )
      gc.setBackground( JPlanner.gui.COLOR_CELL_SELECTED );
    else
      gc.setBackground( getBackground( cell ) );
    gc.fillRectangle( bounds );

    // paint cell text
    String text = (String) cell.getDataValue();
    Rectangle rect = getTextBounds( cell, bounds );
    ArrayList<TextLine> lines = getTextLines( gc, text, rect, getTextAlignment( cell ) );

    if ( cell.getDisplayMode() == DisplayMode.SELECT )
      gc.setForeground( JPlanner.gui.COLOR_WHITE );
    else
      gc.setForeground( JPlanner.gui.COLOR_BLACK );
    for ( TextLine line : lines )
      gc.drawString( line.txt, line.x, line.y, true );

    // paint decorations
    paintDecorations( gc, cell, bounds );
  }

  /**************************************** getTextLines *****************************************/
  private ArrayList<TextLine> getTextLines( GC gc, String text, Rectangle rect, Alignment alignment )
  {
    // initialise variables
    ArrayList<TextLine> lines = new ArrayList<TextLine>();
    int x = rect.x;
    int y = rect.y;
    Point extent;
    int alignX = 0;
    int alignY = 0;

    // determine how text needs to be split into lines
    while ( text != null )
    {
      TextLine line = new TextLine();
      extent = gc.stringExtent( text );

      if ( extent.x <= rect.width )
      {
        // text width fits in cell
        if ( alignment == Alignment.RIGHT )
          alignX = rect.width - extent.x;
        if ( alignment == Alignment.MIDDLE )
          alignX = ( rect.width - extent.x ) / 2;

        alignY = ( rect.height - ( y + extent.y - rect.y ) ) / 2;

        line.txt = text;
        line.x = x + alignX;
        line.y = y;
        text = null;
      }
      else
      {
        // text width exceeds cell width
        boolean isLastLine = ( rect.y + rect.height ) < ( y + extent.y + extent.y );
        if ( isLastLine )
        {
          // last line so fit as much as possible with ellipsis on end
          int cut = text.length();
          try
          {
            do
            {
              cut--;
              extent = gc.stringExtent( text.substring( 0, cut ) + ELLIPSIS );
            }
            while ( extent.x > rect.width );
          }
          catch ( StringIndexOutOfBoundsException tooLittleSpace )
          {
            cut = 0;
          }

          if ( alignment == Alignment.RIGHT )
            alignX = rect.width - extent.x;
          if ( alignment == Alignment.MIDDLE )
            alignX = ( rect.width - extent.x ) / 2;

          alignY = ( rect.height - ( y + extent.y - rect.y ) ) / 2;

          line.txt = text.substring( 0, cut ) + ELLIPSIS;
          line.x = x + alignX;
          line.y = y;
          text = null;
        }
        else
        {
          // not last line so break at word end
          int cut = text.length();
          try
          {
            do
            {
              cut--;
              extent = gc.stringExtent( text.substring( 0, cut ) );
            }
            while ( extent.x > rect.width || !Character.isWhitespace( text.charAt( cut ) ) );

            if ( alignment == Alignment.RIGHT )
              alignX = rect.width - extent.x;
            if ( alignment == Alignment.MIDDLE )
              alignX = ( rect.width - extent.x ) / 2;

            line.txt = text.substring( 0, cut );
            line.x = x + alignX;
            line.y = y;

            while ( Character.isWhitespace( text.charAt( cut ) ) )
              cut++;
            text = text.substring( cut );
          }
          catch ( StringIndexOutOfBoundsException wordTooLong )
          {
            // even one word is too long so don't bother looking for word end
            cut = text.length();
            do
            {
              cut--;
              extent = gc.stringExtent( text.substring( 0, cut ) + ELLIPSIS );
            }
            while ( extent.x > rect.width );

            if ( alignment == Alignment.RIGHT )
              alignX = rect.width - extent.x;
            if ( alignment == Alignment.MIDDLE )
              alignX = ( rect.width - extent.x ) / 2;

            line.txt = text.substring( 0, cut ) + ELLIPSIS;
            line.x = x + alignX;
            line.y = y;

            try
            {
              while ( !Character.isWhitespace( text.charAt( cut ) ) )
                cut++;
              while ( Character.isWhitespace( text.charAt( cut ) ) )
                cut++;
              text = text.substring( cut );
            }
            catch ( StringIndexOutOfBoundsException noWordsRemain )
            {
              alignY = ( rect.height - ( y + extent.y - rect.y ) ) / 2;
              text = null;
            }
          }
        }
      }

      lines.add( line );
      y += extent.y;
    }

    // move all lines down to vertically centre within cell
    for ( TextLine line : lines )
      line.y += alignY;

    return lines;
  }

  /************************************** getTextAlignment ***************************************/
  protected Alignment getTextAlignment( ILayerCell cell )
  {
    // return default alignment
    return Alignment.LEFT;
  }

  /**************************************** getTextBounds ****************************************/
  protected Rectangle getTextBounds( ILayerCell cell, Rectangle bounds )
  {
    // return bounds with default padding
    return new Rectangle( bounds.x + CELL_PADDING, bounds.y, bounds.width - CELL_PADDING - CELL_PADDING, bounds.height );
  }

  /**************************************** getBackground ****************************************/
  protected Color getBackground( ILayerCell cell )
  {
    // return default cell background colour
    return JPlanner.gui.COLOR_CELL_ENABLED;
  }

  /************************************** paintDecorations ***************************************/
  protected void paintDecorations( GC gc, ILayerCell cell, Rectangle bounds )
  {
    // default nothing    
  }

  /************************************** getCellPainterAt ***************************************/
  @Override
  public ICellPainter getCellPainterAt( int x, int y, ILayerCell cell, GC gc, Rectangle adjustedCellBounds,
      IConfigRegistry configRegistry )
  {
    // TODO Auto-generated method stub
    JPlanner.trace( "XCellPainter - getCellPainterAt()" );
    return null;
  }

  /************************************* getPreferredHeight **************************************/
  @Override
  public int getPreferredHeight( ILayerCell cell, GC gc, IConfigRegistry configRegistry )
  {
    // TODO Auto-generated method stub
    JPlanner.trace( "XCellPainter - getPreferredHeight()" );
    return 0;
  }

  /************************************* getPreferredWidth ***************************************/
  @Override
  public int getPreferredWidth( ILayerCell cell, GC gc, IConfigRegistry configRegistry )
  {
    // TODO Auto-generated method stub
    JPlanner.trace( "XCellPainter - getPreferredWidth()" );
    return 0;
  }

}