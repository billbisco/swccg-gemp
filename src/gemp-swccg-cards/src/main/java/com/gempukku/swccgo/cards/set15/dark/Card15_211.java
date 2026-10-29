package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;


import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.cards.set12.dark.Card12_142;
import com.gempukku.swccgo.cards.set12.dark.Card12_137;
import java.util.ArrayList;
public class Card15_211 extends Card12_142 {
    private static final MotionActions MOTION=new MotionActions();
    public Card15_211() {
        setLore(""); setGameText("Deploy on table. If no senator here, you may place a senator here from hand to cancel an opponent's Force drain or to add 2 to your next Force drain at a battleground this turn. If a trade agenda here, once during your turn, you may place a card from hand in Lost Pile, shuffle that pile, and take top card into hand. If a wealth agenda here, once per turn, you may lose your just drawn battle destiny to substitute it with a card from hand.");
        addComboCardTitles(Title.This_Is_Outrageous,"Motion Supported"); addIcon(Icon.CORUSCANT,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "This Is Outrageous! & Motion Supported"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self,int textId) {
        List<TopLevelGameTextAction> actions=new ArrayList<>(); var trade=super.getGameTextTopLevelActions(playerId,game,self,textId); if(trade!=null) actions.addAll(trade);
        var drain=MOTION.actions(playerId,game,self,textId); if(drain!=null) actions.addAll(drain); return actions;
    }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
        List<OptionalGameTextTriggerAction> actions=new ArrayList<>(); var cancel=super.getGameTextOptionalAfterTriggers(playerId,game,result,self,textId); if(cancel!=null) actions.addAll(cancel);
        var wealth=wealthActions(playerId,game,result,self,textId); if(wealth!=null) actions.addAll(wealth); return actions;
    }
    private static final class MotionActions extends Card12_137 {
        List<TopLevelGameTextAction> actions(String playerId,SwccgGame game,PhysicalCard self,int textId) { return super.getGameTextTopLevelActions(playerId,game,self,textId); }
    }
    protected List<OptionalGameTextTriggerAction> wealthActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, Filters.wealth_agenda)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.canMakeDestinyCardLost(game)
                && GameConditions.hasHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel and substitute destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(selectedCard)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            final float destinyValue = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                                            action.setActionMsg("Cancel and lose just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", and substitute from hand " + GameUtils.getCardLink(selectedCard) + "'s destiny value of " + GuiUtils.formatAsString(destinyValue) + " for battle destiny");
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseDestinyCardEffect(action));
                                            action.appendEffect(
                                                    new PutCardFromHandOnUsedPileEffect(action, playerId, selectedCard, false));
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, destinyValue));
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
}
