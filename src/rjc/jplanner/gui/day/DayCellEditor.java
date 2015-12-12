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

package rjc.jplanner.gui.day;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.editor.SpinEditor;
import rjc.jplanner.gui.editor.TextEditor;
import rjc.jplanner.gui.editor.TimeEditor;
import rjc.jplanner.gui.editor.XAbstractCellEditor;
import rjc.jplanner.model.Day;
import rjc.jplanner.model.Time;

/*************************************************************************************************/
/******************************** Editor for day-type table cells ********************************/
/*************************************************************************************************/

public class DayCellEditor extends XAbstractCellEditor
{
  /**************************************** createEditor *****************************************/
  @Override
  public Control createEditor( int row, int col )
  {
    Day day = JPlanner.plan.day( row );

    // create editor based on column
    if ( col == Day.SECTION_NAME )
    {
      TextEditor editor = new TextEditor( parent );

      // add listener to prevent blank & duplicate day names
      editor.getPrimeEditor().addModifyListener( new ModifyListener()
      {
        @Override
        public void modifyText( ModifyEvent event )
        {
          Text prime = editor.getPrimeEditor();
          String txt = prime.getText();

          if ( txt.length() == 0 )
          {
            prime.setForeground( JPlanner.gui.COLOR_ERROR );
            JPlanner.gui.message( "Blank name not allowed" );
          }
          else if ( JPlanner.plan.daytypes.isDuplicateDayName( txt, row ) )
          {
            prime.setForeground( JPlanner.gui.COLOR_ERROR );
            JPlanner.gui.message( "Duplicate name not allowed" );
          }
          else
          {
            prime.setForeground( JPlanner.gui.COLOR_NO_ERROR );
            JPlanner.gui.message( "" );
          }
        }
      } );

      return editor;
    }

    if ( col == Day.SECTION_WORK )
    {
      double value = day.work();
      SpinEditor spin = new SpinEditor( parent, value, false );
      spin.setMinMaxStepPageDPs( 0.0, 10.0, 0.1, 1.0, 2 );
      return spin;
    }

    if ( col == Day.SECTION_PERIODS )
    {
      double value = day.numPeriods();
      SpinEditor spin = new SpinEditor( parent, value, false );
      double max = 9.0;
      if ( day.end().hours() > 23 )
        max = 1.0;
      spin.setMinMaxStepPageDPs( 0.0, max, 1.0, 1.0, 0 );
      return spin;
    }

    // none of above, therefore must be a work-period start or end time
    Time min = Time.MIN_VALUE;
    Time max = Time.MAX_VALUE;
    if ( col > Day.SECTION_START1 )
      min = Time.fromString( day.toString( col - 1 ) ).addMilliseconds( 60 * 1000 );
    if ( col < day.numPeriods() * 2 + Day.SECTION_START1 - 1 )
      max = Time.fromString( day.toString( col + 1 ) ).addMilliseconds( -60 * 1000 );

    TimeEditor editor = new TimeEditor( parent, day.toString( col ), min, max );

    return editor;
  }
}
