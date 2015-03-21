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
import rjc.jplanner.model.Calendar;
import rjc.jplanner.model.DateTime;

/*************************************************************************************************/
/*************************** UndoCommand for updating plan properties ****************************/
/*************************************************************************************************/

public class CommandSetPlanProperties implements UndoCommand
{
  private String   m_oldTitle;
  private DateTime m_oldStart;
  private Calendar m_oldCal;
  private String   m_oldDTformat;
  private String   m_oldDformat;

  private String   m_newTitle;
  private DateTime m_newStart;
  private Calendar m_newCal;
  private String   m_newDTformat;
  private String   m_newDformat;

  /**************************************** constructor ******************************************/
  public CommandSetPlanProperties( String title, DateTime start, Calendar cal, String DTformat, String Dformat )
  {
    // initialise private variables
    m_oldTitle = JPlanner.plan.title();
    m_oldStart = JPlanner.plan.start();
    m_oldCal = JPlanner.plan.calendar();
    m_oldDTformat = JPlanner.plan.datetimeFormat();
    m_oldDformat = JPlanner.plan.dateFormat();

    m_newTitle = title;
    m_newStart = start;
    m_newCal = cal;
    m_newDTformat = DTformat;
    m_newDformat = Dformat;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // TODO Auto-generated method stub

  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // TODO Auto-generated method stub

  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
