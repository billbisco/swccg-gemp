package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.cards.set12.light.Card12_042;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import java.util.*;

public class Card15_032 extends Card12_042 {
    public Card15_032() {
        setLore("");addIcon(Icon.CORUSCANT,0);addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent(Title.Credits_Will_Do_Fine,Uniqueness.UNIQUE),new TitleComponent("We're Leaving",Uniqueness.UNIQUE));
        setGameText("Use 2 Force to deploy on table. If you just initiated a Force drain (or won a battle) at Watto's Junkyard, opponent loses 1 Force (cannot be reduced) and stacks lost card here face down. Also, if you just forfeited all your cards that participated in a battle that opponent initiated, you may place this Effect out of play to cancel all remaining battle damage; place all cards here in owner's Used Pile. (Immune to Alter.)");
    }
    @Override public String getTitle() {return "Credits Will Do Fine & We're Leaving";}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}

    // Remember participants not yet forfeited, including cards that leave battle by another means.
    // The modern We're Leaving implementation's empty-table condition is broader than this printing.
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
        if(!GameConditions.isDuringBattle(game)) self.setWhileInPlayData(null);
        else {
            if(TriggerConditions.battleInitiated(game,result,game.getOpponent(self.getOwner())))
                self.setWhileInPlayData(new WhileInPlayData(new ArrayList<PhysicalCard>()));
            if(self.getWhileInPlayData()!=null) {
                List<PhysicalCard> outstanding=self.getWhileInPlayData().getPhysicalCards();
                for(PhysicalCard participant:game.getGameState().getBattleState().getCardsParticipating(self.getOwner()))
                    if(!outstanding.contains(participant))outstanding.add(participant);
                PhysicalCard forfeited=forfeitedCard(result);
                if(forfeited!=null)outstanding.remove(forfeited);
            }
        }
        return super.getGameTextRequiredAfterTriggers(game,result,self,sourceId);
    }
    private PhysicalCard forfeitedCard(EffectResult result) {
        if(result.getType()==EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)return ((LostFromTableResult)result).getCard();
        if(result.getType()==EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)return ((ForfeitedCardToUsedPileFromTableResult)result).getCard();
        return null;
    }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
        PhysicalCard forfeited=forfeitedCard(result);
        if(forfeited!=null && playerId.equals(forfeited.getOwner())
                && GameConditions.isDuringBattleInitiatedBy(game,game.getOpponent(playerId))
                && GameConditions.isBattleDamageRemaining(game,playerId)
                && self.getWhileInPlayData()!=null && self.getWhileInPlayData().getPhysicalCards().isEmpty()) {
            OptionalGameTextTriggerAction action=new OptionalGameTextTriggerAction(self,sourceId);
            action.setText("Cancel all remaining battle damage");
            // Return stacked cards before their supporting Effect leaves, so normal leave-play rules cannot lose them.
            action.appendCost(new PutStackedCardsInUsedPileEffect(action,game.getOpponent(playerId),new ArrayList<>(game.getGameState().getStackedCards(self)),true));
            action.appendCost(new PlaceCardOutOfPlayFromTableEffect(action,self));
            action.appendEffect(new SatisfyAllBattleDamageEffect(action,playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
