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

package rjc.jplanner.command;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Resource;

/*************************************************************************************************/
/****************************** UndoCommand for updating resources *******************************/
/*************************************************************************************************/

public class CommandSetResourceValue implements IUndoCommand
{
  private int    m_resID;   // resource number in plan
  private int    m_section; // section number
  private Object m_newValue; // new value after command
  private Object m_oldValue; // old value before command

  /**************************************** constructor ******************************************/
  public CommandSetResourceValue( int resID, int section, Object newValue, Object oldValue )
  {
    // initialise private variables
    m_resID = resID;
    m_section = section;
    m_newValue = newValue;
    m_oldValue = oldValue;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    JPlanner.plan.resource( m_resID ).setData( m_section, m_newValue );

    // update resources tables
    JPlanner.gui.resourceTables().refresh();
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command
    JPlanner.plan.resource( m_resID ).setData( m_section, m_oldValue );

    // update resources tables
    JPlanner.gui.resourceTables().refresh();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    return "Resource " + ( m_resID + 1 ) + " " + Resource.sectionName( m_section ) + " = " + m_newValue;
  }

}
