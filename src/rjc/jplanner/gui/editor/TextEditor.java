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

package rjc.jplanner.gui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/******************************** Replacement for SWT Text widget ********************************/
/*************************************************************************************************/

public class TextEditor extends Composite
{
  protected Text m_prime;   // control which accepts the key presses etc
  private int    m_height;
  private int    m_minWidth;

  /**************************************** constructor ******************************************/
  public TextEditor( Composite parent )
  {
    // build composite with grid-layout
    super( parent, SWT.NONE );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    setLayout( gridLayout );
    setBackground( JPlanner.gui.COLOR_WHITE );

    // text editor, set selection to position zero so beginning of text is always shown
    m_prime = new Text( this, SWT.SINGLE );
    m_prime.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 ) );
    m_prime.setSelection( 0 );

    // right-hand edge, one-pixel width vertical line
    Composite edge = new Composite( this, SWT.NONE)
    {
      @Override
      public Point computeSize( int wHint, int hHint, boolean changed )
      {
        // only horizontal size is important, as vertically it stretches
        return new Point( 1, 1 );
      }
    };
    edge.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, false, true, 1, 1 ) );
    edge.setBackground( JPlanner.gui.COLOR_GRAY_DARK );

    // listen to modify to ensure editor width is sufficient to show whole text
    m_prime.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( ModifyEvent event )
      {
        TextEditor.this.setSize( widthForText(), m_height );
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  @Override
  public void addFocusListener( FocusListener listener )
  {
    // interested in when Text editor loses focus, not the extended composite
    m_prime.addFocusListener( listener );
  }

  @Override
  public void addTraverseListener( TraverseListener listener )
  {
    // interested in when Text editor hears tab, not the extended composite
    m_prime.addTraverseListener( listener );
  }

  @Override
  public void setBounds( Rectangle rect )
  {
    // capture cell bounds of editor
    m_height = rect.height;
    m_minWidth = rect.width + 1;
    rect.width = widthForText();
    super.setBounds( rect );

    // want cursor at end but needs to be at beginning during creation so full line shown
    m_prime.setSelection( Integer.MAX_VALUE );
  }

  /**************************************** widthForText *****************************************/
  protected int widthForText()
  {
    // returns ideal editor width given current text
    GC gc = new GC( m_prime );
    Point size = gc.textExtent( m_prime.getText() );
    gc.dispose();

    if ( size.x + 18 > m_minWidth )
      return size.x + 18;

    return m_minWidth;
  }

  /****************************************** getEditor ******************************************/
  public Text getPrimeEditor()
  {
    // return SWT editor actually used to accept key presses
    return m_prime;
  }

  /******************************************* getText *******************************************/
  public String getText()
  {
    // text displayed by editor (including prefix and suffix)
    return m_prime.getText();
  }

  /******************************************* setText *******************************************/
  public void setText( String value )
  {
    m_prime.setText( value );
  }

  /***************************************** setTextLimit ****************************************/
  public void setTextLimit( int limit )
  {
    m_prime.setTextLimit( limit );
  }

}
