package util;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.gui2.ActionListBox;

/**
 * ===============================
 * SafeActionListBox
 * ===============================
 * <p>
 * Purpose:
 * Custom ActionListBox that safely handles up/down key strokes.
 * Prevents the user from moving above the first item or below the last item,
 * avoiding the default Lanterna behavior that makes the selection "disappear"
 * when pressing arrow keys at the boundaries.
 * <p>
 * Usage:
 * Replace `new ActionListBox(...)` with `new SafeActionListBox(...)` in any menu
 * that uses arrow navigation for a more user-friendly experience.
 */

public class SafeActionListBox extends ActionListBox {

    public SafeActionListBox(TerminalSize size) {
        super(size);
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        int index = getSelectedIndex();
        int max = getItemCount() - 1;

        if (keyStroke.getKeyType() == KeyType.ArrowUp && index == 0) {
            return Result.HANDLED; // eat the key
        }
        if (keyStroke.getKeyType() == KeyType.ArrowDown && index == max) {
            return Result.HANDLED; // eat the key
        }

        return super.handleKeyStroke(keyStroke);
    }
}
