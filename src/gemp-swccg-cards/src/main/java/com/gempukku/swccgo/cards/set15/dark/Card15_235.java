package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set12.dark.Card12_161;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.*;
import java.util.*;
public class Card15_235 extends Card12_161 {
    public Card15_235() {
        setLore(""); setGameText("USED: Cancel Rebel Barrier, It's A Trap!, or Smoke Screen. OR Re-target Fallen Portal by choosing up to two of opponent's characters there instead of your own. OR Peek at the top card of your Force Pile; either return it, or place it under your Force Pile or Reserve Deck. LOST: Target opponent's Jedi. Draw destiny. If destiny &gt; 2, target's game text is canceled for remainder of turn."); setCardSubtype(CardSubtype.USED_OR_LOST);
        addComboCardTitles("Dioxis","They're Still Coming Through!");
        addIcon(Icon.CORUSCANT,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Dioxis & They're Still Coming Through!"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId,SwccgGame game,Effect effect,PhysicalCard self) {
        var actions=super.getGameTextOptionalBeforeActions(playerId,game,effect,self);
        if(actions!=null) actions.forEach(action->action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        var actions=new ArrayList<PlayInterruptAction>(); var used=super.getGameTextTopLevelActions(playerId,game,self);
        if(used!=null) { used.forEach(action->action.setPlayedAsSubtype(CardSubtype.USED)); actions.addAll(used); }
        Filter jedi=Filters.and(Filters.opponents(self),Filters.Jedi,GameConditions.isDuringBattle(game)?Filters.participatingInBattle:Filters.any);
        if(GameConditions.canTarget(game,self,jedi)) {
            var action=new PlayInterruptAction(game,self,CardSubtype.LOST); action.setText("Cancel Jedi's game text");
            action.appendTargeting(new TargetCardOnTableEffect(action,playerId,"Choose Jedi",jedi) {
                @Override protected void cardTargeted(int group,PhysicalCard target) {
                    action.addAnimationGroup(target);
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override protected void performActionResults(Action targetingAction) {
                            final PhysicalCard finalTarget=action.getPrimaryTargetCard(group);
                            action.appendEffect(new DrawDestinyEffect(action,playerId) {
                                @Override protected void destinyDraws(SwccgGame current,List<PhysicalCard> cards,List<Float> values,Float total) {
                                    if(total!=null && total>2) action.appendEffect(new CancelGameTextUntilEndOfTurnEffect(action,finalTarget));
                                }
                            });
                        }
                    });
                }
            }); actions.add(action);
        }
        return actions;
    }
}
