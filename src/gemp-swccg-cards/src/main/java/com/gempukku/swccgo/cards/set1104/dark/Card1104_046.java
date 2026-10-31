package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Self-Destruct Mechanism
 */
public class Card1104_046 extends AbstractUsedInterrupt {
    public Card1104_046() {
        super(Side.DARK, 4, Title.Self_Destruct_Mechanism, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("''Fraid there's not much left...I didn't hit it that hard. It musta had a self-destruct.'");
        setGameText("If your droid was just hit by a character weapon, lose that droid to target the character that fired that weapon. Draw destiny. Target hit and forfeit -4 if destiny +3 > defense value.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.your(self), Filters.droid), Filters.character_weapon)) {
            final HitResult hitResult = (HitResult) effectResult;
            final PhysicalCard droid = hitResult.getCardHit();
            final PhysicalCard firer = hitResult.getCardFiringWeapon();
            if (droid != null && firer != null && GameConditions.canTarget(game, self, firer)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Lose droid to target firing character");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character that fired", firer) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(droid);
                                action.addAnimationGroup(targetedCard);
                                action.appendCost(
                                        new LoseCardFromTableEffect(action, droid));
                                action.allowResponses("Lose " + GameUtils.getCardLink(droid) + " to target " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                return Collections.singletonList(finalTarget);
                                                            }

                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null || finalTarget == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }
                                                                float defense = game.getModifiersQuerying().getDefenseValue(gameState, finalTarget);
                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defense));
                                                                if ((totalDestiny + 3) > defense) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new HitCardEffect(action, finalTarget, self));
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfGameModifierEffect(action,
                                                                                    new ForfeitModifier(self, finalTarget, -4),
                                                                                    "Makes " + GameUtils.getCardLink(finalTarget) + " forfeit -4"));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
