//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008 Openbravo, S.L.
//    http://sourceforge.net/projects/openbravopos
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin Street, Fifth floor, Boston, MA  02110-1301  USA

package com.openbravo.sync.kettle;


import java.util.ArrayList;
import java.util.List;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.RowListener;

/**
 *
 * @author Mikel Irurita
 */
public class RowStepCollector implements RowListener {
    private List<RowMetaAndData> rowsRead;
    private List<RowMetaAndData> rowsWritten;
    private List<RowMetaAndData> rowsError;

    public RowStepCollector() {
        rowsRead = new ArrayList<RowMetaAndData>();
	rowsWritten = new ArrayList<RowMetaAndData>();
        rowsError = new ArrayList<RowMetaAndData>();
    }

    @Override
    public void rowReadEvent(RowMetaInterface rowMeta, Object[] row) {
    	rowsRead.add(new RowMetaAndData(rowMeta, row));
    }

    @Override
    public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) {
    	rowsWritten.add(new RowMetaAndData(rowMeta, row));
    }

    @Override
    public void errorRowWrittenEvent(RowMetaInterface rowMeta, Object[] row) {
    	rowsError.add(new RowMetaAndData(rowMeta, row));
    }

    /**
     * Clear the rows read and rows written.
     */
    public void clear() {
        rowsRead.clear();
        rowsWritten.clear();
        rowsError.clear();
    }

    public List<RowMetaAndData> getRowsRead() {
        return rowsRead;
    }

    public List<RowMetaAndData> getRowsWritten() {
        return rowsWritten;
    }

    public List<RowMetaAndData> getRowsError() {
        return rowsError;
    }
}
