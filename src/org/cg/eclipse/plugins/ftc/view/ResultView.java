package org.cg.eclipse.plugins.ftc.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import javax.swing.table.TableModel;

import org.cg.eclipse.plugins.ftc.PluginConst;
import org.eclipse.swt.SWT;

public class ResultView extends ViewPart {

	public static final String ID = PluginConst.RESULT_VIEW_ID;
	private Table table;

	public ResultView() {
	}

	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);

	}

	public void displayTable(TableModel model) {
		
		table.setRedraw(false);
		table.removeAll();
		while ( table.getColumnCount() > 0 ) {
		    table.getColumns()[ 0 ].dispose();
		}
		table.setRedraw(true);
		
		int colCount = model.getColumnCount();
		
		for (int i = 0; i < colCount; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(model.getColumnName(i));
		}

		for (int i = 0; i < model.getRowCount(); i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < colCount; j++)
				item.setText(j, model.getValueAt(i, j).toString());
		}

		for (int i = 0; i < colCount; i++)
			table.getColumn(i).pack();
	}

	@Override
	public void setFocus() {

	}

}
