package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set2.dark.Card2_134;
import com.gempukku.swccgo.cards.set1.dark.Card1_269;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_255 extends Card2_134 {
    private static final TakeelActions TAKEEL = new TakeelActions();
    public Card15_255() {
        setLore("");
        setGameText("USED: If a battle was just initiated at same site as your Undercover spy, your characters at adjacent sites may move there as a 'react' (for free). OR Cancel Sabotage. LOST: If both players just drew one battle destiny, use 1 Force to switch numbers.");
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(5);
        addComboCardTitles("Informant", "Takeel");
        addIcon(Icon.A_NEW_HOPE, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Informant & Takeel"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> used(List<PlayInterruptAction> actions) {
        if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        return used(super.getGameTextTopLevelActions(playerId, game, self));
    }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return used(super.getGameTextOptionalBeforeActions(playerId, game, effect, self));
    }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> react = used(super.getGameTextOptionalAfterActions(playerId, game, result, self));
        if (react != null) actions.addAll(react);
        List<PlayInterruptAction> swap = TAKEEL.actions(playerId, game, result, self);
        if (swap != null) {
            swap.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            actions.addAll(swap);
        }
        return actions;
    }
    private static final class TakeelActions extends Card1_269 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
            return super.getGameTextOptionalAfterActions(playerId, game, result, self);
        }
    }
}
