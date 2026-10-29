package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_037;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_099 extends Card11_037 {
    public Card15_099() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: During battle, place top card of opponent's Lost Pile on top of their Reserve Deck. LOST: Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target is 'damaged.' OR Place the top race destiny of all Podracers in owner's Used Pile. OR Place 3 cards from hand (random choice) into your Lost Pile to take any one character from your Lost Pile into hand.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("Losing Track", Uniqueness.UNIQUE), new TitleComponent("A Step Backward", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Losing Track & A Step Backwards"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final StepBackwardActions STEP_BACKWARD = new StepBackwardActions();
    private static final class StepBackwardActions extends com.gempukku.swccgo.cards.set11.light.Card11_030 {
        List<PlayInterruptAction> actions(String player, SwccgGame game, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
            actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>(super.getGameTextTopLevelActions(player, game, self));
        actions.addAll(STEP_BACKWARD.actions(player, game, self));
        return actions;
    }
}
