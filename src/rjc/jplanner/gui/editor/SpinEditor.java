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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/******************************* Replacement for SWT Spinner widget ******************************/
/*************************************************************************************************/

public class SpinEditor extends Composite
{
  private Text   m_text;  // widget showing value

  private String m_prefix;
  private String m_suffix;
  private double m_value;
  private double m_step;
  private double m_page;
  private double m_min;
  private double m_max;
  private int    m_places; // number of decimal places

  /**************************************** constructor ******************************************/
  public SpinEditor( Composite parent, int style )
  {
    // build composite with grid-layout
    super( parent, style );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    setLayout( gridLayout );
    setBackground( JPlanner.gui.COLOR_WHITE );

    // text editor
    m_text = new Text( this, SWT.NONE );
    m_text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    // up & down buttons
    UpDownButtons buttons = new UpDownButtons( this );
    buttons.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, true, 1, 1 ) );

    // initialise private values
    m_prefix = "";
    m_suffix = "";
    m_value = 0.0;
    m_step = 0.1;
    m_page = 10.0;
    m_min = 0.0;
    m_max = 100.0;
    m_places = 2;
    displayValue();

    // give focus to text editor if composite clicked
    addListener( SWT.MouseDown, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        m_text.setFocus();
      }
    } );

    // react to mouse wheel events
    addMouseWheelListener( new MouseWheelListener()
    {
      @Override
      public void mouseScrolled( MouseEvent event )
      {
        if ( event.count > 0 )
          stepUp();
        else
          stepDown();
      }
    } );

    // react to up / down button presses
    buttons.addListener( SWT.MouseDown, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        // if checked top-half, then up, else down
        int h = buttons.getSize().y;
        if ( event.y < h / 2 )
          stepUp();
        else
          stepDown();

        // also give text editor focus
        m_text.setFocus();
      }
    } );

    // react to arrow-up/arrow-down/page-up/page-down/home/end key presses
    m_text.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( event.keyCode == SWT.ARROW_UP )
        {
          stepUp();
          event.doit = false;
        }
        if ( event.keyCode == SWT.ARROW_DOWN )
        {
          stepDown();
          event.doit = false;
        }
        if ( event.keyCode == SWT.PAGE_UP )
        {
          pageUp();
          event.doit = false;
        }
        if ( event.keyCode == SWT.PAGE_DOWN )
        {
          pageDown();
          event.doit = false;
        }
        if ( event.keyCode == SWT.HOME )
        {
          m_value = m_min;
          displayValue();
          event.doit = false;
        }
        if ( event.keyCode == SWT.END )
        {
          m_value = m_max;
          displayValue();
          event.doit = false;
        }
      }
    } );

    // verify any changes to editor text are valid
    m_text.addVerifyListener( new VerifyListener()
    {
      @Override
      public void verifyText( VerifyEvent event )
      {
        try
        {
          String oldS = m_text.getText();
          String newS = oldS.substring( 0, event.start ) + event.text + oldS.substring( event.end );
          String preS = newS.substring( 0, m_prefix.length() );
          String sufS = newS.substring( newS.length() - m_suffix.length() );

          // check prefix & suffix not altered
          if ( !preS.equals( m_prefix ) || !sufS.equals( m_suffix ) )
            event.doit = false;

          // check new value is valid double
          String valueS = newS.substring( preS.length(), newS.length() - sufS.length() );
          String regex = "";
          if ( m_min < 0.0 )
          {
            if ( m_max < 0.0 )
              regex += "-"; // both min & max <0 so must start with -
            else
              regex += "-?"; // only min <0 so mught start with -
          }
          regex += "\\d*"; // zero or more digits
          if ( m_places > 0 )
            regex += "\\.?\\d{0," + m_places + "}"; // optional . with at most m_places digits
          if ( !valueS.matches( regex ) )
            event.doit = false;

          // convert text to number
          Double value = 0.0;
          try
          {
            value = Double.parseDouble( valueS );
          }
          catch (Exception e)
          {
          }

          // if value is okay, update internal value
          if ( event.doit )
            m_value = value;

          // if value is out of range, highlight text red to indicated invalid
          if ( value < m_min || value > m_max )
            m_text.setForeground( JPlanner.gui.COLOR_RED );
          else
            m_text.setForeground( JPlanner.gui.COLOR_BLACK );
        }
        catch (Exception e)
        {
          event.doit = false;
        }
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /****************************************** stepUp *********************************************/
  private void stepUp()
  {
    // increase value by step and update display
    m_value += m_step;
    displayValue();
  }

  /***************************************** stepDown ********************************************/
  private void stepDown()
  {
    // decrease value by step and update display
    m_value -= m_step;
    displayValue();
  }

  /****************************************** pageUp *********************************************/
  private void pageUp()
  {
    // increase value by page and update display
    m_value += m_page;
    displayValue();
  }

  /***************************************** pageDown ********************************************/
  private void pageDown()
  {
    // decrease value by page and update display
    m_value -= m_page;
    displayValue();
  }

  /*************************************** displayValue ******************************************/
  private void displayValue()
  {
    // check value is within specified min & max
    if ( m_value > m_max )
      m_value = m_max;
    if ( m_value < m_min )
      m_value = m_min;

    // display the value to specified number of decimals places, selected
    String format = "%." + m_places + "f";
    String value = String.format( format, m_value );
    m_text.setText( m_prefix + value + m_suffix );
    m_text.setSelection( new Point( m_prefix.length(), m_prefix.length() + value.length() ) );
  }

  /***************************************** getValue ********************************************/
  public double getValue()
  {
    // value displayed by the editor
    return m_value;
  }

}
