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

import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AggregateConfiguration;
import org.eclipse.nebula.widgets.nattable.edit.action.KeyEditAction;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.export.config.DefaultExportBindings;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.print.config.DefaultPrintBindings;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.swt.SWT;

/*************************************************************************************************/
/************ Based on DefaultGridLayerConfiguration but cell editor on double-click *************/
/*************************************************************************************************/

public class XGridLayerConfiguration extends AggregateConfiguration
{
  /**************************************** constructor ******************************************/
  public XGridLayerConfiguration()
  {
    // add default editing handler config
    addConfiguration( new DefaultEditConfiguration() );

    // instead of default editing UI config "DefaultEditBindings"
    addConfiguration( new AbstractUiBindingConfiguration()
    {
      @Override
      public void configureUiBindings( UiBindingRegistry uiBindingRegistry )
      {
        uiBindingRegistry.registerKeyBinding( new KeyEventMatcher( SWT.NONE, SWT.F2 ), new KeyEditAction() );
        uiBindingRegistry.registerKeyBinding( new LetterOrDigitKeyEventMatcher(), new KeyEditAction() );
        uiBindingRegistry.registerKeyBinding( new LetterOrDigitKeyEventMatcher( SWT.SHIFT ), new KeyEditAction() );

        uiBindingRegistry.registerFirstDoubleClickBinding( new XCellEditorMouseEventMatcher( GridRegion.BODY ),
            new MouseEditAction() );
      }
    } );

    // add default print UI bindings
    addConfiguration( new DefaultPrintBindings() );

    // add default excel export UI bindings
    addConfiguration( new DefaultExportBindings() );
  }

}