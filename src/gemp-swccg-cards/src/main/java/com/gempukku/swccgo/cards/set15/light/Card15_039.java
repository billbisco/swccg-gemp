package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_043;
import com.gempukku.swccgo.cards.set12.light.Card12_045;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.*;

public class Card15_039 extends Card12_043 {
    private static final PleaActions PLEA = new PleaActions();
    public Card15_039() {
        setLore("");addIcon(Icon.CORUSCANT,0);addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("I Will Not Defer",Uniqueness.UNIQUE),new TitleComponent(Title.Plea_To_The_Court,Uniqueness.UNIQUE));
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to cancel an opponent's Force drain or to add 2 to your next Force drain at a battleground this turn. If a peace agenda here, once per turn you may activate up to 2 Force when opponent initiates battle. If a justice agenda here, during a battle where opponent has a weapon and you do not, may cancel a just drawn weapon destiny to cause a re-draw.");
    }
    @Override public String getTitle() {return "I Will Not Defer & Plea To The Court";}
    @Override public Float getDestiny() {return 2f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
        List<OptionalGameTextTriggerAction> actions=new ArrayList<>();
        var defer=super.getGameTextOptionalAfterTriggers(playerId,game,result,self,sourceId);
        if(defer!=null)actions.addAll(defer);
        var plea=PLEA.actions(playerId,game,result,self,sourceId);
        if(plea!=null)actions.addAll(plea);
        return actions.isEmpty()?null:actions;
    }
    private static class PleaActions extends Card12_045 {
        List<OptionalGameTextTriggerAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
            return super.getGameTextOptionalAfterTriggers(playerId,game,result,self,sourceId);
        }
    }
}
