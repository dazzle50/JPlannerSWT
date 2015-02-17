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

package rjc.jplanner.gui;

import org.eclipse.swt.widgets.Display;

/*************************************************************************************************/
/******************************* Main JPlanner application window ********************************/
/*************************************************************************************************/

public class MainWindow
{

  /**************************************** constructor ******************************************/
  public MainWindow()
  {
    // create main application display
    Display display = new Display();
    _MainWindowShell shell = new _MainWindowShell( display );
    shell.open();

    // run the event loop as long as the window is open
    while ( !shell.isDisposed() )
    {
      // read the next OS event queue and transfer it to a SWT event 
      if ( !display.readAndDispatch() )
      {
        // if there are currently no other OS event to process
        // sleep until the next OS event is available 
        display.sleep();
      }
    }

    // disposes all associated windows and their components
    display.dispose();
  }

}
