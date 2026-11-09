package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyCreatureFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.HabitatIncludesLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Block 6.1 Dark Waters & Krayt Dragon Bones. Text: PC-E2, the PC website card list (by 2012-07-21), which
 * moved the creature sentence to the front (a reorder, so a version: "a change is a change"); the list dropped its
 * ▼ ("May deploy"), restored here as on the slips, so the creature still deploys from Reserve Deck. The 2010 text
 * (PC-E1, reprinted by the 2013 Master List) is 2261_1033 (legacy batch B16).
 */
public class Card1261_033 extends AbstractNormalEffect {
    private static final Filter NON_SELECTIVE_FEROCIOUS_CREATURE = Filters.and(LegacyCreatureFilters.ferocious,
            Filters.not(Icon.SELECTIVE_CREATURE));

    public Card1261_033() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, "Dark Waters & Krayt Dragon Bones", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        addComboCardTitles(Title.Dark_Waters, Title.Krayt_Dragon_Bones);
        setGameText("Deploy on Rancor Pit. May ▼ a non-[Selective] ferocious creature here. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.Rancor_Pit;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        // "May [download] a non-[Selective] ferocious creature here": here is part of the habitat of such a creature in
        // its owner's Reserve Deck (so the download may target here) and of one on table (so the habitat rule does not
        // lose it once here). A creature in hand gets no habitat change, so this clause gives no deploy from hand.
        return List.of(new HabitatIncludesLocationModifier(self, Filters.and(NON_SELECTIVE_FEROCIOUS_CREATURE,
                Filters.or(Filters.onTable, Filters.zoneOfPlayer(Zone.RESERVE_DECK, self.getOwner()))),
                Filters.hasAttached(self)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var pit = self.getAttachedTo();
        if (pit == null)
            return null;
        List<TopLevelGameTextAction> actions = new ArrayList<>();

        var relocateId = GameTextActionId.LEGACY__DARK_WATERS_AND_KRAYT_DRAGON_BONES__RELOCATE_CAPTIVE;
        Filter captive = Filters.and(Filters.captive, Filters.at(Filters.Audience_Chamber),
                Filters.canBeRelocatedToLocation(Filters.sameCardId(pit), false, false, true, 0, false));
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, relocateId, Phase.BATTLE)
                && !GameConditions.occupies(game, playerId, pit)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captive)) {
            var action = new TopLevelGameTextAction(self, playerId, sourceId, relocateId);
            action.setText("Relocate captive from Audience Chamber");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose captive",
                    SpotOverride.INCLUDE_CAPTIVE, captive) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                    action.addAnimationGroup(target);
                    action.allowResponses("Relocate " + GameUtils.getCardLink(target) + " to " + GameUtils.getCardLink(pit),
                            new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    action.appendEffect(new RelocateBetweenLocationsEffect(action,
                                            action.getPrimaryTargetCard(targetGroupId), pit));
                                }
                            });
                }
            });
            actions.add(action);
        }

        var deployId = GameTextActionId.LEGACY__DARK_WATERS_AND_KRAYT_DRAGON_BONES__DEPLOY_CREATURE;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            var action = new TopLevelGameTextAction(self, playerId, sourceId, deployId);
            action.setText("Deploy creature from Reserve Deck");
            action.setActionMsg("Deploy a non-[Selective] ferocious creature to " + GameUtils.getCardLink(pit) + " from Reserve Deck");
            action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action, NON_SELECTIVE_FEROCIOUS_CREATURE,
                    Filters.sameCardId(pit), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var pit = self.getAttachedTo();
        if (pit == null
                || !TriggerConditions.isAboutToDrawFerocityDestiny(game, result, Filters.and(Filters.creature, Filters.at(pit)))
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId)
                || !GameConditions.canSubstituteDestiny(game))
            return null;
        var creature = ((com.gempukku.swccgo.logic.effects.DrawFerocityDestinyEffect.FerocityDestinyDrawEffect)
                ((com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult) result).getDrawDestinyEffect()).getCreature();
        float destinyNumber = game.getModifiersQuerying().getDestiny(game.getGameState(), creature);
        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Use " + GameUtils.getFullName(creature) + "'s destiny number (" + GuiUtils.formatAsString(destinyNumber) + ") for ferocity");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new SubstituteDestinyEffect(action, destinyNumber));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var pit = self.getAttachedTo();
        if (pit == null)
            return null;
        if (TriggerConditions.justEatenAt(game, result, Filters.and(Filters.opponents(self), Filters.character), pit)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Retrieve 3 Force");
            action.appendEffect(new RetrieveForceEffect(action, self.getOwner(), 3));
            return List.of(action);
        }
        if (TriggerConditions.justLost(game, result, Filters.creature)) {
            var loss = (LostFromTableResult) result;
            if (loss.getFromLocation() != pit || loss.getCard().getBlueprint().getDeployCost() == null)
                return null;
            var creature = loss.getCard();
            int amount = (int) Math.ceil(creature.getBlueprint().getDeployCost() / 2f);
            if (amount <= 0)
                return null;
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Creature owner loses " + amount + " Force");
            action.appendEffect(new LoseForceEffect(action, creature.getOwner(), amount));
            return List.of(action);
        }
        return null;
    }
}
