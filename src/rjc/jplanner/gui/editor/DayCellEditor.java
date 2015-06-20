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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/******************************** Editor for day-type table cells ********************************/
/*************************************************************************************************/

public class DayCellEditor extends XAbstractCellEditor
{
  /**************************************** createEditor *****************************************/
  @Override
  public Control createEditor( int row, int col )
  {
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
          else if ( JPlanner.plan.isDuplicateDayName( txt, row ) )
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
      double value = JPlanner.plan.day( row ).work();
      SpinEditor spin = new SpinEditor( parent, value, false );
      spin.setMinMaxStepPageDPs( 0.0, 10.0, 0.1, 1.0, 2 );
      return spin;
    }

    if ( col == Day.SECTION_PERIODS )
    {
      double value = JPlanner.plan.day( row ).numPeriods();
      SpinEditor spin = new SpinEditor( parent, value, false );
      spin.setMinMaxStepPageDPs( 0.0, 9.0, 1.0, 1.0, 0 );
      return spin;
    }

    // none of above, therefore must be a work-period start or end time
    // TODO - use Text editor until find/write something better
    return new Text( parent, SWT.SINGLE );
  }
}
