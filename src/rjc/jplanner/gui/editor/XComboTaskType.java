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

import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.model.TaskType;

/*************************************************************************************************/
/********************** Extended version of XCombo with list of task types ***********************/
/*************************************************************************************************/

public class XComboTaskType extends XCombo
{

  /**************************************** constructor ******************************************/
  public XComboTaskType( Composite parent, int style )
  {
    // create XCombo for plan task types
    super( parent, style );

    setItems( TaskType.list() );
  }
}
