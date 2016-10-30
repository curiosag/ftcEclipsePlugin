package org.cg.eclipse.ftcplugin.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import org.cg.common.check.Check;
import org.cg.common.core.AbstractLogger;
import org.cg.common.interfaces.OnTextFieldChangedEvent;
import org.cg.common.interfaces.OnValueChanged;
import org.cg.common.interfaces.Progress;
import org.cg.ftc.ftcClientJava.Const;
import org.cg.ftc.ftcClientJava.FrontEnd;
import org.cg.ftc.ftcClientJava.Observism;

public class FtcPluginFrontEnd implements FrontEnd {
	private ActionListener actionListener = null;
	private final AbstractLogger logger;

	public FtcPluginFrontEnd(AbstractLogger logger) {
		this.logger = logger;
	}

	public void translateCommand(String commandId) {
		Check.notNull(actionListener);
		actionListener.actionPerformed(new ActionEvent(this, 0, Const.listTables));
	}

	@Override
	public Progress getProgressMonitor() {
		return null;
	}

	@Override
	public void setActionListener(ActionListener l) {
		actionListener = null;
	}

	@Override
	public void addClientIdChangedListener(OnTextFieldChangedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClientSecretChangedListener(OnTextFieldChangedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResultTextChangedListener(DocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addQueryTextChangedListener(DocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addQueryCaretChangedListener(OnValueChanged<Integer> onChange) {
		// TODO Auto-generated method stub

	}

	@Override
	public Observer createClientIdObserver() {
		return unObserver;
	}

	@Override
	public Observer createClientSecretObserver() {
		return unObserver;
	}

	@Override
	public Observer createOpResultObserver() {
		return new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				logger.Info(Observism.decodeTextModelObservable(o));
			}
		};
	}

	@Override
	public Observer createQueryObserver() {
		return unObserver;
	}

	@Override
	public Observer createResultDataObserver() {
		return new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				TableModel t = Observism.decodeTableModelObservable(o);

				logger.Info(String.format("retrieved %d rows %d columns", t.getRowCount(), t.getColumnCount()));
			}
		};

	}

	private static Observer unObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
		}
	};

}
