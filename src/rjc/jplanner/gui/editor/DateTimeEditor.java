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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import rjc.jplanner.model.Date;
import rjc.jplanner.model.Time;

/*************************************************************************************************/
/***************************** Widget with both date & time editors ******************************/
/*************************************************************************************************/

public class DateTimeEditor extends Composite
{
  private DateTime m_dateWidget; // SWT "DateTime" widget (not rjc.jplanner.model.DateTime)
  private DateTime m_timeWidget; // SWT "DateTime" widget (not rjc.jplanner.model.DateTime)

  /**************************************** constructor ******************************************/
  public DateTimeEditor( Composite parent, int style )
  {
    // construct composite with date-widget and time-widget, plus convenience menu
    super( parent, style );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );

    m_dateWidget = new DateTime( this, SWT.DROP_DOWN );

    Menu menu = new Menu( m_dateWidget );
    m_dateWidget.setMenu( menu );

    MenuItem workForward = new MenuItem( menu, SWT.NONE );
    workForward.setText( "Forward to next work period start" );

    MenuItem workBack = new MenuItem( menu, SWT.NONE );
    workBack.setText( "Back to previous work period end" );

    m_timeWidget = new DateTime( this, SWT.BORDER | SWT.TIME );

    Menu popup = new Menu( m_timeWidget );
    MenuItem workStart = new MenuItem( popup, SWT.CASCADE );
    workStart.setText( "Start of work day" );
    m_timeWidget.setMenu( popup );

    MenuItem workEnd = new MenuItem( popup, SWT.NONE );
    workEnd.setText( "End of work day" );
  }

  /***************************************** setDateTime *****************************************/
  public void setDateTime( rjc.jplanner.model.DateTime dt )
  {
    // set editor widgets to desired date & time (note widget uses months 0-11, but Date uses months 1-12)
    m_dateWidget.setDate( dt.year(), dt.month() - 1, dt.dayOfMonth() );
    m_timeWidget.setTime( dt.hours(), dt.minutes(), dt.seconds() );
  }

  /**************************************** milliseconds *****************************************/
  public long milliseconds()
  {
    // get editor widgets date-time in milliseconds (note widget uses months 0-11, but Date uses months 1-12)
    Date date = new Date( m_dateWidget.getYear(), m_dateWidget.getMonth() + 1, m_dateWidget.getDay() );
    Time time = new Time( m_timeWidget.getHours(), m_timeWidget.getMinutes(), m_timeWidget.getSeconds(), 0 );
    return date.epochday() * rjc.jplanner.model.DateTime.MILLISECONDS_IN_DAY + time.milliseconds();
  }

}
