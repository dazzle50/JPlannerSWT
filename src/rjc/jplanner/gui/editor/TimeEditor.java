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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Time;
import rjc.jplanner.model.TimeSpan;

/*************************************************************************************************/
/********************************** Table cell editor for Times **********************************/
/*************************************************************************************************/

public class TimeEditor extends SpinEditor
{
  private char m_segment; // indicates time segment being edited, TimeSpan.UNIT_HOURS or TimeSpan.UNIT_MINUTES
  private Time m_timeMin;
  private Time m_timeMax;

  /**************************************** constructor ******************************************/
  public TimeEditor( Composite parent, String time, Time min, Time max )
  {
    // time-editor based on spin-editor
    super( parent, 0.0, false );

    // set time-editor features
    m_prefix = "";
    m_step = 1.0;
    m_page = 10.0;
    m_minDigits = 2;
    m_decimalPlaces = 0;
    m_min = -1.0;
    m_max = 60.0;
    m_timeMin = min;
    m_timeMax = max;
    editHours( time );

    // react to ':' to move from editing hours to editing minutes
    getPrimeEditor().addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( m_segment == TimeSpan.UNIT_HOURS && event.character == ':' )
          editMinutes( getPrimeEditor().getText() );
      }
    } );

    // checks when editor value changes
    getPrimeEditor().addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( ModifyEvent event )
      {
        // if user entering more than two digits assume now entering minutes
        if ( m_segment == TimeSpan.UNIT_HOURS && m_value >= 100.0 )
        {
          m_segment = TimeSpan.UNIT_MINUTES;
          m_prefix = Integer.toString( ( (int) m_value ) / 10 ) + ":";
          m_suffix = "";
          getPrimeEditor().setText( m_prefix + Integer.toString( ( (int) m_value ) % 10 ) );
          getPrimeEditor().setSelection( new Point( 4, 4 ) );
          getPrimeEditor().setTextLimit( 5 );
        }

        // check time is valid and within min/max range
        int ms = milliseconds();
        if ( ms < 0 )
        {
          getPrimeEditor().setForeground( JPlanner.gui.COLOR_ERROR );
          JPlanner.gui.message( "Invalid time" );
        }
        else if ( ms > max.milliseconds() )
        {
          getPrimeEditor().setForeground( JPlanner.gui.COLOR_ERROR );
          JPlanner.gui.message( "Time above maximum allowed " + max.toStringShort() );
        }
        else if ( ms < min.milliseconds() )
        {
          getPrimeEditor().setForeground( JPlanner.gui.COLOR_ERROR );
          JPlanner.gui.message( "Time below minimum allowed " + min.toStringShort() );
        }
        else
        {
          getPrimeEditor().setForeground( JPlanner.gui.COLOR_NO_ERROR );
          JPlanner.gui.message( "" );
        }
      }
    } );

  }

  /****************************************** editHours ******************************************/
  public void editHours( String time )
  {
    // setup editor for editing hours segment
    m_segment = TimeSpan.UNIT_HOURS;
    m_prefix = "";
    m_value = Double.parseDouble( time.substring( 0, 2 ) ); // hours
    m_suffix = time.substring( 2 ); // minutes onwards
    displayValue();
    getPrimeEditor().setTextLimit( 6 );
  }

  /***************************************** editMinutes *****************************************/
  public void editMinutes( String time )
  {
    // setup editor for editing minutes segment
    m_segment = TimeSpan.UNIT_MINUTES;
    m_prefix = time.substring( 0, 3 );
    m_value = Double.parseDouble( time.substring( 3, 5 ) ); // minutes
    m_suffix = "";
    displayValue();
    getPrimeEditor().setTextLimit( 5 );
  }

  /**************************************** checkSegment *****************************************/
  public void checkSegment()
  {
    // based on cursor position ensure hours or minutes being edited
    int cursor = getPrimeEditor().getSelection().x;
    int separator = getPrimeEditor().getText().indexOf( ':' );

    if ( m_segment == TimeSpan.UNIT_HOURS && cursor > separator )
      editMinutes( getPrimeEditor().getText() );

    if ( m_segment == TimeSpan.UNIT_MINUTES && cursor <= separator )
      editHours( getPrimeEditor().getText() );
  }

  /*************************************** displayValue ******************************************/
  @Override
  protected void displayValue()
  {
    JPlanner.trace( "m_value = " + m_value );
    // if editing minutes and value not valid minutes, adjust hours accordingly
    if ( m_segment == TimeSpan.UNIT_MINUTES )
    {
      if ( m_value < 0 )
      {
        m_value = 59;
        int newHours = Integer.parseInt( m_prefix.substring( 0, 2 ) ) - 1;
        m_prefix = Integer.toString( newHours ) + ":";
        if ( m_prefix.length() == 2 )
          m_prefix = "0" + m_prefix;
      }
      else if ( m_value > 59 )
      {
        m_value = 0;
        int newHours = Integer.parseInt( m_prefix.substring( 0, 2 ) ) + 1;
        m_prefix = Integer.toString( newHours ) + ":";
        if ( m_prefix.length() == 2 )
          m_prefix = "0" + m_prefix;
      }
    }

    // display the value in correct format
    String value = Integer.toString( (int) m_value );
    if ( value.length() == 1 )
      value = "0" + value;
    getPrimeEditor().setText( m_prefix + value + m_suffix );
    getPrimeEditor().setSelection( new Point( m_prefix.length(), m_prefix.length() + value.length() ) );

    // if display above max, reset to max
    if ( milliseconds() > m_timeMax.milliseconds() )
      setText( m_timeMax );

    // if display below min, reset to min
    if ( milliseconds() < m_timeMin.milliseconds() )
      setText( m_timeMin );
  }

  /***************************************** milliseconds ****************************************/
  public int milliseconds()
  {
    // return current displayed time milliseconds, or -1 if not valid
    try
    {
      Time time = Time.fromString( getPrimeEditor().getText() );
      return time.milliseconds();
    }
    catch ( Exception exception )
    {
      return -1;
    }
  }

  /******************************************* setText *******************************************/
  public void setText( Time time )
  {
    // set displayed text to specified time
    if ( m_segment == TimeSpan.UNIT_HOURS )
      m_suffix = time.toString().substring( 2, 5 );
    if ( m_segment == TimeSpan.UNIT_MINUTES )
      m_prefix = time.toString().substring( 0, 3 );
    getPrimeEditor().setText( time.toString().substring( 0, 5 ) );
    getPrimeEditor().setSelection( new Point( m_prefix.length(), m_prefix.length() + 2 ) );
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

  /****************************************** stepUp *********************************************/
  @Override
  protected void stepUp()
  {
    // increase value by step and update display
    checkSegment();
    m_value += m_step;
    displayValue();
  }

  /***************************************** stepDown ********************************************/
  @Override
  protected void stepDown()
  {
    // decrease value by step and update display
    checkSegment();
    m_value -= m_step;
    displayValue();
  }

  /****************************************** pageUp *********************************************/
  @Override
  protected void pageUp()
  {
    // increase value by page and update display
    checkSegment();
    m_value += m_page;
    displayValue();
  }

  /***************************************** pageDown ********************************************/
  @Override
  protected void pageDown()
  {
    // decrease value by page and update display
    checkSegment();
    m_value -= m_page;
    displayValue();
  }
}
