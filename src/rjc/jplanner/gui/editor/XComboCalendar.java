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

import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/*********************** Extended version of XCombo with list of calendars ***********************/
/*************************************************************************************************/

public class XComboCalendar extends XCombo
{

  /**************************************** constructor ******************************************/
  public XComboCalendar( Composite parent, int style )
  {
    // create XCombo for plan calendars
    super( parent, style );

    int num = JPlanner.plan.calendarsCount();
    String[] items = new String[num];
    for ( int i = 0; i < num; i++ )
      items[i] = JPlanner.plan.calendar( i ).name();
    setItems( items );
  }
}
