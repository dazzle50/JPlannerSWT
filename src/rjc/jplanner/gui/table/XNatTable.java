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

package rjc.jplanner.gui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.RowHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.reorder.action.ColumnReorderDragMode;
import org.eclipse.nebula.widgets.nattable.reorder.action.RowReorderDragMode;
import org.eclipse.nebula.widgets.nattable.resize.action.AutoResizeColumnAction;
import org.eclipse.nebula.widgets.nattable.resize.action.AutoResizeRowAction;
import org.eclipse.nebula.widgets.nattable.resize.action.ColumnResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.action.RowResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.event.ColumnResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.event.RowResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.action.AggregateDragMode;
import org.eclipse.nebula.widgets.nattable.ui.action.CellDragMode;
import org.eclipse.nebula.widgets.nattable.ui.action.ClearCursorAction;
import org.eclipse.nebula.widgets.nattable.ui.action.NoOpMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.command.CommandTaskIndent;
import rjc.jplanner.command.CommandTaskOutdent;
import rjc.jplanner.gui.Gantt;
import rjc.jplanner.gui.GanttScale;
import rjc.jplanner.gui.calendar.CalendarCellEditor;
import rjc.jplanner.gui.calendar.CalendarCellPainter;
import rjc.jplanner.gui.calendar.CalendarsBody;
import rjc.jplanner.gui.calendar.CalendarsColumnHeader;
import rjc.jplanner.gui.calendar.CalendarsLabelAccumulator;
import rjc.jplanner.gui.calendar.CalendarsRowHeader;
import rjc.jplanner.gui.day.DayCellEditor;
import rjc.jplanner.gui.day.DayCellPainter;
import rjc.jplanner.gui.day.DaysBody;
import rjc.jplanner.gui.day.DaysColumnHeader;
import rjc.jplanner.gui.day.DaysLabelAccumulator;
import rjc.jplanner.gui.day.DaysRowHeader;
import rjc.jplanner.gui.resource.ResourceCellEditor;
import rjc.jplanner.gui.resource.ResourceCellPainter;
import rjc.jplanner.gui.resource.ResourcesBody;
import rjc.jplanner.gui.resource.ResourcesColumnHeader;
import rjc.jplanner.gui.resource.ResourcesLabelAccumulator;
import rjc.jplanner.gui.resource.ResourcesRowHeader;
import rjc.jplanner.gui.task.TaskCellEditor;
import rjc.jplanner.gui.task.TaskCellPainter;
import rjc.jplanner.gui.task.TasksBody;
import rjc.jplanner.gui.task.TasksColumnHeader;
import rjc.jplanner.gui.task.TasksLabelAccumulator;
import rjc.jplanner.gui.task.TasksRowHeader;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/**************************** JPlanner table based on Nebula NatTable ****************************/
/*************************************************************************************************/

public class XNatTable extends NatTable
{
  private static ModernNatTableThemeConfiguration m_JPlannerTheme;         // theme to use for all the tables
  private static IConfiguration                   m_defaultLabelsConfig;   // to support styling of individual cells
  private static IConfiguration                   m_columnHeaderConfig;    // for column resizing and reordering
  private static IConfiguration                   m_rowHeaderConfig;       // for row resizing and reordering
  private static KeyListener                      m_indentOutdentListener; // to support indenting / outdenting

  public enum TableType
  {
    DAY, CALENDAR, RESOURCE, TASK
  }

  public static final String LABEL_CELL_EDITABLE    = "Edit";
  public static final String LABEL_DAY_EDITOR       = "DayE";
  public static final String LABEL_CALENDAR_EDITOR  = "CalE";
  public static final String LABEL_RESOURCE_EDITOR  = "ResE";
  public static final String LABEL_TASK_EDITOR      = "TaskE";
  public static final String LABEL_DAY_PAINTER      = "DayP";
  public static final String LABEL_CALENDAR_PAINTER = "CalP";
  public static final String LABEL_RESOURCE_PAINTER = "ResP";
  public static final String LABEL_TASK_PAINTER     = "TaskP";

  public SelectionLayer      selectionLayer;
  public RowHideShowLayer    rowHideShowLayer;
  public ViewportLayer       viewport;
  public DataLayer           bodyDataLayer;

  public Collection<Task>    collapsedTasks;
  public Gantt               gantt;

  private TableType          m_type;

  /**************************************** constructor ******************************************/
  public XNatTable( Composite parent, TableType type )
  {
    // call super constructor with autoconfig off
    super( parent, false );
    checkStatics();

    // depending on table type configure differently
    m_type = type;
    switch ( type )
    {
      case DAY:
        // create table for day-types
        IDataProvider body = new DaysBody();
        IDataProvider colh = new DaysColumnHeader( body );
        IDataProvider rowh = new DaysRowHeader( body );
        IConfigLabelAccumulator label = new DaysLabelAccumulator();
        int[] widthD = { 60, 25, 150 };
        buildTable( body, colh, rowh, label, widthD, -1 );
        break;

      case CALENDAR:
        // create table for calendars
        body = new CalendarsBody();
        colh = new CalendarsColumnHeader( body );
        rowh = new CalendarsRowHeader( body );
        label = new CalendarsLabelAccumulator();
        int[] widthC = { 140, 75 };
        buildTable( body, colh, rowh, label, widthC, -1 );
        break;

      case RESOURCE:
        // create table for resources
        body = new ResourcesBody();
        colh = new ResourcesColumnHeader( body );
        rowh = new ResourcesRowHeader( body );
        label = new ResourcesLabelAccumulator();
        int[] widthR = { 100, 25, 50 };
        buildTable( body, colh, rowh, label, widthR, -1 );
        break;

      case TASK:
        // create table for tasks
        body = new TasksBody();
        colh = new TasksColumnHeader( body );
        rowh = new TasksRowHeader( body );
        label = new TasksLabelAccumulator();
        int[] widthT = { 110, 25, 200, 60, 140, 140, 60, 110, 110, 110, 60, 140, 60, 200 };
        buildTable( body, colh, rowh, label, widthT, 2 * GanttScale.GANTTSCALE_HEIGHT );
        addKeyListener( m_indentOutdentListener );
        collapsedTasks = new HashSet<Task>();
        break;

      default:
        throw new IllegalArgumentException( "type " + type );
    }
    JPlanner.trace( this.toString() );
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // short string summary
    return "XNatTable@" + Integer.toHexString( hashCode() ) + "[" + m_type + " " + bodyDataLayer.getColumnCount() + "x"
        + bodyDataLayer.getRowCount() + "]";
  }

  /******************************************* type **********************************************/
  public TableType type()
  {
    // return table type
    return m_type;
  }

  /*************************************** checkStatics ******************************************/
  private void checkStatics()
  {
    // check static variables have been initialised
    if ( m_JPlannerTheme == null )
    {
      m_JPlannerTheme = new ModernNatTableThemeConfiguration();
      m_JPlannerTheme.defaultHAlign = HorizontalAlignmentEnum.CENTER;
      m_JPlannerTheme.cHeaderHAlign = HorizontalAlignmentEnum.CENTER;
      m_JPlannerTheme.rHeaderHAlign = HorizontalAlignmentEnum.CENTER;
    }

    // configuration for labels controlling editable, painters, and editors
    if ( m_defaultLabelsConfig == null )
      m_defaultLabelsConfig = new AbstractRegistryConfiguration()
      {
        @Override
        public void configureRegistry( IConfigRegistry reg )
        {
          // Editable
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
              DisplayMode.EDIT, LABEL_CELL_EDITABLE );

          // Cell painters - except task which can't be done from this static
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new DayCellPainter(), DisplayMode.NORMAL,
              LABEL_DAY_PAINTER );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new CalendarCellPainter(), DisplayMode.NORMAL,
              LABEL_CALENDAR_PAINTER );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new ResourceCellPainter(), DisplayMode.NORMAL,
              LABEL_RESOURCE_PAINTER );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new TaskCellPainter(), DisplayMode.NORMAL,
              LABEL_TASK_PAINTER );

          // Cell editors
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new DayCellEditor(), DisplayMode.EDIT,
              LABEL_DAY_EDITOR );
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new CalendarCellEditor(), DisplayMode.EDIT,
              LABEL_CALENDAR_EDITOR );
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new ResourceCellEditor(), DisplayMode.EDIT,
              LABEL_RESOURCE_EDITOR );
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new TaskCellEditor(), DisplayMode.EDIT,
              LABEL_TASK_EDITOR );
        }
      };

    // column header configuration for smooth resizing
    if ( m_columnHeaderConfig == null )
      m_columnHeaderConfig = new DefaultColumnHeaderLayerConfiguration()
      {
        @Override
        protected void addColumnHeaderUIBindings()
        {
          addConfiguration( new AbstractUiBindingConfiguration()
          {
            @Override
            public void configureUiBindings( UiBindingRegistry uiBindingRegistry )
            {
              uiBindingRegistry.registerMouseDragMode( MouseEventMatcher.columnHeaderLeftClick( SWT.NONE ),
                  new AggregateDragMode( new CellDragMode(), new ColumnReorderDragMode() ) );

              // Mouse move - Show resize cursor
              uiBindingRegistry.registerFirstMouseMoveBinding(
                  new ColumnResizeEventMatcher( SWT.NONE, GridRegion.COLUMN_HEADER, 0 ),
                  new ColumnResizeCursorAction() );
              uiBindingRegistry.registerMouseMoveBinding( new MouseEventMatcher(), new ClearCursorAction() );

              // Column resize
              uiBindingRegistry.registerFirstMouseDragMode(
                  new ColumnResizeEventMatcher( SWT.NONE, GridRegion.COLUMN_HEADER, 1 ), new XColumnResizeDragMode() );

              uiBindingRegistry.registerDoubleClickBinding(
                  new ColumnResizeEventMatcher( SWT.NONE, GridRegion.COLUMN_HEADER, 1 ), new AutoResizeColumnAction() );
              uiBindingRegistry.registerSingleClickBinding(
                  new ColumnResizeEventMatcher( SWT.NONE, GridRegion.COLUMN_HEADER, 1 ), new NoOpMouseAction() );
            }
          } );
        }
      };

    // NatTable row header configuration for smooth resizing
    if ( m_rowHeaderConfig == null )
      m_rowHeaderConfig = new DefaultRowHeaderLayerConfiguration()
      {
        @Override
        protected void addRowHeaderUIBindings()
        {
          addConfiguration( new AbstractUiBindingConfiguration()
          {
            @Override
            public void configureUiBindings( UiBindingRegistry uiBindingRegistry )
            {
              uiBindingRegistry.registerMouseDragMode( MouseEventMatcher.rowHeaderLeftClick( SWT.NONE ),
                  new AggregateDragMode( new CellDragMode(), new RowReorderDragMode() ) );

              // Mouse move - Show resize cursor
              uiBindingRegistry.registerFirstMouseMoveBinding( new RowResizeEventMatcher( SWT.NONE, 0 ),
                  new RowResizeCursorAction() );
              uiBindingRegistry.registerMouseMoveBinding( new MouseEventMatcher(), new ClearCursorAction() );

              // Row resize
              uiBindingRegistry.registerFirstMouseDragMode( new RowResizeEventMatcher( SWT.NONE, 1 ),
                  new XRowResizeDragMode() );

              uiBindingRegistry.registerDoubleClickBinding( new RowResizeEventMatcher( SWT.NONE, 1 ),
                  new AutoResizeRowAction() );
              uiBindingRegistry.registerSingleClickBinding( new RowResizeEventMatcher( SWT.NONE, 1 ),
                  new NoOpMouseAction() );
            }
          } );
        }
      };

    // NatTable key adapter for indenting and outdenting tasks
    if ( m_indentOutdentListener == null )
      m_indentOutdentListener = new KeyAdapter()
      {
        @Override
        public void keyPressed( KeyEvent event )
        {
          // detect indent
          if ( event.stateMask == SWT.ALT && event.keyCode == SWT.ARROW_RIGHT )
          {
            Set<Integer> rows = JPlanner.plan.tasks.canIndent( getSelectedRowsIndexes() );
            if ( !rows.isEmpty() )
              JPlanner.plan.undostack().push( new CommandTaskIndent( rows ) );
          }

          // detect outdent
          if ( event.stateMask == SWT.ALT && event.keyCode == SWT.ARROW_LEFT )
          {
            Set<Integer> rows = JPlanner.plan.tasks.canOutdent( getSelectedRowsIndexes() );
            if ( !rows.isEmpty() )
              JPlanner.plan.undostack().push( new CommandTaskOutdent( rows ) );
          }
        }
      };
  }

  /***************************************** buildTable ******************************************/
  private void buildTable( IDataProvider body, IDataProvider ch, IDataProvider rh, IConfigLabelAccumulator label,
      int[] widths, int chHeight )
  {
    // create body layer stack
    bodyDataLayer = new DataLayer( body, widths[0], 20 );
    bodyDataLayer.setConfigLabelAccumulator( label );
    rowHideShowLayer = new RowHideShowLayer( bodyDataLayer );
    selectionLayer = new SelectionLayer( rowHideShowLayer );
    viewport = new ViewportLayer( selectionLayer );

    // widths - first is default, then row header, then columns
    for ( int i = 2; i < widths.length; i++ )
      bodyDataLayer.setColumnWidthByPosition( i - 2, widths[i] );

    // create column header layer stack
    DataLayer chDataLayer = new DataLayer( ch );
    if ( chHeight > 0 )
      chDataLayer.setRowHeightByPosition( 0, chHeight );
    ColumnHeaderLayer colHeader = new ColumnHeaderLayer( chDataLayer, viewport, selectionLayer, false );
    colHeader.addConfiguration( m_columnHeaderConfig );

    // create row header layer stack
    DataLayer rhDataLayer = new DataLayer( rh, widths[1], 20 );
    RowHeaderLayer rowHeader = new RowHeaderLayer( rhDataLayer, viewport, selectionLayer, false );
    rowHeader.addConfiguration( m_rowHeaderConfig );

    // create corner later stack
    DataLayer cDataLayer = new DataLayer( new DefaultCornerDataProvider( ch, rh ) );
    CornerLayer corner = new CornerLayer( cDataLayer, rowHeader, colHeader );

    // create grid layer composite
    GridLayer grid = new GridLayer( viewport, colHeader, rowHeader, corner, false );
    grid.addConfiguration( new XGridLayerConfiguration() );

    // configure NatTable with grid and theme
    setLayer( grid );
    addConfiguration( m_JPlannerTheme );
    addConfiguration( m_defaultLabelsConfig );
    configure();
  }

  /*********************************** getSelectedRowsIndexes ************************************/
  public Set<Integer> getSelectedRowsIndexes()
  {
    // return set of selected row indexes
    Set<Integer> rows = new HashSet<Integer>();

    for ( Range range : selectionLayer.getSelectedRowPositions() )
    {
      for ( int pos = range.start; pos < range.end; ++pos )
        rows.add( selectionLayer.getRowIndexByPosition( pos ) );
    }

    return rows;
  }

  /****************************************** hideRow ********************************************/
  public void hideRow( int row )
  {
    // hide table row index
    ArrayList<Integer> rowList = new ArrayList<Integer>();
    rowList.add( row );
    rowHideShowLayer.hideRowIndexes( rowList );
  }

  /**************************************** isRowHidden ******************************************/
  public boolean isRowHidden( int row )
  {
    // return true if specified row hidden
    return rowHideShowLayer.isRowIndexHidden( row );
  }

  /***************************************** getRowAtY *******************************************/
  public int getRowAtY( int y )
  {
    // returns the row in which the specified y-coordinate
    return viewport.getRowIndexByPosition( viewport.getRowPositionByY( y ) );
  }

  /*************************************** getRowStartY ******************************************/
  public int getRowStartY( int row )
  {
    // return start-y of specified row
    return viewport.getStartYOfRowPosition( viewport.getRowPositionByIndex( row ) );
  }

  /*************************************** getRowHeight ******************************************/
  public int getRowHeight( int row )
  {
    // return height of specified row
    return viewport.getRowHeightByPosition( viewport.getRowPositionByIndex( row ) );
  }

  /*************************************** setRowHeight ******************************************/
  public void setRowHeight( int row, int height )
  {
    // set width of specified column
    int pos = viewport.getRowPositionByIndex( row );
    bodyDataLayer.setRowHeightByPosition( pos, height );
  }

  /*************************************** getRowMiddleY *****************************************/
  public int getRowMiddleY( int row )
  {
    // return middle-y of specified row index
    int pos = viewport.getRowPositionByIndex( row );
    return viewport.getStartYOfRowPosition( pos ) + viewport.getRowHeightByPosition( pos ) / 2;
  }

  /*************************************** getColumnWidth ****************************************/
  private int getColumnWidth( int col )
  {
    // return width of specified column
    return viewport.getColumnWidthByPosition( viewport.getColumnPositionByIndex( col ) );
  }

  /*************************************** setColumnWidth ****************************************/
  private void setColumnWidth( int col, int width )
  {
    // set width of specified column
    bodyDataLayer.setColumnWidthByPosition( viewport.getColumnPositionByIndex( col ), width );
  }

  /*********************************** setRowsHeightToDefault ************************************/
  public void setRowsHeightToDefault()
  {
    // show all hidden rows and reset height to default
    rowHideShowLayer.showAllRows();
    if ( collapsedTasks != null )
      collapsedTasks.clear();

    int count = bodyDataLayer.getRowCount();
    for ( int row = 0; row < count; row++ )
      bodyDataLayer.setRowHeightByPosition( row, DataLayer.DEFAULT_ROW_HEIGHT );
  }

  /****************************************** writeXML *******************************************/
  public void writeXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write column widths
    xsw.writeStartElement( XmlLabels.XML_COLUMNS );
    int count = bodyDataLayer.getColumnCount();
    for ( int col = 0; col < count; col++ )
    {
      xsw.writeStartElement( XmlLabels.XML_COLUMN );
      xsw.writeAttribute( XmlLabels.XML_ID, Integer.toString( col ) );
      xsw.writeAttribute( XmlLabels.XML_WIDTH, Integer.toString( getColumnWidth( col ) ) );
      xsw.writeEndElement(); // XML_COLUMN
    }
    xsw.writeEndElement(); // XML_COLUMNS

    // write row heights
    xsw.writeStartElement( XmlLabels.XML_ROWS );
    count = bodyDataLayer.getRowCount();
    for ( int row = 0; row < count; row++ )
    {
      xsw.writeStartElement( XmlLabels.XML_ROW );
      xsw.writeAttribute( XmlLabels.XML_ID, Integer.toString( row ) );
      xsw.writeAttribute( XmlLabels.XML_HEIGHT, Integer.toString( getRowHeight( row ) ) );
      if ( m_type == TableType.TASK )
      {
        if ( isRowHidden( row ) )
          xsw.writeAttribute( XmlLabels.XML_HIDDEN, "true" );
        if ( collapsedTasks.contains( JPlanner.plan.task( row ) ) )
          xsw.writeAttribute( XmlLabels.XML_COLLAPSED, "true" );
      }
      xsw.writeEndElement(); // XML_ROW
    }
    xsw.writeEndElement(); // XML_ROWS
  }

  /***************************************** loadColumns *****************************************/
  public void loadColumns( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML columns data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of columns data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_COLUMNS ) )
        return;

      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_COLUMN:

            // get attributes from column element to set column width
            int id = -1;
            int width = DataLayer.DEFAULT_COLUMN_WIDTH;
            for ( int i = 0; i < xsr.getAttributeCount(); i++ )
              switch ( xsr.getAttributeLocalName( i ) )
              {
                case XmlLabels.XML_ID:
                  id = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_WIDTH:
                  width = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                default:
                  JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
                  break;
              }
            if ( id >= 0 && id < bodyDataLayer.getColumnCount() )
              setColumnWidth( id, width );
            break;

          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }
  }

  /****************************************** loadRows *******************************************/
  public void loadRows( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML rows data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of rows data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_ROWS ) )
        return;

      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_ROW:

            // get attributes from row element to set row height and hidden
            int id = -1;
            int height = DataLayer.DEFAULT_ROW_HEIGHT;
            boolean hidden = false;
            boolean collapsed = false;

            for ( int i = 0; i < xsr.getAttributeCount(); i++ )
              switch ( xsr.getAttributeLocalName( i ) )
              {
                case XmlLabels.XML_ID:
                  id = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_HEIGHT:
                  height = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_HIDDEN:
                  hidden = Boolean.parseBoolean( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_COLLAPSED:
                  collapsed = Boolean.parseBoolean( xsr.getAttributeValue( i ) );
                  break;
                default:
                  JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
                  break;
              }

            if ( id >= 0 && id < bodyDataLayer.getRowCount() )
            {
              setRowHeight( id, height );
              if ( hidden )
                hideRow( id );
              if ( collapsed && collapsedTasks != null )
                collapsedTasks.add( JPlanner.plan.task( id ) );
            }
            break;

          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }
  }

  /************************************ getExpandCollapseList ************************************/
  private Collection<Integer> getExpandCollapseList( int row )
  {
    // construct list of rows to be hidden or shown for specified summary row
    ArrayList<Integer> list = new ArrayList<Integer>();
    int end = JPlanner.plan.task( row ).summaryEnd();
    for ( int index = row + 1; index <= end; index++ )
    {
      list.add( index );

      // skip over already collapsed sub-summaries
      Task task = JPlanner.plan.task( index );
      if ( collapsedTasks.contains( task ) )
        index = task.summaryEnd();
    }

    return list;
  }

  /**************************************** expandSummary ****************************************/
  public void expandSummary( int row )
  {
    // expand task summary
    rowHideShowLayer.showRowIndexes( getExpandCollapseList( row ) );
    collapsedTasks.remove( JPlanner.plan.task( row ) );
  }

  /*************************************** collapseSummary ***************************************/
  public void collapseSummary( int row )
  {
    // collapse task summary
    rowHideShowLayer.hideRowIndexes( getExpandCollapseList( row ) );
    collapsedTasks.add( JPlanner.plan.task( row ) );
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // refresh the table but keep selected cells
    PositionCoordinate[] pcs = selectionLayer.getSelectedCellPositions();
    PositionCoordinate last = selectionLayer.getLastSelectedCellPosition();
    refresh();

    for ( PositionCoordinate pc : pcs )
      selectionLayer.selectCell( pc.columnPosition, pc.rowPosition, false, true );
    if ( last != null )
      selectionLayer.setLastSelectedCell( last.columnPosition, last.rowPosition );
  }

}