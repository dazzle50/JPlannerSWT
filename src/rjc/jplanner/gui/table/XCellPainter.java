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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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
    public String  txt;
    public int     x;
    public int     y;
    public boolean ellipsis = false;
  }

  private static String ELLIPSIS     = "..."; // ellipsis to show text has been truncated
  private static int    CELL_PADDING = 5;     // cell padding for text left & right edges

  private Font          m_font;               // original graphics-context font 

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

    // prepare to paint cell text
    setFont( cell, gc );
    String text = (String) cell.getDataValue();
    Rectangle rect = getTextBounds( cell, bounds );

    if ( cell.getDisplayMode() == DisplayMode.SELECT )
      gc.setForeground( JPlanner.gui.COLOR_TEXT_SELECTED );
    else
      gc.setForeground( JPlanner.gui.COLOR_TEXT_NORMAL );

    // break text down into individual lines
    ArrayList<TextLine> lines = getTextLines( gc, text, rect, getTextAlignment( cell ) );

    // paint cell text
    for ( TextLine line : lines )
      gc.drawString( line.txt, line.x, line.y, true );
    disposeFont( gc );

    // paint decorations
    paintDecorations( gc, cell, bounds );
  }

  /****************************************** setFont ********************************************/
  private void setFont( ILayerCell cell, GC gc )
  {
    // update graphics-context with font with desired style
    int style = getTextStyle( cell );
    m_font = gc.getFont();
    Font newfont = FontDescriptor.createFrom( m_font ).setStyle( style ).createFont( gc.getDevice() );
    gc.setFont( newfont );
  }

  /***************************************** disposeFont *****************************************/
  private void disposeFont( GC gc )
  {
    // dispose of limited font resources
    gc.getFont().dispose();
    gc.setFont( m_font );
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
          line.ellipsis = true;
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
            line.ellipsis = true;

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

  /**************************************** getTextStyle *****************************************/
  protected int getTextStyle( ILayerCell cell )
  {
    // return bitwise combination of SWT.NORMAL, SWT.ITALIC and SWT.BOLD
    return SWT.NORMAL;
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
    return new Rectangle( bounds.x + CELL_PADDING, bounds.y, bounds.width - CELL_PADDING - CELL_PADDING,
        bounds.height );
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
    // determine smallest height that avoids or minimises ellipsis
    setFont( cell, gc );
    Rectangle bounds = cell.getBounds();
    bounds.width -= 1; // to make same as painting bounds
    bounds.height -= 1; // to make same as painting bounds
    Rectangle rect = getTextBounds( cell, bounds );
    int padding = bounds.height - rect.height;
    String text = (String) cell.getDataValue();
    if ( text == null )
      return padding;

    int minHeight = gc.stringExtent( ELLIPSIS ).y;
    int maxHeight = 999;

    rect.height = maxHeight;
    ArrayList<TextLine> lines = getTextLines( gc, text, rect, Alignment.LEFT );
    int maxChars = countNonWhitespace( lines );
    int charCount;

    // binary chop to find minimum height that shows max characters
    while ( maxHeight - minHeight > 1 )
    {
      rect.height = ( minHeight + maxHeight ) / 2;
      lines = getTextLines( gc, text, rect, Alignment.LEFT );
      charCount = countNonWhitespace( lines );

      if ( charCount < maxChars )
        minHeight = rect.height;
      else
        maxHeight = rect.height;
    }

    // return minimum height adjusted for padding
    disposeFont( gc );
    return maxHeight + padding;
  }

  /************************************ countNonWhitespace ***************************************/
  private int countNonWhitespace( ArrayList<TextLine> lines )
  {
    // count number of non-whitespace characters in the lines
    int count = 0;
    for ( TextLine line : lines )
    {
      for ( int i = 0; i < line.txt.length(); i++ )
        if ( !Character.isWhitespace( line.txt.charAt( i ) ) )
          count++;

      if ( line.ellipsis )
        count -= ELLIPSIS.length();
    }

    return count;
  }

  /************************************* getPreferredWidth ***************************************/
  @Override
  public int getPreferredWidth( ILayerCell cell, GC gc, IConfigRegistry configRegistry )
  {
    // determine smallest width that avoids ellipsis
    setFont( cell, gc );
    Rectangle bounds = cell.getBounds();
    bounds.width -= 1; // to make same as painting bounds
    bounds.height -= 1; // to make same as painting bounds
    Rectangle rect = getTextBounds( cell, bounds );
    int padding = bounds.width - rect.width;
    String text = (String) cell.getDataValue();
    if ( text == null )
      return padding;

    int minWidth = gc.stringExtent( ELLIPSIS ).x;
    int maxWidth = gc.stringExtent( text ).x;

    // binary chop to find minimum width with no ellipsis
    while ( maxWidth - minWidth > 1 )
    {
      rect.width = ( minWidth + maxWidth + 1 ) / 2;
      ArrayList<TextLine> lines = getTextLines( gc, text, rect, Alignment.LEFT );

      boolean noEllipsis = true;
      for ( TextLine line : lines )
        if ( line.ellipsis )
          noEllipsis = false;

      if ( noEllipsis )
        maxWidth = rect.width;
      else
        minWidth = rect.width;
    }

    // return minimum width adjusted for padding
    disposeFont( gc );
    return maxWidth + padding;
  }
}