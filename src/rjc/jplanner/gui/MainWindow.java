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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/******************************* Main JPlanner application window ********************************/
/*************************************************************************************************/

public class MainWindow extends Shell
{
  public static Color           GANTT_BACKGROUND;
  public static Color           GANTT_NONWORKING;
  public static Transform       TRANSFORM;

  public static UndoStackWindow undoWindow;         // window to show plan undo-stack
  public static MenuItem        actionUndoStackView; // action to show plan undo-stack window
  public static MenuItem        actionUndo;
  public static MenuItem        actionRedo;

  /**************************************** constructor ******************************************/
  public MainWindow( Display display )
  {
    // create JPlanner main window
    super( display, SWT.SHELL_TRIM );
    setSize( 650, 500 );
    setText( "JPlanner" );
    setLayout( new FillLayout( SWT.HORIZONTAL ) );

    // initialise some static variables for use elsewhere
    GANTT_BACKGROUND = display.getSystemColor( SWT.COLOR_WHITE );
    GANTT_NONWORKING = new Color( display, 240, 240, 240 );
    TRANSFORM = new Transform( display );

    // add menus
    Menu menuBar = new Menu( this, SWT.BAR );
    setMenuBar( menuBar );
    addFileMenu( menuBar );
    addEditMenu( menuBar );
    addTaskMenu( menuBar );
    addReportMenu( menuBar );
    addViewMenu( menuBar );
    addHelpMenu( menuBar );

    new MainTabWidget( this );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /**************************************** addFileMenu ******************************************/
  private void addFileMenu( Menu menuBar )
  {
    // create file menu
    MenuItem menuFile = new MenuItem( menuBar, SWT.CASCADE );
    menuFile.setText( "File" );
    Menu fileMenu = new Menu( menuFile );
    menuFile.setMenu( fileMenu );

    // add file menu items
    MenuItem actionNew = new MenuItem( fileMenu, SWT.NONE );
    actionNew.setAccelerator( SWT.CTRL + 'N' );
    actionNew.setText( "New\tCtrl+N" );
    actionNew.setEnabled( false );

    MenuItem actionOpen = new MenuItem( fileMenu, SWT.NONE );
    actionOpen.setAccelerator( SWT.CTRL + 'O' );
    actionOpen.setText( "Open...\tCtrl+O" );
    actionOpen.setEnabled( false );

    MenuItem actionSave = new MenuItem( fileMenu, SWT.NONE );
    actionSave.setAccelerator( SWT.CTRL + 'S' );
    actionSave.setText( "Save\tCtrl+S" );
    actionSave.setEnabled( false );

    MenuItem actionSaveAs = new MenuItem( fileMenu, SWT.NONE );
    actionSaveAs.setText( "Save As..." );
    actionSaveAs.setEnabled( false );

    new MenuItem( fileMenu, SWT.SEPARATOR );

    MenuItem actionPrintPreview = new MenuItem( fileMenu, SWT.NONE );
    actionPrintPreview.setText( "Print preview..." );
    actionPrintPreview.setEnabled( false );

    MenuItem actionPrint = new MenuItem( fileMenu, SWT.NONE );
    actionPrint.setText( "Print..." );
    actionPrint.setEnabled( false );

    new MenuItem( fileMenu, SWT.SEPARATOR );

    MenuItem actionExit = new MenuItem( fileMenu, SWT.NONE );
    actionExit.setAccelerator( SWT.CTRL + 'Q' );
    actionExit.setText( "Exit\tCtrl+Q" );
    actionExit.setEnabled( false );
  }

  /**************************************** addEditMenu ******************************************/
  private void addEditMenu( Menu menuBar )
  {
    // create edit menu
    MenuItem menuEdit = new MenuItem( menuBar, SWT.CASCADE );
    menuEdit.setText( "Edit" );
    Menu editMenu = new Menu( menuEdit );
    menuEdit.setMenu( editMenu );

    // add edit menu items
    actionUndo = new MenuItem( editMenu, SWT.NONE );
    actionUndo.setText( "Undo\tCtrl+Z" );
    actionUndo.setAccelerator( SWT.CTRL + 'Z' );
    actionUndo.setEnabled( false );
    actionUndo.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        JPlanner.plan.undostack().undo();
      }
    } );

    actionRedo = new MenuItem( editMenu, SWT.NONE );
    actionRedo.setText( "Redo\tCtrl+Y" );
    actionRedo.setAccelerator( SWT.CTRL + 'Y' );
    actionRedo.setEnabled( false );
    actionRedo.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        JPlanner.plan.undostack().redo();
      }
    } );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionInsert = new MenuItem( editMenu, SWT.NONE );
    actionInsert.setAccelerator( SWT.INSERT );
    actionInsert.setText( "Insert\tIns" );
    actionInsert.setEnabled( false );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionCut = new MenuItem( editMenu, SWT.NONE );
    actionCut.setAccelerator( SWT.CTRL + 'X' );
    actionCut.setText( "Cut\tCtrl+X" );
    actionCut.setEnabled( false );

    MenuItem actionCopy = new MenuItem( editMenu, SWT.NONE );
    actionCopy.setAccelerator( SWT.CTRL + 'C' );
    actionCopy.setText( "Copy\tCtrl+C" );
    actionCopy.setEnabled( false );

    MenuItem actionPaste = new MenuItem( editMenu, SWT.NONE );
    actionPaste.setAccelerator( SWT.CTRL + 'V' );
    actionPaste.setText( "Paste\tCtrl+V" );
    actionPaste.setEnabled( false );

    MenuItem actionDelete = new MenuItem( editMenu, SWT.NONE );
    actionDelete.setAccelerator( SWT.DEL );
    actionDelete.setText( "Delete\tDel" );
    actionDelete.setEnabled( false );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionFindReplace = new MenuItem( editMenu, SWT.NONE );
    actionFindReplace.setAccelerator( SWT.CTRL + 'F' );
    actionFindReplace.setText( "Find/Replace...\tCtrl+F" );
    actionFindReplace.setEnabled( false );

    MenuItem actionSchedule = new MenuItem( editMenu, SWT.NONE );
    actionSchedule.setText( "Schedule" );
    actionSchedule.setEnabled( false );
  }

  /**************************************** addTaskMenu ******************************************/
  private void addTaskMenu( Menu menuBar )
  {
    // create task menu
    MenuItem menuTask = new MenuItem( menuBar, SWT.CASCADE );
    menuTask.setText( "Task" );
    Menu taskMenu = new Menu( menuTask );
    menuTask.setMenu( taskMenu );

    // add task menu items
    MenuItem actionIndent = new MenuItem( taskMenu, SWT.NONE );
    actionIndent.setText( "Indent" );
    actionIndent.setEnabled( false );

    MenuItem actionOutdent = new MenuItem( taskMenu, SWT.NONE );
    actionOutdent.setText( "Outdent" );
    actionOutdent.setEnabled( false );
  }

  /**************************************** addViewMenu ******************************************/
  private void addViewMenu( Menu menuBar )
  {
    // create view menu
    MenuItem menuView = new MenuItem( menuBar, SWT.CASCADE );
    menuView.setText( "View" );
    Menu viewMenu = new Menu( menuView );
    menuView.setMenu( viewMenu );

    // add view menu items
    actionUndoStackView = new MenuItem( viewMenu, SWT.CHECK );
    actionUndoStackView.setText( "Undo Stack..." );
    actionUndoStackView.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        if ( undoWindow == null )
        {
          undoWindow = new UndoStackWindow( MainWindow.this.getDisplay() );
          undoWindow.open();
        }
        undoWindow.setVisible( actionUndoStackView.getSelection() );
        undoWindow.setMinimized( false );
        undoWindow.forceActive();
      }
    } );

    MenuItem actionNewWindow = new MenuItem( viewMenu, SWT.NONE );
    actionNewWindow.setText( "New window..." );
    actionNewWindow.setEnabled( false );

    new MenuItem( viewMenu, SWT.SEPARATOR );

    MenuItem actionStretchTasks = new MenuItem( viewMenu, SWT.NONE );
    actionStretchTasks.setText( "Stretch tasks" );
    actionStretchTasks.setEnabled( false );
  }

  /*************************************** addReportMenu *****************************************/
  private void addReportMenu( Menu menuBar )
  {
    // create report menu
    MenuItem menuReport = new MenuItem( menuBar, SWT.CASCADE );
    menuReport.setText( "Report" );
    Menu reportMenu = new Menu( menuReport );
    menuReport.setMenu( reportMenu );

    // add report menu items
    MenuItem actionTBD = new MenuItem( reportMenu, SWT.NONE );
    actionTBD.setText( "TBD" );
    actionTBD.setEnabled( false );
  }

  /**************************************** addHelpMenu ******************************************/
  private void addHelpMenu( Menu menuBar )
  {
    // create help menu
    MenuItem menuHelp = new MenuItem( menuBar, SWT.CASCADE );
    menuHelp.setText( "Help" );
    Menu helpMenu = new Menu( menuHelp );
    menuHelp.setMenu( helpMenu );

    // add help menu items
    MenuItem actionAboutJplanner = new MenuItem( helpMenu, SWT.NONE );
    actionAboutJplanner.setText( "About JPlanner" );
    actionAboutJplanner.setEnabled( false );
  }

}
