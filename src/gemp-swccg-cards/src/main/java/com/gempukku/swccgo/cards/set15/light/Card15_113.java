package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set14.light.Card14_043;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_113 extends Card14_043 {
    public Card15_113() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(5);
        setLore("");
        setGameText("If you just lost a Gungan from table, place him in your hand. OR If Jar Jar and Tarpals are in a battle together, add two battle destiny. OR Relocate Jar Jar to a battle just initiated at an adjacent site. OR Increase Jar Jar's landspeed by 1 for remainder of turn. OR If Jar Jar on Brisky Morning Munchen, take him to hand.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("No Giben Up, General Jar Jar!", Uniqueness.UNIQUE), new TitleComponent("Gimme A Lift!", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "No Giben Up, General Jar Jar & Gimme A Lift!"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final LiftActions COMPONENT = new LiftActions();
    private static final class LiftActions extends com.gempukku.swccgo.cards.set14.light.Card14_042 {
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
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextOptionalAfterActions(player, game, result, self);
        if (first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.after(player, game, result, self);
        if (second != null) actions.addAll(second);
        return actions;
    }
}
