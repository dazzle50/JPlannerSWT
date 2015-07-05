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

package rjc.jplanner.model;

import java.util.ArrayList;

/*************************************************************************************************/
/********************** Task predecessors shows dependencies on other tasks **********************/
/*************************************************************************************************/

public class Predecessors
{
  private class PredecessorType
  {
    public static final String FINISH_START  = "FS";
    public static final String START_START   = "SS";
    public static final String START_FINISH  = "SF";
    public static final String FINISH_FINISH = "FF";

    private String             m_type;

    /****************************************** toString *******************************************/
    @Override
    public String toString()
    {
      // returns string representation
      return m_type;
    }
  }

  private class Predecessor
  {
    public Task            task;
    public PredecessorType type;
    public TimeSpan        lag;
  }

  private ArrayList<Predecessor> m_preds = new ArrayList<Predecessor>();

  /**************************************** constructor ******************************************/
  public Predecessors( String text )
  {
    // TODO Auto-generated constructor stub
    String[] parts = text.split( "," );

  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    return "TBD";
  }

  /************************************** hasPredecessor *****************************************/
  public boolean hasPredecessor( Task other )
  {
    // TODO Auto-generated method stub
    return false;
  }

}
