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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/*************************************************************************************************/
/******************************** Drop down pick list for XCombo *********************************/
/*************************************************************************************************/

public class XComboList extends Shell
{
  /**************************************** constructor ******************************************/
  public XComboList( XCombo parent )
  {
    // create shell to display the drop down pick list
    super( parent.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP );

    // layout is simple single list with scroll bar when needed 
    setLayout( new FillLayout() );
    List list = new List( this, SWT.BORDER | SWT.V_SCROLL );

    // populate list and set selection
    parent.addItems( list );
    list.setSelection( parent.getSelection() );

    // locate just below XCombo parent
    setLocation( parent.toDisplay( new Point( 0, parent.getSize().y - 1 ) ) );

    // set size ensuring height fits on monitor
    int item = list.getItemHeight();
    int maxY = getMonitor().getClientArea().y + getMonitor().getClientArea().height - 4;
    int height = list.getItemCount() * item + 4;
    if ( getLocation().y + height > maxY )
      height = ( ( maxY - getLocation().y ) / item ) * item + 4;
    setSize( parent.getSize().x, height );

    // ensure list has focus, and when focus lost, list is closed
    list.forceFocus();
    list.addListener( SWT.FocusOut, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        XComboList.this.dispose();
      }
    } );

    // update list selection as user moves mouse 
    list.addMouseMoveListener( new MouseMoveListener()
    {
      @Override
      public void mouseMove( MouseEvent event )
      {
        int newSelection = event.y / list.getItemHeight() + list.getVerticalBar().getSelection();
        if ( newSelection != list.getSelectionIndex() )
          list.setSelection( newSelection );
      }
    } );

    // when user clicks mouse, close list and return selection
    list.addListener( SWT.MouseDown, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        parent.setSelection( list.getSelectionIndex() );
        XComboList.this.dispose();
      }
    } );

    // when presses escape close list, presses enter close list and return selection 
    list.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( event.character == SWT.ESC )
          XComboList.this.dispose();

        if ( event.character == SWT.CR )
        {
          parent.setSelection( list.getSelectionIndex() );
          XComboList.this.dispose();
        }
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
