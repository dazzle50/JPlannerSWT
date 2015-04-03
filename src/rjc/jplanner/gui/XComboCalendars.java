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

package rjc.jplanner.gui;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/**************** Extended version of Combo auto populated with list of calendars ****************/
/*************************************************************************************************/

public class XComboCalendars extends Combo
{

  /**************************************** constructor ******************************************/
  public XComboCalendars( Composite parent, int style )
  {
    super( parent, style );

    // set drop-down list items to calendar names, and refresh every time widget gets focus
    setCalendarItems();
    addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( FocusEvent e )
      {
        // do nothing
      }

      @Override
      public void focusGained( FocusEvent e )
      {
        // ensure drop-down list items are up to date
        setCalendarItems();
      }
    } );

  }

  /************************************** setCalendarItems ***************************************/
  private void setCalendarItems()
  {
    // ensure drop-down list items are up to date
    int current = indexOf( getText() );
    removeAll();
    int num = JPlanner.plan.calendarsCount();
    for ( int i = 0; i < num; i++ )
      add( JPlanner.plan.calendar( i ).name() );

    // if current text wasn't valid then default to plan default calendar
    if ( current == -1 )
      setText( JPlanner.plan.calendar().name() );
    else
      setText( getItem( current ) );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
