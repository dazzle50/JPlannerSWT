/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  http://code.google.com/p/jplanner/                                    *
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
import org.eclipse.swt.widgets.Spinner;
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
      return new Text( parent, SWT.SINGLE );
    }

    if ( col == Day.SECTION_WORK )
    {
      Spinner spin = new Spinner( parent, SWT.NONE );
      spin.setDigits( 2 ); // 2 decimal places
      spin.setMinimum( 0 ); // min 0.00
      spin.setMaximum( 1000 ); // max 10.00
      spin.setIncrement( 10 ); // step 0.10
      spin.setPageIncrement( 100 ); // page 1.00
      return spin;
    }

    if ( col == Day.SECTION_PERIODS )
    {
      Spinner spin = new Spinner( parent, SWT.NONE );
      spin.setMinimum( 0 ); // min 0
      spin.setMaximum( 9 ); // max 9
      return spin;
    }

    // none of above, therefore must be a work-period start or end time
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

    // if day work, set spinner catching if not valid float
    if ( col == Day.SECTION_WORK )
    {
      if ( value.equals( "." ) )
        value = "0.";

      try
      {
        ( (Spinner) editor ).setSelection( (int) ( 100 * Float.parseFloat( value ) ) );
      }
      catch (NumberFormatException e)
      {
        Double work = JPlanner.plan.day( row ).work();
        ( (Spinner) editor ).setSelection( (int) ( 100 * work ) );
      }
    }

    // if day number of work periods, set spinner catching if not valid integer
    if ( col == Day.SECTION_PERIODS )
    {
      try
      {
        ( (Spinner) editor ).setSelection( Integer.parseInt( value ) );
      }
      catch (NumberFormatException e)
      {
        int periods = JPlanner.plan.day( row ).numPeriods();
        ( (Spinner) editor ).setSelection( periods );
      }
    }

  }

}
