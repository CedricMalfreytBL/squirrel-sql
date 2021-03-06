package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ConfigureNonDbConstraintDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConfigureNonDbConstraintDlg.class);


   DataSetViewerTablePanel _table;
   JComboBox _cboPkCol;
   JButton _btnOk;
   JButton _btnCancel;
   JButton _btnAdd;
   JButton _btnRemove;
   JComboBox _cboFkCol;
   JTextField _txtContstrName;

   public ConfigureNonDbConstraintDlg(Window parent, String fkTableName, String pkTableName)
   {
      super(parent, s_stringMgr.getString("graph.ConfigureNonDbConstraintDlgConfigureNonDBConstraint"));
      setModal(true);
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      _table = new DataSetViewerTablePanel();
      _table.init(null);
      JPanel pnlBordered = new JPanel(new GridLayout(1,1));
      pnlBordered.add(new JScrollPane(_table.getComponent()));
      TitledBorder titledBorder = BorderFactory.createTitledBorder(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.tblTitle"));
      titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
      pnlBordered.setBorder(titledBorder);
      getContentPane().add(pnlBordered, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,10,5), 0,0);
      _btnRemove = new JButton(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.remove"));
      getContentPane().add(_btnRemove, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createControlsPanel(fkTableName, pkTableName), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createNamePanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      MultipleLineLabel lblHint = new MultipleLineLabel(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.NonDbConstraintHint"));
      lblHint.setForeground(Color.red);
      getContentPane().add(lblHint, gbc);

      gbc = new GridBagConstraints(0,6,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);


      setSize(800, 530);

      GUIUtils.centerWithinParent(this);


      getRootPane().setDefaultButton(_btnOk);

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);

   }

   private JPanel createNamePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.ContsrName")), gbc);


      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      _txtContstrName = new JTextField();
      ret.add(_txtContstrName, gbc);

      return ret;
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _btnOk = new JButton(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.OK"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _btnCancel = new JButton(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.Cancel"));
      ret.add(_btnCancel, gbc);
      return ret;
   }

   private JPanel createControlsPanel(String fkTableName, String pkTableName)
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      JLabel lblLocalCol = new JLabel(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.Localcolumn", fkTableName));
      ret.add(lblLocalCol, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      _cboFkCol = new JComboBox();
      ret.add(_cboFkCol, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      JLabel lblRefrencing = new JLabel(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.Referencing", pkTableName));
      ret.add(lblRefrencing, gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      _cboPkCol = new JComboBox();
      ret.add(_cboPkCol, gbc);

      Dimension preferredSize = lblLocalCol.getPreferredSize();
      preferredSize.width = Math.max(lblLocalCol.getPreferredSize().width, lblRefrencing.getPreferredSize().width);
      lblLocalCol.setPreferredSize(preferredSize);
      lblRefrencing.setPreferredSize(preferredSize);


      gbc = new GridBagConstraints(2,0,1,2,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      ret.add(createAddButtonsPanel(), gbc);


      TitledBorder titledBorder = BorderFactory.createTitledBorder(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.AddColsTitle"));
      titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
      ret.setBorder(titledBorder);

      return ret;
   }

   private JPanel createAddButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _btnAdd = new JButton(s_stringMgr.getString("graph.ConfigureNonDbConstraintDlg.add"));
      ret.add(_btnAdd, gbc);

      return ret;
   }
}
