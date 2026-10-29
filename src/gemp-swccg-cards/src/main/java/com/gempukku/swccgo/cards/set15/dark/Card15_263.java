package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.set14.dark.Card14_104;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Card15_263 extends Card14_104 {
    public Card15_263() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: Target a character at same site as two of your destroyer droids. For remainder of turn, target may not fire weapons and is power -2. OR Take any non-unique destroyer droid into hand from Reserve Deck; reshuffle. OR During your deploy phase, relocate your destroyer droid to an adjacent site. LOST: Lose 1 Force to take up to three non-unique destroyer droids into hand from Reserve Deck; reshuffle.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addComboCardTitles("Master, Destroyers!", "Rolling, Rolling, Rolling");
    }
    @Override public String getTitle() { return "Master, Destroyers! & Rolling, Rolling, Rolling"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ROLLING_ROLLING_ROLLING__UPLOAD_NON_UNIQUE_DESTROYER_DROID;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a non-unique destroyer droid into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.destroyer_droid), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            Collection<PhysicalCard> destroyerDroids = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.destroyer_droid, Filters.at(Filters.site), Filters.canBeTargetedBy(self)));
            if (!destroyerDroids.isEmpty()) {
                // Figure out which destroyer droids can be relocated to an adjacent site
                List<PhysicalCard> validDestroyerDroids = new LinkedList<PhysicalCard>();
                for (PhysicalCard destroyerDroid : destroyerDroids) {
                    if (Filters.canBeRelocatedToLocation(Filters.adjacentSite(destroyerDroid), 0).accepts(game, destroyerDroid)) {
                        validDestroyerDroids.add(destroyerDroid);
                    }
                }
                if (!validDestroyerDroids.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                    action.setText("Relocate destroyer droid to adjacent site");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose destroyer droid", Filters.in(validDestroyerDroids)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard destroyerDroidToRelocate) {
                                    Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game,
                                            Filters.and(Filters.adjacentSite(destroyerDroidToRelocate), Filters.locationCanBeRelocatedTo(destroyerDroidToRelocate, 0)));
                                    action.appendTargeting(
                                            new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(destroyerDroidToRelocate) + " to", Filters.in(otherSites)) {
                                                @Override
                                                protected void cardSelected(final PhysicalCard siteSelected) {
                                                    action.addAnimationGroup(destroyerDroidToRelocate);
                                                    action.addAnimationGroup(siteSelected);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, destroyerDroidToRelocate, siteSelected, 0));
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(destroyerDroidToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacter, siteSelected));
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
