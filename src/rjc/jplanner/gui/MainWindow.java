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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;

/*************************************************************************************************/
/******************************* Main JPlanner application window ********************************/
/*************************************************************************************************/

public class MainWindow
{
  public static Color           GANTT_BACKGROUND;
  public static Color           GANTT_NONWORKING;
  public static Transform       TRANSFORM;

  public static UndoStackWindow undoWindow;         // window to show plan undo-stack
  public static MenuItem        actionUndoStackView; // action to show plan undo-stack window
  public static MenuItem        actionUndo;
  public static MenuItem        actionRedo;

  /**************************************** constructor ******************************************/
  public MainWindow()
  {
    // create main application display
    Display display = new Display();
    GANTT_BACKGROUND = display.getSystemColor( SWT.COLOR_WHITE );
    GANTT_NONWORKING = new Color( display, 240, 240, 240 );
    TRANSFORM = new Transform( display );

    MainWindowShell shell = new MainWindowShell( display );
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
