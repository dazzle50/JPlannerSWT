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

package rjc.jplanner.gui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Task;
import rjc.jplanner.model.TaskType;
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
      Combo combo = new Combo( parent, SWT.READ_ONLY );
      combo.add( TaskType.ASAP_FDUR );
      combo.add( TaskType.ASAP_FWORK );
      combo.add( TaskType.SON_FDUR );
      combo.add( TaskType.SON_FWORK );
      combo.add( TaskType.FIXED_PERIOD );
      return combo;
    }

    if ( col == Task.SECTION_DURATION || col == Task.SECTION_WORK )
    {
      TimeSpan sp = new TimeSpan( JPlanner.plan.task( row ).toString( col ) );
      TimeSpanEditor editor = new TimeSpanEditor( parent, sp );
      return editor;
    }

    // TODO - use Text editor until find/write something better
    return new Text( parent, SWT.SINGLE );
  }

  /****************************************** setEditor ******************************************/
  @Override
  public void setEditor( Control editor, String value, int row, int col )
  {
    // set editor value
    if ( editor instanceof Text )
    {
      ( (Text) editor ).setText( value );
      ( (Text) editor ).setSelection( Integer.MAX_VALUE );
    }

    if ( editor instanceof Combo )
      ( (Combo) editor ).setText( value );

    if ( editor instanceof TimeSpanEditor )
      ( (TimeSpanEditor) editor ).setText( value );
  }

}
