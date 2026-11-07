package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Virtual Block 7.1: two destiny versus defense; the original weapon's weapon-loss clause does not apply. */
public class Card1271_048 extends AbstractArtilleryWeapon {
    public Card1271_048() {
        super(Side.LIGHT, 4, 4, 4, Title.Planet_Defender_Ion_Cannon, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("KDY v-150 surface-to-space heavy ion cannon. The most powerful ground-based weapon in the Rebel arsenal. Reserved for strategic installations due to limited supply.");
        setGameText("Deploy on an exterior site. May target a capital starship (except Executor) at related system for free. Draw two destiny. If total destiny > defense value, target is power and hyperspeed = 0 (if target forfeited this battle, may place this weapon out of play to steal it into your hand).");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.ION_CANNON);
    }
    @Override public boolean isFiredByCharacterPresentOrHere() { return false; }
    @Override protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.exterior_site;
    }
    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
            boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
            Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree,
            extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
            .targetAtRelatedSystemUsingForce(Filters.and(Filters.capital_starship, Filters.not(Filters.Executor)), 0, TargetingReason.OTHER).finishBuildPrep();
        if (builder == null) return null;
        FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, null, repeatedFiring,
            targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(game));
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose capital starship", builder.getTargetFiltersMap(action.getCardFiringWeapon())) {
            @Override protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                action.addAnimationGroup(target);
                game.getGameState().getWeaponFiringState().setTarget(target);
                float cost = builder.getUseForceCost(action.getCardFiringWeapon(), target);
                if (cost > 0) action.appendCost(new UseForceEffect(action, playerId, cost));
                action.allowResponses(new RespondableWeaponFiringEffect(action) {
                    @Override protected void performActionResults(Action targetingAction) {
                        PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                        game.getGameState().getWeaponFiringState().setTarget(finalTarget);
                        action.appendEffect(new DrawDestinyEffect(action, playerId, 2, DestinyType.WEAPON_DESTINY) {
                            @Override protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                                if (total == null || total <= game.getModifiersQuerying().getDefenseValue(game.getGameState(), finalTarget)) return;
                                action.appendEffect(new IonizeStarshipEffect(action, finalTarget, self, true, false, true));
                                if (GameConditions.isDuringBattle(game)) scheduleSteal(action, self, finalTarget, playerId);
                            }
                        });
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }
    private void scheduleSteal(Action firingAction, PhysicalCard weapon, PhysicalCard target, String owner) {
        final int weaponId = weapon.getPermanentCardId();
        final int targetId = target.getPermanentCardId();
        firingAction.appendEffect(new AddUntilEndOfBattleActionProxyEffect(firingAction, new AbstractActionProxy() {
            @Override public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result) {
                PhysicalCard currentWeapon = game.findCardByPermanentId(weaponId);
                PhysicalCard currentTarget = game.findCardByPermanentId(targetId);
                if (!owner.equals(playerId) || currentWeapon == null || currentTarget == null || !currentWeapon.getZone().isInPlay()
                        || !GameConditions.canBePlacedOutOfPlay(game, currentWeapon)
                        || !TriggerConditions.justForfeitedToLostPileFromLocation(game, result, Filters.sameCardId(currentTarget), Filters.battleLocation)) return null;
                OptionalGameTextTriggerAction steal = new OptionalGameTextTriggerAction(currentWeapon, playerId, currentWeapon.getCardId());
                steal.setText("Steal forfeited capital starship into hand");
                steal.appendCost(new PlaceCardOutOfPlayFromTableEffect(steal, currentWeapon));
                steal.appendEffect(new StealCardIntoHandFromLostPileEffect(steal, playerId, Filters.sameCardId(currentTarget)));
                return Collections.singletonList(steal);
            }
        }));
    }
}
