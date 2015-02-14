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

package rjc.jplanner.model;

/*************************************************************************************************/
/*************************************** Single plan task ****************************************/
/*************************************************************************************************/

public class Task
{
  private String        m_title;       // free text title
  private TimeSpan      m_duration;    // duration of task
  private DateTime      m_start;       // start date-time of task
  private DateTime      m_end;         // end date-time of task
  private TimeSpan      m_work;        // work effort for task
  private Predecessors  m_predecessors; // task predecessors
  private TaskResources m_resources;   // resources allocated to task
}
