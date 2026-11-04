package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireSingleWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardAndResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Bossk With Mortar Gun (V)
 */
public class Card1281_004 extends AbstractAlien {
    public Card1281_004() {
        super(Side.DARK, 3, 3, 4, 2, 4, "Bossk With Mortar Gun", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Trandoshan assassin and slaver.");
        setGameText("Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit.' Permanent weapon is •Bossk's Mortar Gun (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny = target's destiny number).");
        addPersona(Persona.BOSSK);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON);
        addKeywords(Keyword.ASSASSIN, Keyword.SLAVER);
        setSpecies(Species.TRANDOSHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final Filter otherSlaverPresentNotHit = Filters.and(Filters.your(self), Filters.slaver, Filters.other(self), Filters.presentWith(self), Filters.not(Filters.hit));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self,
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        return Filters.countActive(gameState.getGame(), self, otherSlaverPresentNotHit) > 0;
                    }
                }));
        return modifiers;
    }

    // Define "Bossk's Mortar Gun" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon(Persona.BOSSKS_MORTAR_GUN, Title.Bossks_Mortar_Gun) {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(final String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                final FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder == null) {
                    return null;
                }
                final SwccgBuiltInCardBlueprint permanentWeapon = this;
                final FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, permanentWeapon, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
                action.setText("Fire " + action.getWeaponTitle(game));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose target", actionBuilder.getTargetFiltersMap(action.getCardFiringWeapon())) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                game.getGameState().getWeaponFiringState().setTarget(cardTargeted);
                                float forceToUse = actionBuilder.getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                                if (forceToUse > 0) {
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, forceToUse));
                                }
                                action.allowResponses("Fire " + action.getWeaponTitle(game) + " at " + GameUtils.getCardLink(cardTargeted),
                                        new RespondableWeaponFiringEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                                    return;
                                                                }
                                                                float targetDestiny = game.getModifiersQuerying().getDestiny(gameState, cardFiredAt);
                                                                gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Target's destiny number: " + GuiUtils.formatAsString(targetDestiny));
                                                                if (totalDestiny == targetDestiny) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new HitCardAndResetForfeitEffect(action, cardFiredAt, 0, self, permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        };
    }
}
