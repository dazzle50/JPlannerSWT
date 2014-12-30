package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainWindowShell extends Shell
{

  /**
   * Launch the application.
   * @param args
   */
  public static void main( String args[] )
  {
    try
    {
      Display display = Display.getDefault();
      MainWindowShell shell = new MainWindowShell( display );
      shell.open();
      shell.layout();
      while ( !shell.isDisposed() )
      {
        if ( !display.readAndDispatch() )
        {
          display.sleep();
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Create the shell.
   * @param display
   */
  public MainWindowShell( Display display )
  {
    super( display, SWT.SHELL_TRIM );
    setLayout( new FillLayout( SWT.HORIZONTAL ) );

    Menu menuBar = new Menu( this, SWT.BAR );
    setMenuBar( menuBar );

    MenuItem menuFile = new MenuItem( menuBar, SWT.CASCADE );
    menuFile.setText( "File" );

    Menu menu_1 = new Menu( menuFile );
    menuFile.setMenu( menu_1 );

    MenuItem actionNew = new MenuItem( menu_1, SWT.NONE );
    actionNew.setEnabled( false );
    actionNew.setText( "New" );

    MenuItem actionOpen = new MenuItem( menu_1, SWT.NONE );
    actionOpen.setEnabled( false );
    actionOpen.setText( "Open ..." );

    MenuItem actionSave = new MenuItem( menu_1, SWT.NONE );
    actionSave.setEnabled( false );
    actionSave.setText( "Save" );

    MenuItem actionSaveAs = new MenuItem( menu_1, SWT.NONE );
    actionSaveAs.setEnabled( false );
    actionSaveAs.setText( "Save As ..." );

    new MenuItem( menu_1, SWT.SEPARATOR );

    MenuItem actionPrintPreview = new MenuItem( menu_1, SWT.NONE );
    actionPrintPreview.setEnabled( false );
    actionPrintPreview.setText( "Print preview ..." );

    MenuItem actionPrint = new MenuItem( menu_1, SWT.NONE );
    actionPrint.setEnabled( false );
    actionPrint.setText( "Print ..." );

    new MenuItem( menu_1, SWT.SEPARATOR );

    MenuItem actionExit = new MenuItem( menu_1, SWT.NONE );
    actionExit.setEnabled( false );
    actionExit.setText( "Exit" );

    MenuItem menuEdit = new MenuItem( menuBar, SWT.CASCADE );
    menuEdit.setText( "Edit" );

    Menu menu_2 = new Menu( menuEdit );
    menuEdit.setMenu( menu_2 );

    MenuItem actionUndo = new MenuItem( menu_2, SWT.NONE );
    actionUndo.setEnabled( false );
    actionUndo.setText( "Undo" );

    MenuItem actionRedo = new MenuItem( menu_2, SWT.NONE );
    actionRedo.setText( "Redo" );
    actionRedo.setEnabled( false );

    new MenuItem( menu_2, SWT.SEPARATOR );

    MenuItem actionInsert = new MenuItem( menu_2, SWT.NONE );
    actionInsert.setText( "Insert" );
    actionInsert.setEnabled( false );

    new MenuItem( menu_2, SWT.SEPARATOR );

    MenuItem actionCut = new MenuItem( menu_2, SWT.NONE );
    actionCut.setText( "Cut" );
    actionCut.setEnabled( false );

    MenuItem actionCopy = new MenuItem( menu_2, SWT.NONE );
    actionCopy.setText( "Copy" );
    actionCopy.setEnabled( false );

    MenuItem actionPaste = new MenuItem( menu_2, SWT.NONE );
    actionPaste.setText( "Paste" );
    actionPaste.setEnabled( false );

    MenuItem actionDelete = new MenuItem( menu_2, SWT.NONE );
    actionDelete.setText( "Delete" );
    actionDelete.setEnabled( false );

    MenuItem menuItem = new MenuItem( menu_2, SWT.SEPARATOR );

    MenuItem actionFindReplace = new MenuItem( menu_2, SWT.NONE );
    actionFindReplace.setText( "Find/Replace ..." );
    actionFindReplace.setEnabled( false );

    MenuItem actionSchedule = new MenuItem( menu_2, SWT.NONE );
    actionSchedule.setText( "Schedule" );
    actionSchedule.setEnabled( false );

    MenuItem menuTask = new MenuItem( menuBar, SWT.CASCADE );
    menuTask.setText( "Task" );

    Menu menu_3 = new Menu( menuTask );
    menuTask.setMenu( menu_3 );

    MenuItem actionIndent = new MenuItem( menu_3, SWT.NONE );
    actionIndent.setEnabled( false );
    actionIndent.setText( "Indent" );

    MenuItem actionOutdent = new MenuItem( menu_3, SWT.NONE );
    actionOutdent.setEnabled( false );
    actionOutdent.setText( "Outdent" );

    MenuItem menuReport = new MenuItem( menuBar, SWT.CASCADE );
    menuReport.setText( "Report" );

    Menu menu_4 = new Menu( menuReport );
    menuReport.setMenu( menu_4 );

    MenuItem actionTBD = new MenuItem( menu_4, SWT.NONE );
    actionTBD.setEnabled( false );
    actionTBD.setText( "TBD" );

    MenuItem menuView = new MenuItem( menuBar, SWT.CASCADE );
    menuView.setText( "View" );

    Menu menu_5 = new Menu( menuView );
    menuView.setMenu( menu_5 );

    MenuItem actionUndoStackView = new MenuItem( menu_5, SWT.NONE );
    actionUndoStackView.setEnabled( false );
    actionUndoStackView.setText( "Undo Stack ..." );

    MenuItem actionNewWindow = new MenuItem( menu_5, SWT.NONE );
    actionNewWindow.setText( "New window ..." );
    actionNewWindow.setEnabled( false );

    new MenuItem( menu_5, SWT.SEPARATOR );

    MenuItem actionStretchTasks = new MenuItem( menu_5, SWT.NONE );
    actionStretchTasks.setText( "Stretch tasks" );
    actionStretchTasks.setEnabled( false );

    MenuItem menuHelp = new MenuItem( menuBar, SWT.CASCADE );
    menuHelp.setText( "Help" );

    Menu menu_6 = new Menu( menuHelp );
    menuHelp.setMenu( menu_6 );

    MenuItem actionAboutJplanner = new MenuItem( menu_6, SWT.NONE );
    actionAboutJplanner.setEnabled( false );
    actionAboutJplanner.setText( "About JPlanner" );

    MainTabWidget mainTabWidget = new MainTabWidget( this, SWT.NONE );
    createContents();
  }

  /**
   * Create contents of the shell.
   */
  protected void createContents()
  {
    setText( "JPlanner" );
    setSize( 650, 400 );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }
}
