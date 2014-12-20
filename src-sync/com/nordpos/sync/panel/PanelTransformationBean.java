/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2014 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.sync.panel;

import com.nordpos.sync.kettle.TransVariable;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class PanelTransformationBean extends JPanelTransformation {

    private String sTitle;
    private String sTransformation;

    private final List<TransVariable> listVariable = new ArrayList<>();

    @Override
    public void init(AppView app) throws BeanFactoryException {

        super.init(app);
    }

    @Override
    public void activate() throws BasicException {

        super.activate();

    }

    public void setTitle(String title) {
        this.sTitle = title;
    }

    public void setTitleKey(String titlekey) {
        sTitle = AppLocal.getIntString(titlekey);
    }

    @Override
    public String getTitle() {
        return sTitle;
    }

    public void setTransformation(String transformation) {
        this.sTransformation = transformation;
    }

    @Override
    protected String getTransformation() {
        return sTransformation;
    }

    @Override
    protected List<TransVariable> getTransVaribles() {
        return listVariable;
    }

    public void addTransVariable(String name, String value) {
        listVariable.add(new TransVariable(name, value));
    }
}
