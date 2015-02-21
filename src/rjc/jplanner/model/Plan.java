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

import java.util.ArrayList;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import rjc.jplanner.model.Calendar.DefaultCalendarTypes;
import rjc.jplanner.model.DayType.DefaultDayTypes;

/*************************************************************************************************/
/************************** Holds the complete data model for the plan ***************************/
/*************************************************************************************************/

public class Plan
{
  private StringBuilder       m_title;            // plan title as set in properties
  private DateTime            m_start;            // plan start as set in properties

  private ArrayList<Task>     m_tasks;            // list of plan tasks
  private ArrayList<Resource> m_resources;        // list of plan resources
  private ArrayList<Calendar> m_calendars;        // list of plan calendars
  private ArrayList<DayType>  m_daytypes;         // list of plan day types

  public IDataProvider        daysDataProvider;
  public IDataProvider        daysCHeaderProvider;
  public IDataProvider        daysRHeaderProvider;

  /**************************************** constructor ******************************************/
  public Plan()
  {
    // construct empty but usable Plan
    m_tasks = new ArrayList<Task>();
    m_resources = new ArrayList<Resource>();
    m_calendars = new ArrayList<Calendar>();
    m_daytypes = new ArrayList<DayType>();
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // convert to string
    String hash = super.toString();
    String id = hash.substring( hash.lastIndexOf( '.' ) + 1 );
    return id + "[" + m_title + ", " + m_start + ", " + m_tasks.size() + " Tasks, " + m_resources.size()
        + " Resources, " + m_calendars.size() + " Calendars, " + m_daytypes.size() + " DayTypes]";
  }

  /**************************************** initialise *******************************************/
  public void initialise()
  {
    // initialise plan with default settings and contents
    m_start = DateTime.now();

    m_daytypes.clear();
    for ( DefaultDayTypes type : DefaultDayTypes.values() )
      m_daytypes.add( new DayType( type ) );

    m_calendars.clear();
    for ( DefaultCalendarTypes type : DefaultCalendarTypes.values() )
      m_calendars.add( new Calendar( type ) );

    m_resources.clear();
    for ( int count = 0; count < 5; count++ )
      m_resources.add( new Resource() );

    m_tasks.clear();
    for ( int count = 0; count < 10; count++ )
      m_tasks.add( new Task() );
  }

  /**************************************** tasksCount *******************************************/
  public int tasksCount()
  {
    // return number of tasks in plan
    return m_tasks.size();
  }

  /************************************** resourcesCount *****************************************/
  public int resourcesCount()
  {
    // return number of resources in plan
    return m_resources.size();
  }

  /************************************** calendarsCount *****************************************/
  public int calendarsCount()
  {
    // return number of calendars in plan
    return m_calendars.size();
  }

  /***************************************** daysCount *******************************************/
  public int daysCount()
  {
    // return number of day-types in plan
    return m_daytypes.size();
  }

  /******************************************* task **********************************************/
  public Task task( int index )
  {
    // return task corresponding to index
    return m_tasks.get( index );
  }

  /***************************************** resource ********************************************/
  public Resource resource( int index )
  {
    // return resource corresponding to index
    return m_resources.get( index );
  }

  /***************************************** calendar ********************************************/
  public Calendar calendar( int index )
  {
    // return calendar corresponding to index
    return m_calendars.get( index );
  }

  /********************************************* day *********************************************/
  public DayType day( int index )
  {
    // return day-type corresponding to index
    return m_daytypes.get( index );
  }

}
