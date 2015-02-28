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
  private String          m_type;               // task type
  private int             m_priority;           // overall task priority (0 to 999 times one million)
  private DateTime        m_deadline;           // task warning deadline
  private String          m_cost;               // calculated cost based on resource use
  private String          m_comment;            // free text comment

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
    // if task is null return blank
    if ( isNull() )
      return "";

    // return display string for given section
    if ( section == SECTION_TITLE )
      return m_title;

    if ( section == SECTION_DURATION )
      return m_duration.toString();

    if ( section == SECTION_START )
      return m_start.toString();

    if ( section == SECTION_END )
      return m_end.toString();

    if ( section == SECTION_WORK )
      return m_work.toString();

    if ( section == SECTION_PRED )
      return m_predecessors.toString();

    if ( section == SECTION_RES )
      return m_resources.toString();

    if ( section == SECTION_TYPE )
      return m_type;

    if ( section == SECTION_PRIORITY )
      return String.format( "%d", m_priority / 1_000_000 );

    if ( section == SECTION_DEADLINE )
      return m_deadline.toString();

    if ( section == SECTION_COST )
      return m_cost;

    if ( section == SECTION_COMMENT )
      return m_comment;

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

  /****************************************** isNull *********************************************/
  public boolean isNull()
  {
    // task is considered null if title not set
    return ( m_title == null );
  }
}
