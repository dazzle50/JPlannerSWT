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

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/**************************** Table cell editor for Resource initials ****************************/
/*************************************************************************************************/

public class ResourceInitialsEditor extends TextEditor
{

  /**************************************** constructor ******************************************/
  public ResourceInitialsEditor( Composite parent )
  {
    super( parent );
    setTextLimit( 20 );

    m_prime.addVerifyListener( new VerifyListener()
    {
      @Override
      public void verifyText( VerifyEvent event )
      {
        try
        {
          String oldS = m_prime.getText();
          String newS = oldS.substring( 0, event.start ) + event.text + oldS.substring( event.end );

          // any characters except white-space, comma, open & close sq brackets
          if ( !newS.matches( "[^\\s,\\[\\]]*" ) )
            throw new IllegalArgumentException();

          // highlight text red with explanation message if invalid
          if ( newS.length() == 0 )
          {
            m_prime.setForeground( JPlanner.gui.COLOR_RED );
            JPlanner.gui.message( "Blank initials not allowed" );
          }
          else if ( newS.equals( "TODO" ) ) // TODO duplicates not allowed !!!!!!!!!!
          {
            m_prime.setForeground( JPlanner.gui.COLOR_RED );
            JPlanner.gui.message( "Duplicate initials not allowed" );
          }
          else
          {
            m_prime.setForeground( JPlanner.gui.COLOR_BLACK );
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

}
