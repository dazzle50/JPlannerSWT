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

package rjc.jplanner.gui.calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.editor.TextEditor;
import rjc.jplanner.gui.editor.XAbstractCellEditor;
import rjc.jplanner.gui.editor.XComboDay;
import rjc.jplanner.model.Calendar;

/*************************************************************************************************/
/******************************** Editor for calendar table cells ********************************/
/*************************************************************************************************/

public class CalendarCellEditor extends XAbstractCellEditor
{
  /**************************************** createEditor *****************************************/
  @Override
  public Control createEditor( int row, int col )
  {
    // create editor based on row
    if ( row >= Calendar.SECTION_NORMAL1 )
    {
      XComboDay combo = new XComboDay( parent, SWT.NONE );
      combo.setText( JPlanner.plan.calendar( col ).toString( row ) );
      return combo;
    }

    // TODO - use Text editor until find/write something better
    return new TextEditor( parent );
  }

}
