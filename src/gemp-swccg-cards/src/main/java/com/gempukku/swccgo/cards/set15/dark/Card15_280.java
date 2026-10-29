package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set4.dark.Card4_153;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Effect;
import java.util.LinkedList;
import java.util.List;

public class Card15_280 extends Card4_153 {
    public Card15_280() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("USED: Cancel Death Star Plans, The Professor, Bacta Tank, How Did We Get Into This Mess?, Shoo! Shoo! or Thank The Maker. OR Name an Interrupt card. Opponent must reveal entire Used Pile, without shuffling. Then you choose: USED: Opponent must lose 1 Force for each copy of that interrupt found there. LOST: Each copy of that Interrupt found there is lost.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Shut Him Up Or Shut Him Down", "Voyeur");
    }
    @Override public String getTitle() { return "Shut Him Up Or Shut Him Down & Voyeur"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Death_Star_Plans, Filters.The_Professor,
                Filters.Bacta_Tank, Filters.How_Did_We_Get_Into_This_Mess, Filters.Shoo_Shoo, Filters.Thank_The_Maker))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Death_Star_Plans)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Death_Star_Plans, Title.Death_Star_Plans);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.The_Professor)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.The_Professor, Title.The_Professor);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Bacta_Tank)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Bacta_Tank, Title.Bacta_Tank);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.How_Did_We_Get_Into_This_Mess)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.How_Did_We_Get_Into_This_Mess, Title.How_Did_We_Get_Into_This_Mess);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Shoo_Shoo)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Shoo_Shoo, Title.Shoo_Shoo);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Thank_The_Maker)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Thank_The_Maker, Title.Thank_The_Maker);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
