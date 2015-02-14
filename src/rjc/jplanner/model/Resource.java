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
/************************************* Single plan resource **************************************/
/*************************************************************************************************/

public class Resource
{
  private String   m_initials;    // must be unique across all resources in model
  private String   m_name;        // free text
  private String   m_org;         // free text
  private String   m_group;       // free text
  private String   m_role;        // free text
  private String   m_alias;       // free text
  private Date     m_start;       // date availability starts inclusive
  private Date     m_end;         // date availability end inclusive
  private double   m_availability; // number available
  private double   m_cost;        // cost TODO
  private Calendar m_calendar;    // calendar for resource
  private String   m_comment;     // free text

}
