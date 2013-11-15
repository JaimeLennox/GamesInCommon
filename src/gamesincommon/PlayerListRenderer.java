package gamesincommon;

import java.awt.Color;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class PlayerListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof SteamId) {
            SteamId id = (SteamId) value;
            setText(id.getNickname());

            if (id.isOnline()) {
                setForeground(Color.BLUE);
            }
            if (id.isInGame()) {
                setForeground(Color.GREEN);
            }

            try {
                ImageIcon icon = new ImageIcon(new URL(id.getAvatarIconUrl()));
                setIcon(icon);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

}