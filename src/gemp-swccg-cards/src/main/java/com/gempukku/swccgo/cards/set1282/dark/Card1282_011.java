package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Weapon
 * Subtype: Character
 * Title: Assassin's Blaster Rifle
 */
public class Card1282_011 extends AbstractCharacterWeapon {
    public Card1282_011() {
        super(Side.DARK, 3, "Assassin's Blaster Rifle", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setGameText("Deploy on your assassin; assassin is power +1. May target a character at same or adjacent site for free. Draw destiny. If destiny +1 > defense value: target hit, may not be captured this turn, and you may place a bounty on target in Used Pile to retrieve 2 Force.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.BLASTER_RIFLE);
        setMatchingCharacterFilter(Filters.assassin);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.assassin);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.assassin;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.hasAttached(self), Filters.assassin), 1));
        return modifiers;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, self)) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " not able to be captured");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotTargetToBeCapturedModifier(self, cardHit),
                            GameUtils.getCardLink(cardHit) + " may not be captured this turn"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, self)) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            final Filter bountyOnTarget = Filters.and(Filters.any_bounty, Filters.attachedTo(cardHit));
            if (GameConditions.canSpot(game, self, bountyOnTarget)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place bounty in Used Pile to retrieve 2 Force");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose bounty on " + GameUtils.getCardLink(cardHit), bountyOnTarget) {
                            @Override
                            protected void cardSelected(PhysicalCard bounty) {
                                action.addAnimationGroup(bounty);
                                // Pay cost(s)
                                action.appendCost(
                                        new PlaceCardInUsedPileFromTableEffect(action, bounty));
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 2));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
