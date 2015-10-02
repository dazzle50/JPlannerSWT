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
import org.eclipse.swt.widgets.Control;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Task;
import rjc.jplanner.model.TimeSpan;

/*************************************************************************************************/
/********************************** Editor for task table cells **********************************/
/*************************************************************************************************/

public class TaskCellEditor extends XAbstractCellEditor
{
  /**************************************** createEditor *****************************************/
  @Override
  public Control createEditor( int row, int col )
  {
    // create editor based on column
    if ( col == Task.SECTION_TYPE )
    {
      XComboTaskType combo = new XComboTaskType( parent, SWT.NONE );
      combo.setText( JPlanner.plan.task( row ).toString( col ) );
      return combo;
    }

    if ( col == Task.SECTION_DURATION || col == Task.SECTION_WORK )
    {
      TimeSpan sp = new TimeSpan( JPlanner.plan.task( row ).toString( col ) );
      return new TimeSpanEditor( parent, sp );
    }

    if ( col == Task.SECTION_PRED )
      return new TaskPredecessorsEditor( parent, row );

    // TODO - use Text editor until find/write something better
    return new TextEditor( parent );
  }

}
