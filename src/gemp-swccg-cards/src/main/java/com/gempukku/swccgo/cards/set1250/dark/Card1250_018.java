package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_040;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/** Legacy Block 5: An Enemy Of The Republic.
 * Text: PC-E3, the Virtual Cards Master List (2013-04-27; the PC card image, PC-E2, came first), which repeats the 2009 release-sheet words ("their participating
 * character", "may ▲ a card"); the PC website card-list text (PC-E1, 2012, "non-droid") is version 2250_1018
 * (Bill 2026-11-11: every publication is a version). */
public class Card1250_018 extends Card1116_040 {
    public Card1250_018() {
        setGameText("Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ a card.");
        addIcons(Icon.SIDIOUS, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotCancelDestinyDrawsModifier(self,
                (state, querying) -> state.isDuringBattle() && self.getAttachedTo() != null
                        && state.getBattleLocation().equals(querying.getLocationHere(state, self.getAttachedTo())),
                self.getOwner(), null));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        var bearer = self.getAttachedTo();
        if (bearer == null) return null;
        if (TriggerConditions.battleInitiated(game, result)
                && Filters.at(Filters.Galactic_Senate).accepts(game, bearer)) {
            var action = relocateToBattle(game, self, sourceId);
            return action == null ? null : List.of(action);
        }
        if (!Filters.Sidious.accepts(game, bearer)
                && TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, result, bearer)) {
            var action = returnToSidious(game, self, sourceId);
            return action == null ? null : List.of(action);
        }
        return null;
    }

    /** Master List (PC-E3) and the 2009 sheet: "their participating character" (the card list, 2250_1018, says "non-droid"). */
    protected Filter relocationTargetFilter() {
        return Filters.character;
    }

    private RequiredGameTextTriggerAction relocateToBattle(SwccgGame game, PhysicalCard self, int sourceId) {
        String opponent = game.getOpponent(self.getOwner());
        var battle = game.getGameState().getBattleState();
        if (battle == null) return null;
        var targets = Filters.filter(battle.getCardsParticipating(opponent), game,
                Filters.and(relocationTargetFilter(), Filters.your(opponent)));
        if (targets.isEmpty()) return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Relocate to a participating character");
        action.appendEffect(new ChooseCardOnTableEffect(action, opponent, "Choose your participating character", targets) {
            @Override
            protected void cardSelected(PhysicalCard target) {
                action.appendEffect(new AttachCardFromTableEffect(action, self, target));
            }
        });
        return action;
    }

    private RequiredGameTextTriggerAction returnToSidious(SwccgGame game, PhysicalCard self, int sourceId) {
        var sidious = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Sidious));
        if (sidious == null) return null;
        String owner = self.getOwner();
        var id = GameTextActionId.LEGACY__AN_ENEMY_OF_THE_REPUBLIC__TAKE_INTO_HAND;
        var action = new RequiredGameTextTriggerAction(self, sourceId, id);
        action.setText("Relocate to Sidious");
        action.appendEffect(new AttachCardFromTableEffect(action, self, sidious));
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (self.getAttachedTo() != sidious
                        || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, owner, self, id)) return;
                action.appendEffect(new PlayoutDecisionEffect(action, owner,
                        new YesNoDecision("Take a card into hand from Reserve Deck?") {
                    @Override
                    public void yes() {
                        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, owner, Filters.any, true));
                    }
                }));
            }
        });
        return action;
    }
}
