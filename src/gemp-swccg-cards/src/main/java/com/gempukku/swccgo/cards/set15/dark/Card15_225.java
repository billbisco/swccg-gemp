package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set3.dark.Card3_128;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.cards.set5.dark.Card5_133;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_225 extends Card3_128 {
    public Card15_225() {
        setLore("");
        setGameText("USED: Lose 1 Force to activate 2 Force. LOST: Just after the weapons phase of a battle, use 2 Force to cause each player to target one of that player's characters involved in the battle. Target characters cannot apply their ability toward drawing battle destiny this turn. OR Lose 1 Force to activate 4 Force.");
        addComboCardTitles("Brief Loss Of Control", "Imperial Supply");
        addIcon(Icon.HOTH, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public String getTitle() { return "Brief Loss Of Control & Imperial Supply"; }

    @Override
    public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }

    @Override
    public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final BriefLossActions BRIEF_LOSS = new BriefLossActions();

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                       EffectResult result, PhysicalCard self) {
        List<PlayInterruptAction> actions = BRIEF_LOSS.actions(playerId, game, result, self);
        if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
        return actions;
    }

    // Expose the unchanged component's protected hook while retaining this combo as the action source.
    private static final class BriefLossActions extends Card5_133 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
            return super.getGameTextOptionalAfterActions(playerId, game, result, self);
        }
    }
}
