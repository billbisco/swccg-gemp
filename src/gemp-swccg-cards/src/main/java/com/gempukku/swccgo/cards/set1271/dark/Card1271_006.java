package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Imperial
 * Title: Saber Squadron Pilot
 */
public class Card1271_006 extends AbstractImperial {
    public Card1271_006() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Saber Squadron Pilot", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("-");
        setGameText("Adds 2 to power of anything he pilots. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that drain).");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        setMatchingStarshipFilter(Filters.title("Saber Squadron TIE"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.matchingStarship(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SABER_SQUADRON_PILOT__RELOCATE;
        Filter matchingTie = Filters.and(Filters.matchingStarship(self), Filters.hasPiloting(self), Filters.at(Filters.system), Filters.hasNotPerformedRegularMove);

        Filter virtualBlock7CombatResponse = Filters.and(Filters.Combat_Response, Filters.icon(Icon.LEGACY_BLOCK_7));
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canSpot(game, self, virtualBlock7CombatResponse)
                && GameConditions.canSpot(game, self, matchingTie)) {
            PhysicalCard tie = Filters.findFirstActive(game, self, matchingTie);
            if (tie != null
                    && GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.withinParsecsOf(tie, 3), Filters.locationCanBeRelocatedTo(tie, 1)))) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate Saber Squadron TIE");
                Filter systemToRelocateTo = Filters.and(Filters.system, Filters.withinParsecsOf(tie, 3), Filters.locationCanBeRelocatedTo(tie, 1));
                final PhysicalCard tieToRelocate = tie;
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose system to relocate " + GameUtils.getCardLink(tieToRelocate) + " to", systemToRelocateTo) {
                            @Override
                            protected void cardSelected(final PhysicalCard systemSelected) {
                                action.addAnimationGroup(tieToRelocate);
                                action.addAnimationGroup(systemSelected);
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, tieToRelocate, systemSelected, 1));
                                action.allowResponses("Relocate " + GameUtils.getCardLink(tieToRelocate) + " to " + GameUtils.getCardLink(systemSelected),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, tieToRelocate, systemSelected, true));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SABER_SQUADRON_PILOT__ADD_TO_FORCE_DRAIN;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.here(self))
                && GameConditions.isPiloting(game, self, Filters.matchingStarship(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            action.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(action,
                            new LimitForceLossFromForceDrainModifier(self, Filters.here(self), 2, opponent),
                            "Opponent loses no more than 2 Force to that drain"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
