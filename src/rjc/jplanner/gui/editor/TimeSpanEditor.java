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

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.model.TimeSpan;

/*************************************************************************************************/
/******************************** Table cell editor for TimeSpans ********************************/
/*************************************************************************************************/

public class TimeSpanEditor extends SpinEditor
{

  /**************************************** constructor ******************************************/
  public TimeSpanEditor( Composite parent, TimeSpan sp )
  {
    // create editor
    super( parent, true );
    setValue( sp.number() );
    setSuffix( " " + sp.units() );

    // add key listener to allow user to change time-span units
    getPrimeEditor().addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( TimeSpan.UNITS.indexOf( event.character ) >= 0 )
        {
          setSuffix( " " + event.character );
          event.doit = false;
        }
      }
    } );
  }

  /****************************************** setText ********************************************/
  public void setText( String value )
  {
    // if point, set value to zero and position cursor after
    if ( ".".equals( value ) )
    {
      setValue( 0.0 );
      getPrimeEditor().setText( "0." + getSuffix() );
      positonCursorValueEnd();
    }

    // if numeric digit, set value and position cursor after
    if ( "0123456789".indexOf( value ) >= 0 )
    {
      setValue( Double.parseDouble( value ) );
      positonCursorValueEnd();
    }

    // if time-span units, set suffix
    if ( TimeSpan.UNITS.indexOf( value ) >= 0 )
      setSuffix( " " + value );
  }
}
