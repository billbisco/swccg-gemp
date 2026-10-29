package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set4.light.Card4_068;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_111 extends Card4_068 {
    public Card15_111() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("USED: Send one of your missing characters at same site as one of your piloted or driven vehicles to your Used Pile. OR Cancel any attempt by one character to 'hide' from a battle. OR Name an Interrupt card. Opponent must reveal entire Used Pile, without shuffling, Then you choose: USED: Opponent must lose 1 Force for each copy of that Interrupt found there. LOST: Each copy of that Interrupt found there is lost.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Nice Of You Guys To Drop By", Uniqueness.UNRESTRICTED), new TitleComponent("Visored Vision", Uniqueness.RESTRICTED_2));
    }
    @Override public String getTitle() { return "Nice Of You Guys To Drop By & Visored Vision"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.RESTRICTED_2; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final NiceActions COMPONENT = new NiceActions();
    private static final class NiceActions extends com.gempukku.swccgo.cards.set3.light.Card3_046 {
        List<PlayInterruptAction> top(String player, SwccgGame game, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            return actions;
        }
        List<PlayInterruptAction> after(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextOptionalAfterActions(player, game, result, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
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
