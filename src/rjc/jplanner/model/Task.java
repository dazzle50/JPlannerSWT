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
  private String          m_title;              // free text title
  private TimeSpan        m_duration;           // duration of task
  private DateTime        m_start;              // start date-time of task
  private DateTime        m_end;                // end date-time of task
  private TimeSpan        m_work;               // work effort for task
  private Predecessors    m_predecessors;       // task predecessors
  private TaskResources   m_resources;          // resources allocated to task

  final public static int SECTION_TITLE    = 0;
  final public static int SECTION_DURATION = 1;
  final public static int SECTION_START    = 2;
  final public static int SECTION_END      = 3;
  final public static int SECTION_WORK     = 4;
  final public static int SECTION_PRED     = 5;
  final public static int SECTION_RES      = 6;
  final public static int SECTION_TYPE     = 7;
  final public static int SECTION_PRIORITY = 8;
  final public static int SECTION_DEADLINE = 9;
  final public static int SECTION_COST     = 10;
  final public static int SECTION_COMMENT  = 11;
  final public static int SECTION_MAX      = 11;

  /****************************************** toString *******************************************/
  public String toString( int section )
  {
    // return display string for given section
    if ( section == SECTION_TITLE )
      return m_title;

    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!

    throw new IllegalArgumentException( "Section=" + section );
  }

  /****************************************** setData ********************************************/
  public void setData( int section, Object newValue )
  {
    // set task data for given section 
    if ( section == SECTION_TITLE )
      m_title = (String) newValue;

    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!

    else
      throw new IllegalArgumentException( "Section=" + section );
  }
}
