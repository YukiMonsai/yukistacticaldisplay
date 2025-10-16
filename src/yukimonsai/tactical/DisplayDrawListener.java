package yukimonsai.tactical;

import com.fs.starfarer.api.input.InputEventAPI;

public interface DisplayDrawListener {
    boolean draw(NA_CombatPlugin.InputType input, int side, boolean flip, boolean flipv, float XX, float YY, float textxoff, float textoff, float textheight, float titlexoff, float textSpacing, InputEventAPI e);
}
