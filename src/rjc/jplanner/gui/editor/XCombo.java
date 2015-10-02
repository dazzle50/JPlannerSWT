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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/******************** Replacement for SWT Combo with my desired look and feel ********************/
/*************************************************************************************************/

public class XCombo extends Composite
{
  private String[] m_items;
  private int      m_selection;
  private Text     m_text;
  private int      m_border;

  public boolean   focusOutViaParent = false;

  /**************************************** constructor ******************************************/
  public XCombo( Composite parent, int style )
  {
    // build overall composite, ignore style for now as implementing own border
    super( parent, SWT.NONE );

    // check if border wanted
    m_border = 0;
    if ( ( style & SWT.BORDER ) > 0 )
      m_border = 1;

    // layout to implement outer border
    FillLayout outerBorder = new FillLayout();
    outerBorder.marginHeight = m_border;
    outerBorder.marginWidth = m_border;
    setBackground( JPlanner.gui.COLOR_BORDER_NORMAL );
    setLayout( outerBorder );

    // inner composite to contain text and button of XCombo
    Composite innerComposite = new Composite( this, SWT.NONE );

    // grid-layout to arrange components of XCombo
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginHeight = m_border;
    gridLayout.marginWidth = m_border;
    innerComposite.setLayout( gridLayout );
    innerComposite.setBackground( JPlanner.gui.COLOR_WHITE );

    // text widget to display current selection text
    m_text = new Text( innerComposite, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED );
    m_text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 ) );
    m_text.setBackground( JPlanner.gui.COLOR_WHITE );
    m_text.setCursor( getDisplay().getSystemCursor( SWT.CURSOR_ARROW ) );

    // down arrow button to sit next to text widget
    Button button = new Button( innerComposite, SWT.ARROW | SWT.DOWN );
    button.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true, 1, 1 ) );

    // add focus listener to change border colour when selected
    m_text.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( FocusEvent event )
      {
        setBackground( JPlanner.gui.COLOR_BORDER_NORMAL );
      }

      @Override
      public void focusGained( FocusEvent event )
      {
        setBackground( JPlanner.gui.COLOR_BORDER_SELECTED );
      }
    } );

    // add mouse tracker listener to change border colour when mouse over
    m_text.addMouseTrackListener( new MouseTrackAdapter()
    {
      @Override
      public void mouseExit( MouseEvent event )
      {
        if ( !m_text.isFocusControl() )
          setBackground( JPlanner.gui.COLOR_BORDER_NORMAL );
      }

      @Override
      public void mouseEnter( MouseEvent event )
      {
        if ( !m_text.isFocusControl() )
          setBackground( JPlanner.gui.COLOR_BLACK );
      }
    } );

    // create listener to open drop down list
    MouseAdapter listener = new MouseAdapter()
    {
      @Override
      public void mouseDown( MouseEvent event )
      {
        if ( focusOutViaParent )
          focusOutViaParent = false;
        else
        {
          XComboList list = new XComboList( XCombo.this, m_items, m_border );
          list.open();
        }
      }
    };

    // add listener to open drop down list to both text & button widgets
    m_text.addMouseListener( listener );
    button.addMouseListener( listener );

    // add key press listener for arrow keys
    m_text.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        // if arrow-up (when borderless) or arrow-left move to next item
        if ( ( m_border > 0 && event.keyCode == SWT.ARROW_UP ) || event.keyCode == SWT.ARROW_LEFT )
        {
          m_selection++;
          if ( m_selection >= m_items.length )
            m_selection = 0;
          setSelection( m_selection );
          return;
        }

        // if arrow-down (when borderless) or arrow-right move to previous item
        if ( ( m_border > 0 && event.keyCode == SWT.ARROW_DOWN ) || event.keyCode == SWT.ARROW_RIGHT )
        {
          m_selection--;
          if ( m_selection < 0 )
            m_selection = m_items.length - 1;
          setSelection( m_selection );
          return;
        }

        // if F4 open combo drop-down list
        if ( event.keyCode == SWT.F4 )
        {
          XComboList list = new XComboList( XCombo.this, m_items, m_border );
          list.open();
          return;
        }

        // otherwise move to next selection where key-pressed matches item first character
        nextSelection( event.character );

        // stop anything else happening expect ctrl+C (copy to clipboard)
        if ( event.character != 3 )
          event.doit = false;
      }
    } );

  }

  /****************************************** setItems *******************************************/
  public void setItems( String[] items )
  {
    // set drop down list items
    m_items = items;
  }

  /******************************************* setText *******************************************/
  public void setText( String text )
  {
    // set selection based on given text
    int select = Arrays.asList( m_items ).indexOf( text );
    if ( select >= 0 )
      setSelection( select );
    else
      nextSelection( text.charAt( 0 ) );
  }

  /*************************************** nextSelection *****************************************/
  public void nextSelection( char key )
  {
    // move to next selection where key matches item first character (case-insensitive)
    key = Character.toLowerCase( key );
    int select = getSelection();
    for ( int count = 0; count <= m_items.length; count++ )
    {
      select++;
      if ( select >= m_items.length )
        select = 0;

      char firstChar = Character.toLowerCase( m_items[select].charAt( 0 ) );
      if ( key == firstChar )
      {
        setSelection( select );
        break;
      }
    }
  }

  /**************************************** setSelection *****************************************/
  public void setSelection( int selection )
  {
    // set selection index and update text
    m_selection = selection;
    m_text.setText( m_items[selection] );
    m_text.setSelection( 0, 0 );
  }

  /**************************************** getSelection *****************************************/
  public int getSelection()
  {
    // return selection index
    return m_selection;
  }

  /*************************************** getPrimeEditor ****************************************/
  public Text getPrimeEditor()
  {
    // return SWT editor actually used to accept key presses
    return m_text;
  }

}
