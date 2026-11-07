package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.LandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.List;

/** Vader's Bionic Limbs, Virtual Block 7.1. */
public class Card1271_013 extends LegacyVaderEquipment {
    public Card1271_013() {
        super(0, "Vader's Bionic Limbs");
        setLore("Darth Vader often used physical means of 'persuasion' to get information. Captain Antilles of Tantive IV chose to die rather than reveal the location of the stolen Death Star plans.");
        setGameText("Deploy on [Vader] Vader. Vader is landspeed +1. If Vader about to be lost before the damage segment, he is instead 'hit.' During battle, may target one opponent's character present. Draw destiny. Target 'choked' (lost) if destiny > ability. (Immune to Alter.)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new LandspeedModifier(self, Filters.hasAttached(self), 1));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null || !GameConditions.isDuringBattle(game) || GameConditions.isDamageSegmentOfBattle(game)
                || !TriggerConditions.isAboutToBeLost(game, result, vader)) return null;

        AboutToLoseCardFromTableResult pendingLoss = (AboutToLoseCardFromTableResult) result;
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Make Vader 'hit' instead");
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                pendingLoss.getPreventableCardEffect().preventEffectOnCard(vader);
                action.appendEffect(new HitCardEffect(action, vader, self));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null || !GameConditions.isInBattle(game, vader)
                || !GameConditions.isOncePerBattle(game, self, playerId, sourceId)) return null;
        Filter targets = Filters.and(Filters.opponents(self), Filters.character, Filters.present(vader));
        TargetingReason choke = TargetingReason.TO_BE_CHOKED;
        if (!GameConditions.canTarget(game, self, choke, targets)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId);
        action.setText("'Choke' a character");
        action.appendUsage(new OncePerBattleEffect(action));
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target character", choke, targets) {
            @Override protected void cardTargeted(int group, PhysicalCard target) {
                action.addAnimationGroup(target);
                action.allowResponses(new RespondableEffect(action) {
                    @Override protected void performActionResults(Action targetingAction) {
                        PhysicalCard victim = targetingAction.getPrimaryTargetCard(group);
                        action.appendEffect(new DrawDestinyEffect(action, playerId, 1, DestinyType.CHOKE_DESTINY) {
                            @Override protected List<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return List.of(victim);
                            }
                            @Override protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                                if (total != null && total > game.getModifiersQuerying().getAbility(game.getGameState(), victim)) {
                                    action.appendEffect(new LoseCardFromTableEffect(action, victim));
                                }
                            }
                        });
                    }
                });
            }
        });
        return List.of(action);
    }
}
