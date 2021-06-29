package ru.kids.copier.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class ProgressDialog extends JDialog implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051957748575317184L;

	private JLabel firstLabel;
	private JProgressBar firstBar;
	private JLabel secondLabel;
	private JProgressBar secondBar;

	private SwingWorker<?, ?> worker;

	public ProgressDialog(JDialog parent, String title) {
		super(parent, title, false);
		setSize(400, 150);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 166, 0 };
		gridBagLayout.rowHeights = new int[] { 47, 47, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel firstPanel = new JPanel();
		GridBagConstraints gbc_firstPanel = new GridBagConstraints();
		gbc_firstPanel.weightx = 1.0;
		gbc_firstPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstPanel.anchor = GridBagConstraints.NORTH;
		gbc_firstPanel.insets = new Insets(0, 0, 5, 0);
		gbc_firstPanel.gridx = 0;
		gbc_firstPanel.gridy = 0;
		getContentPane().add(firstPanel, gbc_firstPanel);
		firstPanel.setLayout(new BorderLayout(0, 0));
		firstLabel = new JLabel();
		firstPanel.add(firstLabel, BorderLayout.NORTH);
		firstBar = new JProgressBar(SwingConstants.HORIZONTAL);
		firstPanel.add(firstBar, BorderLayout.SOUTH);
		firstBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		firstBar.setStringPainted(true);

		JPanel secondPanel = new JPanel();
		GridBagConstraints gbc_secondPanel = new GridBagConstraints();
		gbc_secondPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_secondPanel.weightx = 1.0;
		gbc_secondPanel.weighty = 1.0;
		gbc_secondPanel.anchor = GridBagConstraints.CENTER;
		gbc_secondPanel.gridx = 0;
		gbc_secondPanel.gridy = 1;
		getContentPane().add(secondPanel, gbc_secondPanel);
		secondPanel.setLayout(new BorderLayout(0, 0));

		secondLabel = new JLabel();
		secondPanel.add(secondLabel, BorderLayout.NORTH);

		secondBar = new JProgressBar(SwingConstants.HORIZONTAL);
		secondBar.setStringPainted(true);
		secondBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		secondPanel.add(secondBar, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setCursor(Cursor.getDefaultCursor());
				worker.cancel(true);
				worker = null;
			}
		});
	}

	public void setFirstLabelText(String string) {
		this.firstLabel.setText(string);
	}

	public void setSecondLabelText(String string) {
		this.secondLabel.setText(string);
	}

	public void setFirstBarSize(int maxSize) {
		firstBar.setValue(0);
		firstBar.setMaximum(maxSize);
	}

	public void setSecondBarSize(int maxSize) {
		secondBar.setValue(0);
		secondBar.setMaximum(maxSize);
	}

	public void setFirstBarIncValue() {
		firstBar.setValue(firstBar.getValue() + 1);
	}

	public void setSecondBarIncValue() {
		secondBar.setValue(secondBar.getValue() + 1);
	}

	public void runWork(final SwingWorker<?, ?> worker) {
		this.worker = worker;
		worker.addPropertyChangeListener(this);
		setVisible(true);
		worker.run();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("state".equals(evt.getPropertyName()))
			if (SwingWorker.StateValue.STARTED.equals(evt.getNewValue())
					|| SwingWorker.StateValue.PENDING.equals(evt.getNewValue())) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			} else if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
				setCursor(Cursor.getDefaultCursor());
				worker.removePropertyChangeListener(this);
				worker.cancel(false);
				setVisible(false);
				dispose();
			}
	}
	
	public boolean isCanseled() {
		return worker.isCancelled();
	}

}
