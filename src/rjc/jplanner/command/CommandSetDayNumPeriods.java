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

package rjc.jplanner.command;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/****************** UndoCommand for updating day-types number of work periods ********************/
/*************************************************************************************************/

public class CommandSetDayNumPeriods implements IUndoCommand
{
  private int m_row;     // table row
  private int m_newValue; // new value after command
  private int m_oldValue; // old value before command

  /**************************************** constructor ******************************************/
  public CommandSetDayNumPeriods( int row, Object newValue, Object oldValue )
  {
    // initialise private variables
    m_row = row;
    m_newValue = Integer.parseInt( (String) newValue );
    m_oldValue = Integer.parseInt( (String) oldValue );

    JPlanner.trace( "CommandSetDayNumPeriods " + row + " " + m_oldValue + " " + m_newValue );
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    //JPlanner.plan.day( m_row ).setData( m_column, m_newValue );
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command
    //JPlanner.plan.day( m_row ).setData( m_column, m_oldValue );
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // text description of command
    return "Day " + m_row + " Periods = " + m_newValue;
  }

}
