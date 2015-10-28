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
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
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
import rjc.jplanner.gui.editor.CalendarCellEditor;
import rjc.jplanner.gui.editor.DayCellEditor;
import rjc.jplanner.gui.editor.ResourceCellEditor;
import rjc.jplanner.gui.editor.TaskCellEditor;

/*************************************************************************************************/
/**************************** JPlanner table based on Nebula NatTable ****************************/
/*************************************************************************************************/

public class XNatTable extends NatTable
{
  private static ModernNatTableThemeConfiguration m_theme;                 // theme to use for all the tables
  private static IConfiguration                   m_labels;                // to support styling of individual cells
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

  /**************************************** constructor ******************************************/
  public XNatTable( Composite parent, TableType type )
  {
    // call super constructor with autoconfig off
    super( parent, false );
    checkStatics();

    // depending on table type configure differently
    switch ( type )
    {
      case DAY:
        // create table for day-types
        IDataProvider body = new DaysBody();
        IDataProvider colh = new DaysColumnHeader( body );
        IDataProvider rowh = new DaysRowHeader( body );
        IConfigLabelAccumulator label = new DaysLabelAccumulator();
        int[] widthD = { 60, 25, 150 };
        configTable( body, colh, rowh, label, widthD, -1 );
        break;

      case CALENDAR:
        // create table for calendars
        body = new CalendarsBody();
        colh = new CalendarsColumnHeader( body );
        rowh = new CalendarsRowHeader( body );
        label = new CalendarsLabelAccumulator();
        int[] widthC = { 140, 75 };
        configTable( body, colh, rowh, label, widthC, -1 );
        break;

      case RESOURCE:
        // create table for resources
        body = new ResourcesBody();
        colh = new ResourcesColumnHeader( body );
        rowh = new ResourcesRowHeader( body );
        label = new ResourcesLabelAccumulator();
        int[] widthR = { 100, 25, 50 };
        configTable( body, colh, rowh, label, widthR, -1 );
        break;

      case TASK:
        // create table for tasks
        body = new TasksBody();
        colh = new TasksColumnHeader( body );
        rowh = new TasksRowHeader( body );
        label = new TasksLabelAccumulator();
        int[] widthT = { 110, 25, 200, 60, 140, 140, 60, 110, 110, 110, 60, 140, 60, 200 };
        configTable( body, colh, rowh, label, widthT, 2 * JPlanner.gui.GANTTSCALE_HEIGHT );
        addKeyListener( m_indentOutdentListener );
        break;

      default:
        throw new IllegalArgumentException( "type " + type );
    }
  }

  /*************************************** checkStatics ******************************************/
  private void checkStatics()
  {
    // check static variables have been initialised
    if ( m_theme == null )
    {
      m_theme = new ModernNatTableThemeConfiguration();
      m_theme.defaultHAlign = HorizontalAlignmentEnum.CENTER;
      m_theme.cHeaderHAlign = HorizontalAlignmentEnum.CENTER;
      m_theme.rHeaderHAlign = HorizontalAlignmentEnum.CENTER;
    }

    if ( m_labels == null )
      m_labels = new AbstractRegistryConfiguration()
      {
        @Override
        public void configureRegistry( IConfigRegistry reg )
        {
          // Editable
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
              DisplayMode.EDIT, LABEL_CELL_EDITABLE );

          // Cell painters
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

    if ( m_indentOutdentListener == null )
      m_indentOutdentListener = new KeyAdapter()
      {
        @Override
        public void keyPressed( KeyEvent event )
        {
          // detect indent
          if ( event.stateMask == SWT.ALT && event.keyCode == SWT.ARROW_RIGHT )
          {
            Set<Integer> rows = JPlanner.plan.tasks.canIndent( selectedRows() );
            if ( !rows.isEmpty() )
              JPlanner.plan.undostack().push( new CommandTaskIndent( rows ) );
          }

          // detect outdent
          if ( event.stateMask == SWT.ALT && event.keyCode == SWT.ARROW_LEFT )
          {
            Set<Integer> rows = JPlanner.plan.tasks.canOutdent( selectedRows() );
            if ( !rows.isEmpty() )
              JPlanner.plan.undostack().push( new CommandTaskOutdent( rows ) );
          }
        }
      };
  }

  /**************************************** configTable ******************************************/
  private void configTable( IDataProvider body, IDataProvider ch, IDataProvider rh, IConfigLabelAccumulator label,
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

    // configure NatTable with grid, theme, and labels
    setLayer( grid );
    addConfiguration( m_theme );
    addConfiguration( m_labels );
    configure();
  }

  /**************************************** selectedRows *****************************************/
  public Set<Integer> selectedRows()
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

  /******************************************* rowAt *********************************************/
  public int rowAt( int y )
  {
    // returns the row in which the specified y-coordinate
    return viewport.getRowIndexByPosition( viewport.getRowPositionByY( y ) );
  }

  /**************************************** isRowHidden ******************************************/
  public boolean isRowHidden( int row )
  {
    // return true if specified row hidden
    return rowHideShowLayer.isRowIndexHidden( row );
  }

  /******************************************** rowY *********************************************/
  public int rowY( int row )
  {
    // return start-y of specified row
    return viewport.getStartYOfRowPosition( viewport.getRowPositionByIndex( row ) );
  }

  /****************************************** rowHeight ******************************************/
  public int rowHeight( int row )
  {
    // return height of specified row
    return viewport.getRowHeightByPosition( viewport.getRowPositionByIndex( row ) );
  }

  /***************************************** getMiddleY ******************************************/
  public int getMiddleY( int index )
  {
    // return middle-y of specified row index
    int pos = viewport.getRowPositionByIndex( index );
    return viewport.getStartYOfRowPosition( pos ) + viewport.getRowHeightByPosition( pos ) / 2;
  }

  /***************************************** columnWidth *****************************************/
  private int columnWidth( int col )
  {
    // return width of specified column
    return viewport.getColumnWidthByPosition( viewport.getColumnPositionByIndex( col ) );
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
      xsw.writeAttribute( XmlLabels.XML_WIDTH, Integer.toString( columnWidth( col ) ) );
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
      xsw.writeAttribute( XmlLabels.XML_HEIGHT, Integer.toString( rowHeight( row ) ) );
      xsw.writeEndElement(); // XML_ROW
    }
    xsw.writeEndElement(); // XML_ROWS
  }

}