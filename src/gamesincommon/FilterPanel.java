package gamesincommon;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FilterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JCheckBox> filterCheckBoxes;

	public FilterPanel() {
		this.setLayout(new GridLayout(0, 1));
		this.add(new JLabel("Filters"));
		// creates checkbox list of all available filter types
		filterCheckBoxes = new ArrayList<JCheckBox>();
		for (FilterType filter : FilterType.values()) {
			JCheckBox checkBox = new JCheckBox(filter.getValue());
			this.add(checkBox);
			filterCheckBoxes.add(checkBox);
		}
	}

	public List<FilterType> getFilters() {
		ArrayList<FilterType> result = new ArrayList<FilterType>();
		for (JCheckBox cbox : filterCheckBoxes) {
			if (cbox.isSelected()) {
				result.add(FilterType.getEnum(cbox.getText()));
			}
		}
		return result;
	}

}
