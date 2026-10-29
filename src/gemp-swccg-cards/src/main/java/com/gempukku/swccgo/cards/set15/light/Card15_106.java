package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_097;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;



import java.util.ArrayList;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_106 extends Card1_097 {
    public Card15_106() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(5);
        setLore("");
        setGameText("If Elis Helrot just completed a transport, use 2 Force. Elis and all characters transported are lost. (Immune to Sense.) OR At any time (except during battle), target any or all of your characters at one site to 'transport' (relocate) to any one other site. Draw destiny. Use that much Force to 'transport' or place Interrupt in Lost Pile. OR Prevent a smuggler from moving this turn.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Nabrun Leids", Uniqueness.UNRESTRICTED), new TitleComponent("Quite A Mercenary", Uniqueness.UNRESTRICTED));
    }
    @Override public String getTitle() { return "Nabrun Leids & Quite A Mercenary"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final MercenaryActions MERCENARY = new MercenaryActions();
    private static final class MercenaryActions extends com.gempukku.swccgo.cards.set2.light.Card2_055 {
        List<PlayInterruptAction> transportActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            if (!com.gempukku.swccgo.logic.TriggerConditions.transportCompletedBy(game, result, com.gempukku.swccgo.filters.Filters.Elis_Helrot))
                return null;
            List<PlayInterruptAction> actions = super.getGameTextOptionalAfterActions(player, game, result, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return MERCENARY.transportActions(player, game, result, self);
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> transport = super.getGameTextTopLevelActions(playerId, game, self);
        if (transport != null) actions.addAll(transport);
        if (GameConditions.canTarget(game, self, Filters.smuggler)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Prevent a smuggler from moving");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose smuggler", Filters.smuggler) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard smuggler) {
                            action.addAnimationGroup(smuggler);
                            // Allow response(s)
                            action.allowResponses("Prevent " + GameUtils.getCardLink(smuggler) + " from moving",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSmuggler = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MayNotMoveUntilEndOfTurnEffect(action, finalSmuggler));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
