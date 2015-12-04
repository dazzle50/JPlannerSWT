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

package rjc.jplanner.gui.task;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.editor.TextEditor;
import rjc.jplanner.model.TaskResources;

/*************************************************************************************************/
/***************************** Table cell editor for Task resources ******************************/
/*************************************************************************************************/

public class TaskResourcesEditor extends TextEditor
{

  /**************************************** constructor ******************************************/
  public TaskResourcesEditor( Composite parent )
  {
    super( parent );

    m_prime.addVerifyListener( new VerifyListener()
    {
      @Override
      public void verifyText( VerifyEvent event )
      {
        String oldS = m_prime.getText();
        String newS = oldS.substring( 0, event.start ) + event.text + oldS.substring( event.end );

        // highlight text red with explanation message if invalid
        String errors = TaskResources.errors( newS );
        if ( errors.length() > 0 )
        {
          m_prime.setForeground( JPlanner.gui.COLOR_ERROR );
          JPlanner.gui.message( errors );
        }
        else
        {
          m_prime.setForeground( JPlanner.gui.COLOR_NO_ERROR );
          JPlanner.gui.message( "" );
        }
      }
    } );

  }

}
