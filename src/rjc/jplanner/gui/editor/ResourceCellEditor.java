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

import org.eclipse.swt.widgets.Control;

import rjc.jplanner.model.Resource;

/*************************************************************************************************/
/******************************** Editor for resource table cells ********************************/
/*************************************************************************************************/

public class ResourceCellEditor extends XAbstractCellEditor
{
  /**************************************** createEditor *****************************************/
  @Override
  public Control createEditor( int row, int col )
  {
    // create editor based on column
    if ( col == Resource.SECTION_INITIALS )
      return new ResourceInitialsEditor( parent );

    if ( col == Resource.SECTION_NAME || col == Resource.SECTION_ORG || col == Resource.SECTION_GROUP
        || col == Resource.SECTION_ROLE || col == Resource.SECTION_ALIAS || col == Resource.SECTION_COMMENT )
    {
      TextEditor editor = new TextEditor( parent );
      editor.setTextLimit( 100 );
      return editor;
    }

    // TODO - use Text editor until find/write something better
    return new TextEditor( parent );
  }

}
