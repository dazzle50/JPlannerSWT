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

package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/******************** Replacement for SWT Combo with my desired look and feel ********************/
/*************************************************************************************************/

public class XCombo extends Composite
{
  private int  m_selection;
  private Text m_text;
  private List m_list;

  /**************************************** constructor ******************************************/
  public XCombo( Composite parent, int style )
  {
    // build overall composite, ignore style for now as implementing own border
    super( parent, SWT.NONE );

    // check if border wanted
    int border = 0;
    if ( ( style & SWT.BORDER ) > 0 )
      border = 1;

    // layout to implement outer border
    FillLayout outerBorder = new FillLayout();
    outerBorder.marginHeight = border;
    outerBorder.marginWidth = border;
    setBackground( JPlanner.gui.COLOR_BORDER_NORMAL );
    setLayout( outerBorder );

    // inner composite to contain text and button of XCombo
    Composite innerComposite = new Composite( this, SWT.NONE );

    // grid-layout to arrange components of XCombo
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginHeight = border;
    gridLayout.marginWidth = border;
    innerComposite.setLayout( gridLayout );
    innerComposite.setBackground( JPlanner.gui.COLOR_WHITE );

    // text widget to display current selection text
    m_text = new Text( innerComposite, SWT.READ_ONLY );
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
        XComboList list = new XComboList( XCombo.this );
        list.open();
      }
    };

    // add listener to open drop down list to both text & button widgets
    m_text.addMouseListener( listener );
    button.addMouseListener( listener );

    // TODO HANDLE arrow keys + other keys
    // TODO HANDLE toggle drop down list on clicking text/button
  }

  /**************************************** setSelection *****************************************/
  public void setSelection( int selection )
  {
    // set selection index and update text
    m_selection = selection;
    m_text.setText( m_list.getItem( selection ) );
  }

  /**************************************** getSelection *****************************************/
  public int getSelection()
  {
    // return selection index
    return m_selection;
  }

  /****************************************** addItems *******************************************/
  public void addItems( List list )
  {
    // override this method to add desired drop down list items
    m_list = list;
  }

}
