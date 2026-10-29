package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set11.light.Card11_035;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.*;
import java.util.*;

public class Card15_076 extends Card11_035 {
    public Card15_076() {
        setLore("");addIcon(Icon.TATOOINE,0);addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Either Way, You Win","Eventually You'll Lose");
        setGameText("USED: If a battle was just initiated, place the top card of your Used Pile on top of your Reserve Deck. LOST: Use 3 Force to cause opponent to reveal hand. All cards opponent has 3 or more of in hand are lost. OR Opponent chooses: opponent may activate up to 2 Force and you may activate up to 4 Force, or opponent retrieves 1 Force and you retrieve 3 Force.");
    }
    @Override public String getTitle() {return "Either Way, You Win & Eventually You'll Lose";}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>(super.getGameTextTopLevelActions(playerId,game,self));
        String opponent=game.getOpponent(playerId);
        if(GameConditions.canActivateForce(game,playerId)||GameConditions.canActivateForce(game,opponent)||GameConditions.hasLostPile(game,playerId)||GameConditions.hasLostPile(game,opponent)) {
            PlayInterruptAction action=new PlayInterruptAction(game,self,CardSubtype.LOST);action.setText("Make opponent choose");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new PlayoutDecisionEffect(action,opponent,new MultipleChoiceAwaitingDecision("Choose effect",new String[]{"Activate up to 2 Force","Retrieve 1 Force"}) {
                        @Override protected void validDecisionMade(int index,String result) {
                            if(index==0) {activateUpTo(action,game,opponent,2);activateUpTo(action,game,playerId,4);}
                            else {action.appendEffect(new RetrieveForceEffect(action,opponent,1));action.appendEffect(new RetrieveForceEffect(action,playerId,3));}
                        }
                    }));
                }
            });actions.add(action);
        }
        return actions;
    }
    private void activateUpTo(PlayInterruptAction action,SwccgGame game,String playerId,int limit) {
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                if(!GameConditions.canActivateForce(game,playerId))return;
                int maximum=Math.min(limit,game.getGameState().getReserveDeckSize(playerId));
                action.insertEffect(new PlayoutDecisionEffect(action,playerId,new IntegerAwaitingDecision("Choose amount of Force to activate",0,maximum,maximum) {
                    @Override public void decisionMade(int amount) throws DecisionResultInvalidException {action.insertEffect(new ActivateForceEffect(action,playerId,amount));}
                }));
            }
        });
    }
}
