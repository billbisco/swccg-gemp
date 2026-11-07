package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.List;

/** Vader's Cybernetic Enhancements, Virtual Block 7.1. */
public class Card1271_015 extends LegacyVaderEquipment {
    public Card1271_015() {
        super(0, "Vader's Cybernetic Enhancements");
        setLore("Users of the Force can mysteriously manipulate their own physiology and psychology. Control allows rapid healing and provides some immunity to pain, poison and disease.");
        setGameText("Deploy on [Vader] Vader. Vader may move as a 'react.' Your battle destiny draws here are +1. Opponent's cards deployed on Vader are canceled. If Vader about to be placed in Lost Pile, may lose 4 Force to take him (and a lightsaber he was armed with, if possible) into hand. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(
            new MayMoveOtherCardsAsReactToLocationModifier(self, "Move Vader as a 'react'", self.getOwner(), Filters.hasAttached(self), Filters.any),
            new EachBattleDestinyModifier(self, Filters.here(self), 1, self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null || !TriggerConditions.isTableChanged(game, result)) return null;
        Filter opponentAttachments = Filters.and(Filters.opponents(self), Filters.attachedTo(vader));
        if (!GameConditions.canTargetToCancel(game, self, opponentAttachments)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardAction(action, opponentAttachments, "opponent's card deployed on Vader");
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null || !TriggerConditions.isAboutToBeLost(game, result, vader)
                || game.getGameState().getPlayerLifeForce(playerId) + game.getGameState().getHand(playerId).size() < 4) return null;

        AboutToLoseCardFromTableResult pendingLoss = (AboutToLoseCardFromTableResult) result;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        action.setText("Lose 4 Force to take Vader into hand");
        action.appendCost(new LoseForceEffect(action, playerId, 4, true));
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                pendingLoss.getPreventableCardEffect().preventEffectOnCard(vader);
                Filter weapon = Filters.and(Filters.attachedTo(vader), Filters.lightsaber);
                if (GameConditions.canSpot(game, self, weapon)) {
                    action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose Vader's lightsaber", weapon) {
                        @Override protected void cardSelected(PhysicalCard lightsaber) {
                            action.appendEffect(new ReturnCardsToHandFromTableSimultaneouslyEffect(action, List.of(vader, lightsaber), false));
                        }
                    });
                } else {
                    action.appendEffect(new ReturnCardToHandFromTableEffect(action, vader));
                }
            }
        });
        return List.of(action);
    }
}
