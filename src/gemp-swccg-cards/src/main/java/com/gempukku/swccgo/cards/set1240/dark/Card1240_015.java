package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SeparatelyOrCombinedFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.IonizeStarshipEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotGoMissingModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Alien
 * Title: Scout Mercenary
 */
public class Card1240_015 extends AbstractAlien {
    public Card1240_015() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Scout Mercenary", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("Opportunists work with corrupt governments and Raithal casino operators. Keep the Empire informed of local Alliance activity, for a price.");
        setGameText("Adds 2 to power of anything she pilots, (3 if piloting a speeder bike). During battle, unless Gall on table, may use 1 Force to fire a DH-17 blaster twice (for free), separately or combined. While piloting a vehicle, she may use its defense value and may not go missing (except from your card).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final Condition pilotingVehicle = new PilotingCondition(self, Filters.vehicle);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.speeder_bike));
        // "May use its defense value": her defense value becomes the higher of her own (ability) and the vehicle's.
        modifiers.add(new DefenseValueModifier(self, self, pilotingVehicle, new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard vehicle = Filters.findFirstActive(gameState.getGame(), self, Filters.and(Filters.vehicle, Filters.hasPiloting(self)));
                if (vehicle == null) {
                    return 0;
                }
                float vehicleDefense = modifiersQuerying.getDefenseValue(gameState, vehicle);
                float ownDefense = modifiersQuerying.getAbility(gameState, self);
                return Math.max(0, vehicleDefense - ownDefense);
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        }));
        modifiers.add(new MayNotGoMissingModifier(self, self, pilotingVehicle));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter weaponFilter = Filters.and(
                Filters.DH17_blaster,
                Filters.attachedTo(self),
                Filters.canBeFired(self, 0, Filters.canBeTargetedBy(self)));

        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && !GameConditions.canSpot(game, self, Filters.title("Gall"))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSpot(game, self, weaponFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Fire a DH-17 blaster twice");
            action.setActionMsg("use " + GameUtils.getCardLink(self) + " to fire a DH-17 blaster twice");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose DH-17 blaster to fire twice", weaponFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }

                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.appendTargeting(
                                    new FireTwiceModeEffect(action, playerId) {
                                        @Override
                                        protected void modeChosen(String result) {
                                            if (result != null && result.startsWith("Don't Fire")) {
                                                action.setActionMsg(null);
                                                cancel();
                                                return;
                                            }
                                            final boolean combined = "Combined".equals(result);
                                            game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
                                            action.appendEffect(
                                                    new UseForceEffect(action, playerId, 1));
                                            game.getGameState().beginSeparatelyOrCombinedFiring(self, weapon, combined);
                                            action.appendAfterEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            SeparatelyOrCombinedFiringState soc = game.getGameState().getSeparatelyOrCombinedFiringState();
                                                            if (soc != null && soc.getCurrentFiringNumber() > 0
                                                                    && !game.getGameState().isDuringWeaponFiring()) {
                                                                game.getGameState().finishSeparatelyOrCombinedFiring();
                                                            }
                                                        }
                                                    }
                                            );
                                            final String mode = result.toLowerCase();
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            game.getGameState().sendMessage(playerId + " uses " + GameUtils.getCardLink(self)
                                                                    + " to fire " + GameUtils.getCardLink(weapon) + " twice (for free): " + mode);
                                                        }
                                                    }
                                            );
                                            appendFireTwice(action, game, self, weapon, combined);
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * Fires the chosen DH-17 blaster twice (for free) as sub-actions of this action, separately or combined
     * (same flow as Targeting Computer).
     */
    private void appendFireTwice(final TopLevelGameTextAction action, final SwccgGame game, final PhysicalCard self,
                                 final PhysicalCard weapon, final boolean combined) {
        action.appendEffect(createFireTwiceShotEffect(action, weapon, Filters.any));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter secondTargetFilter = Filters.any;
                        SeparatelyOrCombinedFiringState soc = game.getGameState().getSeparatelyOrCombinedFiringState();
                        if (combined && soc != null && soc.getCombinedTarget() != null) {
                            secondTargetFilter = Filters.sameCardId(soc.getCombinedTarget());
                        }
                        if (!canInitiateShot(game, self, weapon, secondTargetFilter)) {
                            game.getGameState().sendMessage("Second firing does not initiate. Completed firings still count.");
                            resolveCombinedIfStillPending(action, game, weapon);
                            game.getGameState().finishSeparatelyOrCombinedFiring();
                            return;
                        }
                        action.appendEffect(createFireTwiceShotEffect(action, weapon, secondTargetFilter));
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        resolveCombinedIfStillPending(action, game, weapon);
                                        game.getGameState().finishSeparatelyOrCombinedFiring();
                                    }
                                }
                        );
                    }
                }
        );
    }

    private boolean canInitiateShot(SwccgGame game, PhysicalCard self, PhysicalCard weapon, Filter fireAtTargetFilter) {
        FireWeaponAction fireWeaponAction = weapon.getBlueprint().getFireWeaponAction(
                weapon.getOwner(), game, weapon, true, 0, self, false,
                Filters.none, null, fireAtTargetFilter, true);
        return fireWeaponAction != null;
    }

    private FireWeaponEffect createFireTwiceShotEffect(Action action, PhysicalCard weapon, Filter fireAtTargetFilter) {
        return new FireWeaponEffect(action, weapon, true, fireAtTargetFilter) {
            @Override
            protected boolean isignorePerAttackOrBattleLimit() {
                return true;
            }
        };
    }

    private void resolveCombinedIfStillPending(Action action, SwccgGame game, PhysicalCard weapon) {
        SeparatelyOrCombinedFiringState soc = game.getGameState().getSeparatelyOrCombinedFiringState();
        if (soc == null || !soc.isCombined() || soc.isResolved() || soc.getCompletedFiringCount() == 0) {
            return;
        }
        PhysicalCard target = soc.getCombinedTarget();
        if (target == null) {
            return;
        }
        float combined = soc.getCombinedFiringDestinySum();
        float totalDestiny = game.getModifiersQuerying().getTotalWeaponDestiny(
                game.getGameState(), soc.getCardFiringWeapon(), weapon, null,
                Collections.singletonList(target), combined);
        soc.markResolved();
        float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);
        game.getGameState().sendMessage(soc.getCombinedDestiniesMathMessage(
                GuiUtils.formatAsString(combined), GuiUtils.formatAsString(totalDestiny),
                GuiUtils.formatAsString(defenseValue)));
        if (soc.getDrawDestinyEffect() != null) {
            soc.getDrawDestinyEffect().applyDeferredWeaponResult(game, action, weapon,
                    soc.getCardFiringWeapon(), null, target, soc.getVariableXSnapshot(), totalDestiny);
        }
        else if (totalDestiny > defenseValue) {
            game.getGameState().sendMessage("Result: Succeeded");
            if (weapon.getBlueprint().hasKeyword(Keyword.ION_CANNON)) {
                action.appendEffect(new IonizeStarshipEffect(action, target, weapon, false, true, true));
            }
            else {
                action.appendEffect(new HitCardEffect(action, target, weapon));
            }
        }
        else {
            game.getGameState().sendMessage("Result: Failed");
        }
    }

    /**
     * Decision for Separately / Combined / Don't Fire.
     */
    private abstract static class FireTwiceModeEffect extends AbstractStandardEffect implements TargetingEffect {
        private final String _playerId;
        private boolean _cancelled;

        private FireTwiceModeEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        protected void cancel() {
            _cancelled = true;
        }

        protected abstract void modeChosen(String result);

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasCarriedOut() {
            return super.wasCarriedOut() && !_cancelled;
        }

        @Override
        protected FullEffectResult playEffectReturningResult(SwccgGame game) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new MultipleChoiceAwaitingDecision("Fire a DH-17 blaster twice", new String[]{"Separately", "Combined", "Don't Fire (Cancel)"}, true) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            modeChosen(result);
                        }
                    });
            return new FullEffectResult(true);
        }
    }
}
