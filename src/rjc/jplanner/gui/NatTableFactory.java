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

package rjc.jplanner.gui;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.style.theme.ThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.gui.data.DaysBody;
import rjc.jplanner.gui.data.DaysColumnHeader;
import rjc.jplanner.gui.data.DaysRowHeader;

/*************************************************************************************************/
/*************** Factory for making the different Nebula NatTable used in JPlanner ***************/
/*************************************************************************************************/

public class NatTableFactory
{
  private ThemeConfiguration m_theme; // theme to use for all the tables

  /**************************************** constructor ******************************************/
  public NatTableFactory()
  {
    // initialise variable
    m_theme = new ModernNatTableThemeConfiguration();
  }

  /**************************************** newTable ******************************************/
  public NatTable newTable( Composite parent, IDataProvider body, IDataProvider ch, IDataProvider rh )
  {
    // create body layer stack
    DataLayer bodyDataLayer = new DataLayer( body );
    SelectionLayer selLayer = new SelectionLayer( bodyDataLayer );
    ViewportLayer viewport = new ViewportLayer( selLayer );

    // create column header layer stack
    DataLayer chDataLayer = new DataLayer( ch );
    ColumnHeaderLayer colHeader = new ColumnHeaderLayer( chDataLayer, viewport, selLayer );

    // create row header layer stack
    DataLayer rhDataLayer = new DataLayer( rh );
    RowHeaderLayer rowHeader = new RowHeaderLayer( rhDataLayer, viewport, selLayer );

    // create corner later stack
    DataLayer cDataLayer = new DataLayer( new DefaultCornerDataProvider( ch, rh ) );
    CornerLayer corner = new CornerLayer( cDataLayer, rowHeader, colHeader );

    // create grid layer composite
    GridLayer grid = new GridLayer( viewport, colHeader, rowHeader, corner );

    // create NatTable with correct parent and theme
    NatTable table = new NatTable( parent, grid );
    table.setTheme( m_theme );
    return table;
  }

  /**************************************** newDaysTable ******************************************/
  public NatTable newDaysTable( Composite parent )
  {
    // create table for day-types
    IDataProvider body = new DaysBody();
    IDataProvider colh = new DaysColumnHeader( body );
    IDataProvider rowh = new DaysRowHeader( body );
    return newTable( parent, body, colh, rowh );
  }

  /**************************************** newDaysTable ******************************************/
  public NatTable newCalendarsTable( Composite parent )
  {
    // create table for calendars TODO
    IDataProvider body = new DaysBody();
    IDataProvider colh = new DaysColumnHeader( body );
    IDataProvider rowh = new DaysRowHeader( body );
    return newTable( parent, body, colh, rowh );
  }

  /**************************************** newDaysTable ******************************************/
  public NatTable newResourcesTable( Composite parent )
  {
    // create table for resources TODO
    IDataProvider body = new DaysBody();
    IDataProvider colh = new DaysColumnHeader( body );
    IDataProvider rowh = new DaysRowHeader( body );
    return newTable( parent, body, colh, rowh );
  }

  /**************************************** newDaysTable ******************************************/
  public NatTable newTasksTable( Composite parent )
  {
    // create table for tasks TODO
    IDataProvider body = new DaysBody();
    IDataProvider colh = new DaysColumnHeader( body );
    IDataProvider rowh = new DaysRowHeader( body );
    return newTable( parent, body, colh, rowh );
  }

}
