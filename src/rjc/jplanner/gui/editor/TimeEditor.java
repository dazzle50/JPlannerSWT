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

import rjc.jplanner.model.Time;
import rjc.jplanner.model.TimeSpan;

/*************************************************************************************************/
/********************************** Table cell editor for Times **********************************/
/*************************************************************************************************/

public class TimeEditor extends SpinEditor
{
  private char m_segment; // indicates time segment being edited
  private Time m_timeMin;
  private Time m_timeMax;

  /**************************************** constructor ******************************************/
  public TimeEditor( Composite parent, String time, Time min, Time max )
  {
    // time-editor based on spin-editor
    super( parent, 0.0, false );

    // set time-editor features
    m_segment = TimeSpan.UNIT_HOURS;
    m_timeMin = min;
    m_timeMax = max;

    // set spin-editor features
    m_prefix = "";
    m_value = Double.parseDouble( time.substring( 0, 2 ) ); // hours
    m_suffix = time.substring( 2 ); // minutes onwards
    m_step = 1.0;
    m_page = 10.0;
    m_min = 0.0;
    m_max = 24.0;
    m_minDigits = 2;
    m_decimalPlaces = 0;
    displayValue();

    // react to ':' to move from editing hours to editing minutes
    getPrimeEditor().addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( m_segment == TimeSpan.UNIT_HOURS && event.character == ':' )
        {
          displayValue();
          String txt = getPrimeEditor().getText();
          m_segment = TimeSpan.UNIT_MINUTES;
          m_prefix = txt.substring( 0, 3 );
          m_suffix = "";
          m_value = Double.parseDouble( txt.substring( 3, 5 ) ); // minutes
          m_max = 59;
          displayValue();
          event.doit = false;
        }
      }
    } );

  }

  /******************************************* setText *******************************************/
  @Override
  public void setText( String str )
  {
    // if editor already set, no need to do anything else
    if ( str.equals( getPrimeEditor().getText() ) )
      return;

    super.setText( str );
  }

}
