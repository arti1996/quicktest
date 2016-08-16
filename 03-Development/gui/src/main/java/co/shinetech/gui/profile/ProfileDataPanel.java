package co.shinetech.gui.profile;

import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.shinetech.dao.db.PersistenceException;
import co.shinetech.dto.Profile;
import co.shinetech.gui.GUIUtils;
import co.shinetech.gui.QTestMainWindow;
import co.shinetech.gui.table.DynamicTableModel;
import co.shinetech.gui.table.GridDataPanel;
import co.shinetech.service.ServiceFactory;
import co.shinetech.service.impl.ProfileService;

/**
 * 
 * @author Robin
 * @date 03/08/2016
 */
@SuppressWarnings("serial")
public class ProfileDataPanel extends GridDataPanel {
	private JPanel mySelf;

	public ProfileDataPanel(DynamicTableModel tm) {
		super(tm);
		mySelf = this;
		loadData();
	}

	public void loadData() {
		ProfileService ps = ServiceFactory.getService(ProfileService.class);
		new Thread(() -> {
			try {
				QTestMainWindow.processStart();
				Thread.sleep(1000L);
				tableModel.setData(ps.retrieveAll());
				tableModel.fireTableDataChanged();
				table.repaint();
				QTestMainWindow.processEnd();
			} catch (PersistenceException e) {
				JOptionPane.showMessageDialog(mySelf, "Error loading data from database.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public ActionListener getCreateListener() {
		return e -> {
			JFrame f = (JFrame) SwingUtilities.getWindowAncestor(mySelf);
			JDialog d = new JDialog(f,"Inclus�o do perfil");
			d.setModal(true);
			d.add(new ProfileFormPanel(d));
			d.pack(); // redimension the JDialog to the JPanel size
			d.setResizable(false);
			GUIUtils.centerOnParent(d, true);
			d.setVisible(true);
			loadData();
		};
	}

	@Override
	public ActionListener getRetrieveListener() {
		return e -> {
			JFrame f = (JFrame) SwingUtilities.getWindowAncestor(mySelf);
			JDialog d = new JDialog(f,"Pesquisar perfil");

			d.setModal(true);
			d.setResizable(false);
			d.add(new ProfileFormPanel(d));
			d.pack(); // redimension the JDialog to the JPanel size
			GUIUtils.centerOnParent(d, true);
			d.setVisible(true);
			//loadData();
		};
	}

	@Override
	public ActionListener getUpdateListener() {
		return e -> {
			JFrame f = (JFrame) SwingUtilities.getWindowAncestor(mySelf);
			JDialog d = new JDialog(f,"Atualiza��o do perfil");
			ProfileFormPanel pfp;

			d.setModal(true);
			d.setResizable(false);
			d.add(pfp = new ProfileFormPanel(d));
			d.pack(); // redimension the JDialog to the JPanel size

			if (table.getSelectedRow() < 0) {
				JOptionPane.showMessageDialog(mySelf, "Seleciona um perfil");
				return;
			}

			Profile p = (Profile) tableModel.getData().get(table.getSelectedRow());

			pfp.setDomainModel(p);
			GUIUtils.centerOnParent(d, true);
			d.setVisible(true);
			loadData();
		};
	}

	@Override
	public ActionListener getDeleteListener() {
		return e -> {
			if (table.getSelectedRow() < 0) {
				JOptionPane.showMessageDialog(mySelf, "Seleciona um perfil");
				return;
			}
			Profile p = (Profile) tableModel.getData().get(table.getSelectedRow());
			ProfileService ps = ServiceFactory.getService(ProfileService.class);
			try {
				int i = JOptionPane.showConfirmDialog(mySelf, "Quer mesmo apagar o Perfil selecionado?", "Confirma��o", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					ps.delete((int)p.getPk());
					loadData();
				}
			}catch (PersistenceException e1) {
				JOptionPane.showMessageDialog(mySelf, "N�o foi poss�vel apagar o Perfil");
				e1.printStackTrace();
			}
		};
	}

}
