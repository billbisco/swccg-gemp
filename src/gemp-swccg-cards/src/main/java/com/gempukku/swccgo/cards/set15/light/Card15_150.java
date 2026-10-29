package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set8.light.Card8_064;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_150 extends Card8_064 {
    public Card15_150() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: Take one or two communications or protocol droids (or one of each) into hand from Reserve Deck; reshuffle. LOST: Retrieve C-3PO. OR If C-3PO is in battle at a site, add one battle destiny (or two if with three Ewoks). OR If Leia and Han are in a battle together, add two battle destiny. OR If opponent just initiated a battle against Han, you may 'react' by deploying there (for free) Leia and/or her matching weapon from hand and/or Reserve Deck; reshuffle.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Were You Looking For Me?", Uniqueness.UNIQUE), new TitleComponent("I Know", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Were You Looking For Me? & I Know"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final ComponentActions COMPONENT = new ComponentActions();
    private static final class ComponentActions extends com.gempukku.swccgo.cards.set8.light.Card8_056 {
        List<PlayInterruptAction> top(String player, SwccgGame game, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            return actions;
        }
        List<PlayInterruptAction> after(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextOptionalAfterActions(player, game, result, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(player, game, self);
        if (first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.top(player, game, self);
        if (second != null) actions.addAll(second);
        return actions;
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return COMPONENT.after(player, game, result, self);
    }
}
