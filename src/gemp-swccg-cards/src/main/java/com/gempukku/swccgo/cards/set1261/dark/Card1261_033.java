package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.List;

/** Legacy Block 6.1 Dark Waters & Krayt Dragon Bones. */
public class Card1261_033 extends AbstractNormalEffect {
    private static final Filter NON_SELECTIVE_FEROCIOUS_CREATURE = Filters.and(LegacyCreatureFilters.ferocious,
            Filters.not(Icon.SELECTIVE_CREATURE));

    public Card1261_033() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, "Dark Waters & Krayt Dragon Bones", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        addComboCardTitles(Title.Dark_Waters, Title.Krayt_Dragon_Bones);
        setGameText("Deploy on Rancor Pit. May deploy a non-[Selective] ferocious creature here. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. (Immune to Alter.)");
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
        return List.of(new MayDeployToLocationModifier(self, NON_SELECTIVE_FEROCIOUS_CREATURE, Filters.hasAttached(self)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var pit = self.getAttachedTo();
        if (pit == null || !GameConditions.isDuringYourPhase(game, self, Phase.BATTLE)
                || GameConditions.occupies(game, playerId, pit))
            return null;
        Filter captive = Filters.and(Filters.captive, Filters.at(Filters.Audience_Chamber),
                Filters.canBeRelocatedToLocation(Filters.sameCardId(pit), false, false, true, 0, false));
        if (!GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captive))
            return null;

        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Relocate captive from Audience Chamber");
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
        return List.of(action);
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
