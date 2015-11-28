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

public class SpinEditor extends Composite implements ModifyListener, VerifyListener
{
  private Text      m_prime;             // control which accepts the key presses etc
  private int       m_height;            // height of editor
  private int       m_minWidth;          // minimum width of editor, can be larger
  private Listener  m_mouseWheelListener;// listener for mouse wheel events

  protected String  m_prefix;            // prefix shown before value
  protected String  m_suffix;            // suffix shown after value
  protected double  m_value;             // numerical value being shown
  protected double  m_step;              // small step for example on arrow-up or arrow-down
  protected double  m_page;              // large step for example on page-up or page-down
  protected double  m_min;               // min valid value
  protected double  m_max;               // max valid value
  protected int     m_minDigits;         // minimum number of digits, padded with zeros on left
  protected int     m_decimalPlaces;     // number of decimal places, padded with zeros on right
  protected boolean m_suppressZeros;     // suppress unneeded zeros & dp at end of displayed value

  public static int TEXT_WIDTH_EXTRA;

  /**************************************** constructor ******************************************/
  public SpinEditor( Composite parent, double value, boolean suppressZeros )
  {
    // build spin-editor using grid-layout
    super( parent, SWT.NONE );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    setLayout( gridLayout );
    setBackground( JPlanner.gui.COLOR_WHITE );

    // SWT text editor to display value and accept user edit actions 
    TEXT_WIDTH_EXTRA = ( SpinUpDownButtons.SPINBUTTONS_WIDTH * 13 ) / 10;
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
    m_minDigits = 1;
    m_decimalPlaces = 2;
    m_suppressZeros = suppressZeros;

    // listen to modify to ensure editor width is sufficient to show whole text, and in valid value range
    m_prime.addModifyListener( this );

    // react to mouse wheel events
    m_mouseWheelListener = new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        event.doit = false;
        if ( event.count > 0 )
          stepUp();
        else
          stepDown();
      }
    };
    m_prime.addListener( SWT.MouseWheel, m_mouseWheelListener );
    getParent().addListener( SWT.MouseWheel, m_mouseWheelListener );

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

    // verify any changes to editor text are valid, i.e. don't edit suffix & prefix, and valid number
    m_prime.addVerifyListener( this );
  }

  /*************************************** removeListeners ***************************************/
  public void removeListeners()
  {
    // remove listener from parent, needs to be done before closing or committing!
    getParent().removeListener( SWT.MouseWheel, m_mouseWheelListener );
  }

  /****************************************** verifyText *****************************************/
  @Override
  public void verifyText( VerifyEvent event )
  {
    // reject any invalid text changes
    try
    {
      String oldS = m_prime.getText();
      String newS = oldS.substring( 0, event.start ) + event.text + oldS.substring( event.end );
      String prefix = newS.substring( 0, m_prefix.length() );
      String suffix = newS.substring( newS.length() - m_suffix.length() );

      // check prefix & suffix not altered
      if ( !prefix.equals( m_prefix ) || !suffix.equals( m_suffix ) )
        event.doit = false;

      // check new value is valid double
      String valueS = newS.substring( prefix.length(), newS.length() - suffix.length() );
      String regex = "";
      if ( m_min < 0.0 )
      {
        if ( m_max < 0.0 )
          regex += "-"; // both min & max <0 so must start with -
        else
          regex += "-?"; // only min <0 so might start with -
      }
      regex += "\\d*"; // zero or more digits
      if ( m_decimalPlaces > 0 )
        regex += "\\.?\\d{0," + m_decimalPlaces + "}"; // optional . with at most decimal digits
      if ( !valueS.matches( regex ) )
        event.doit = false;
    }
    catch ( Exception exception )
    {
      event.doit = false;
    }
  }

  /****************************************** modifyText *****************************************/
  @Override
  public void modifyText( ModifyEvent event )
  {
    setSize( widthForText(), m_height );

    // if value is out of range, highlight text red to indicated invalid
    determineValue();
    if ( getText().length() == 0 )
    {
      m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
      JPlanner.gui.message( "Blank not allowed" );
    }
    else if ( m_value < m_min )
    {
      m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
      JPlanner.gui.message( "Value below minimum allowed (" + m_min + ")" );
    }
    else if ( m_value > m_max )
    {
      m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
      JPlanner.gui.message( "Value above maximum allowed (" + m_max + ")" );
    }
    else
    {
      m_prime.setForeground( JPlanner.gui.COLOR_NO_ERROR );
      JPlanner.gui.message( "" );
    }
  }

  /************************************** addFocusListener ***************************************/
  @Override
  public void addFocusListener( FocusListener listener )
  {
    // interested in when Text editor loses focus, not the extended composite
    m_prime.addFocusListener( listener );
  }

  /************************************* addTraverseListener *************************************/
  @Override
  public void addTraverseListener( TraverseListener listener )
  {
    // interested in when Text editor hears tab, not the extended composite
    m_prime.addTraverseListener( listener );
  }

  /****************************************** setBounds ******************************************/
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

    if ( size.x + TEXT_WIDTH_EXTRA > m_minWidth )
      return size.x + TEXT_WIDTH_EXTRA;

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

  /************************************** determineValue *****************************************/
  private void determineValue()
  {
    // determine numeric value from current editor text
    try
    {
      m_value = Double.parseDouble( getText() );
    }
    catch ( NumberFormatException exception )
    {
      // if NumberFormatException then default m_value to zero
      m_value = 0.0;
    }
  }

  /*************************************** displayValue ******************************************/
  protected void displayValue()
  {
    // check value is within specified min & max
    if ( m_value > m_max )
      m_value = m_max;
    if ( m_value < m_min )
      m_value = m_min;

    // display the value in correct format
    String format = "%0" + m_minDigits + "." + m_decimalPlaces + "f";
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
    // position editor selection cursor to end of value
    m_prime.setSelection( m_prime.getText().length() - m_suffix.length() );
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
    // text displayed by editor (without prefix and suffix)
    String txt = m_prime.getText();
    return txt.substring( m_prefix.length(), txt.length() - m_suffix.length() );
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
    // suffix displayed by the editor
    return m_suffix;
  }

  /****************************************** setValue *******************************************/
  public void setValue( double value )
  {
    // set editor value
    m_value = value;
    displayValue();
  }

  /******************************************* setText *******************************************/
  public void setText( String str )
  {
    // if point, set value to zero and position cursor after
    if ( ".".equals( str ) )
      str = "0.";

    // display the string with prefix and suffix added
    try
    {
      m_value = Double.parseDouble( str );
      m_prime.setText( m_prefix + str + m_suffix );
      positonCursorValueEnd();
    }
    catch ( NumberFormatException exception )
    {
      // if NumberFormatException then don't use string
    }
  }

  /****************************************** setSuffix ******************************************/
  public void setSuffix( String suffix )
  {
    // set suffix and display
    m_suffix = suffix;
    displayValue();
  }

  /****************************************** setPrefix ******************************************/
  public void setPrefix( String prefix )
  {
    // set prefix and display
    m_prefix = prefix;
    displayValue();
  }

  /************************************* setMinMaxStepPageDPs ************************************/
  public void setMinMaxStepPageDPs( double min, double max, double step, double page, int dps )
  {
    // set the common spin-editor features
    m_min = min;
    m_max = max;
    m_step = step;
    m_page = page;
    m_decimalPlaces = dps;
  }

}
