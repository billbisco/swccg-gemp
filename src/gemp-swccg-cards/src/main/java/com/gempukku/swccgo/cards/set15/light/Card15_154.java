package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_076;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_154 extends Card6_076 {
    public Card15_154() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(5);
        setLore("");
        setGameText("USED: If Jabba is at a Jabba's Palace site, relocate one of your characters to that location from a related site. OR During your deploy phase, deploy one alien from Reserve Deck to Audience Chamber; reshuffle. LOST: Target an escort alone and its captive. Draw destiny. Escort lost if destiny + captive's power > escort's ability + power. OR During a battle at a site where Leia is an escorted captive, add one battle destiny.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("You Will Take Me To Jabba Now", Uniqueness.UNIQUE), new TitleComponent("Strangle", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "You Will Take Me To Jabba Now & Strangle"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final ComponentActions COMPONENT = new ComponentActions();
    private static final class ComponentActions extends com.gempukku.swccgo.cards.set6.light.Card6_080 {
        List<PlayInterruptAction> top(String player, SwccgGame game, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(player, game, self);
        if (first != null) first.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
        if (first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.top(player, game, self);
        if (second != null) actions.addAll(second);
        return actions;
    }
}
