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
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
  private Text    m_prime;        // control which accepts the key presses etc
  private int     m_height;
  private int     m_minWidth;

  private String  m_prefix;
  private String  m_suffix;
  private double  m_value;
  private double  m_step;
  private double  m_page;
  private double  m_min;
  private double  m_max;
  private int     m_decimalPlaces; // number of decimal places
  private boolean m_suppressZeros; // suppress unneeded zeros & dp at end of displayed value

  /**************************************** constructor ******************************************/
  public SpinEditor( Composite parent, double value, boolean suppressZeros )
  {
    // build composite with grid-layout
    super( parent, SWT.NONE );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    setLayout( gridLayout );
    setBackground( JPlanner.gui.COLOR_WHITE );

    // text editor
    m_prime = new Text( this, SWT.NONE );
    m_prime.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    // up & down buttons
    SpinUpDownButtons buttons = new SpinUpDownButtons( this );
    buttons.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, true, 1, 1 ) );

    // initialise private variables with default values
    m_prefix = "";
    m_suffix = "";
    m_value = value;
    m_step = 1.0;
    m_page = 10.0;
    m_min = 0.0;
    m_max = 9999.0;
    m_decimalPlaces = 2;
    m_suppressZeros = suppressZeros;
    displayValue();

    // listen to modify to ensure editor width is sufficient to show whole text
    m_prime.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( ModifyEvent event )
      {
        SpinEditor.this.setSize( widthForText(), m_height );
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
        m_prime.setFocus();
      }
    } );

    // react to arrow-up/arrow-down/page-up/page-down/home/end key presses
    m_prime.addKeyListener( new KeyAdapter()
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
    m_prime.addVerifyListener( new VerifyListener()
    {
      @Override
      public void verifyText( VerifyEvent event )
      {
        try
        {
          String oldS = m_prime.getText();
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
          if ( m_decimalPlaces > 0 )
            regex += "\\.?\\d{0," + m_decimalPlaces + "}"; // optional . with at most decimal digits
          if ( !valueS.matches( regex ) )
            event.doit = false;

          // convert text to number
          Double value = 0.0;
          try
          {
            value = Double.parseDouble( valueS );
          }
          catch ( Exception e )
          {
          }

          // if value is okay, update internal value
          if ( event.doit )
            m_value = value;

          // if value is out of range, highlight text red to indicated invalid
          if ( value < m_min )
          {
            m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
            JPlanner.gui.message( "Value below minimum (" + m_min + ") allowed" );
          }
          else if ( value > m_max )
          {
            m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
            JPlanner.gui.message( "Value above maximum (" + m_max + ") allowed" );
          }
          else
          {
            m_prime.setForeground( JPlanner.gui.COLOR_NO_ERROR );
            JPlanner.gui.message( "" );
          }
        }
        catch ( Exception e )
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

  @Override
  public void addFocusListener( FocusListener listener )
  {
    // interested in when Text editor loses focus, not the extended composite
    m_prime.addFocusListener( listener );
  }

  @Override
  public void addTraverseListener( TraverseListener listener )
  {
    // interested in when Text editor hears tab, not the extended composite
    m_prime.addTraverseListener( listener );
  }

  @Override
  public void setBounds( Rectangle rect )
  {
    // capture cell bounds of editor
    m_height = rect.height;
    m_minWidth = rect.width;
    rect.width = widthForText();
    super.setBounds( rect );
  }

  /**************************************** widthForText *****************************************/
  protected int widthForText()
  {
    // returns ideal editor width given current text
    GC gc = new GC( m_prime );
    Point size = gc.textExtent( m_prime.getText() );
    gc.dispose();

    if ( size.x + 22 > m_minWidth )
      return size.x + 22;

    return m_minWidth;
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

    // display the value
    String format = "%." + m_decimalPlaces + "f";
    String value = String.format( format, m_value );

    // suppress unneeded zeros & dp at end of displayed value
    if ( m_suppressZeros && value.indexOf( '.' ) >= 0 )
    {
      while ( value.charAt( value.length() - 1 ) == '0' )
        value = value.substring( 0, value.length() - 1 );
      if ( value.charAt( value.length() - 1 ) == '.' )
        value = value.substring( 0, value.length() - 1 );
    }

    m_prime.setText( m_prefix + value + m_suffix );
    m_prime.setSelection( new Point( m_prefix.length(), m_prefix.length() + value.length() ) );
  }

  /************************************ positonCursorValueEnd ************************************/
  public void positonCursorValueEnd()
  {
    // position editor selection cursor to end of number
    m_prime.setSelection( getText().length() - m_suffix.length() );
  }

  /*************************************** getPrimeEditor ****************************************/
  public Text getPrimeEditor()
  {
    // return SWT editor actually used to accept key presses
    return m_prime;
  }

  /******************************************* getText *******************************************/
  public String getText()
  {
    // text displayed by editor (including prefix and suffix)
    return m_prime.getText();
  }

  /****************************************** getValue *******************************************/
  public double getValue()
  {
    // value displayed by the editor
    return m_value;
  }

  /****************************************** getSuffix ******************************************/
  public String getSuffix()
  {
    return m_suffix;
  }

  /****************************************** setValue *******************************************/
  public void setValue( double value )
  {
    m_value = value;
    displayValue();
  }

  /******************************************* setText *******************************************/
  public void setText( String str )
  {
    // if point, set value to zero and position cursor after
    if ( ".".equals( str ) )
      str = "0.";

    try
    {
      m_value = Double.parseDouble( str );
      m_prime.setText( str );
      positonCursorValueEnd();
    }
    catch ( NumberFormatException e )
    {
      // if NumberFormatException then don't use string
    }
  }

  /****************************************** setSuffix ******************************************/
  public void setSuffix( String suffix )
  {
    m_suffix = suffix;
    displayValue();
  }

  /****************************************** setPrefix ******************************************/
  public void setPrefix( String prefix )
  {
    m_prefix = prefix;
    displayValue();
  }

  /************************************* setMinMaxStepPageDPs ************************************/
  public void setMinMaxStepPageDPs( double min, double max, double step, double page, int dps )
  {
    m_min = min;
    m_max = max;
    m_step = step;
    m_page = page;
    m_decimalPlaces = dps;
    displayValue();
  }

}
