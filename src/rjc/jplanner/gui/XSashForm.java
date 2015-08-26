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

package rjc.jplanner.gui;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/*************************************************************************************************/
/***************** Extended version of Sashform with preferred left child width ******************/
/*************************************************************************************************/

public class XSashForm extends SashForm
{
  public int preferredLeftChildWidth = 300;

  /**************************************** constructor ******************************************/
  public XSashForm( Composite parent, int style )
  {
    super( parent, style );

    // add listener for when sashform resized to left child to preferred width
    addControlListener( new ControlListener()
    {
      @Override
      public void controlMoved( ControlEvent e )
      {
        // do nothing
      }

      @Override
      public void controlResized( ControlEvent e )
      {
        // when sashform resized re-calc weights so left child has preferred width
        setWeights();
      }
    } );

  }

  /***************************************** setWeights ******************************************/
  public void setWeights()
  {
    // calculate weights so left child has preferred width
    int width = getSize().x - 2;
    int[] weights = new int[2];
    weights[0] = preferredLeftChildWidth;
    weights[1] = width - preferredLeftChildWidth;
    if ( weights[1] < 0 )
      weights[1] = 0;

    setWeights( weights );
  }

  /****************************************** monitor ********************************************/
  public void monitor( Control left, Control right )
  {
    // add listener to detect sash drag to update preferred left child width
    XSashForm form = this;
    left.addControlListener( new ControlListener()
    {
      @Override
      public void controlMoved( ControlEvent e )
      {
        // do nothing
      }

      @Override
      public void controlResized( ControlEvent e )
      {
        // if left resize due to sash drag (and not sashform resize below preferred) then update preferred
        if ( right.getSize().x > 2 )
          form.preferredLeftChildWidth = left.getSize().x + 1;
      }
    } );

  }
}
