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

package rjc.jplanner.gui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/*************************************************************************************************/
/*********************** Modified MessageDialog without close 'X' button  ************************/
/*************************************************************************************************/

public class XMessageDialog extends MessageDialog
{

  /**************************************** constructor ******************************************/
  public XMessageDialog( Shell parentShell, String dialogTitle, String dialogMessage, int dialogImageType,
      String[] dialogButtonLabels, int defaultIndex )
  {
    super( parentShell, dialogTitle, null, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex );

    // remove close 'X' button in top-right corner
    setShellStyle( getShellStyle() & ~SWT.CLOSE );
  }

}
