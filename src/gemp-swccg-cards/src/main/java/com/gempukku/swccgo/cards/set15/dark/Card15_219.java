package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set8.dark.Card8_152;
import com.gempukku.swccgo.cards.set8.dark.Card8_135;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.*;
public class Card15_219 extends Card8_152 {
    private static final StomachActions STOMACH=new StomachActions();
    public Card15_219() {
        setLore(""); setCardSubtype(CardSubtype.USED_OR_LOST);
        setGameText("USED: Add 1 to landspeed of your scout at an exterior planet site for remainder of turn. OR if Igar is with Grond or Dyer in battle, draw one destiny and subtract it from opponent's attrition against you. LOST: Cancel a Force drain at an exterior planet site where opponent has no vehicles or starships. Draw destiny. If destiny > number of opponent's characters at that site, choose one of those characters to be missing.");
        addComboCardTitles("Always Thinking With Your Stomach","Scout Recon");
        addIcon(Icon.ENDOR,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Always Thinking With Your Stomach & Scout Recon"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        var actions=super.getGameTextTopLevelActions(playerId,game,self);
        if(actions!=null) actions.forEach(action->action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        var used=super.getGameTextOptionalAfterActions(playerId,game,result,self);
        if(used!=null) { used.forEach(action->action.setPlayedAsSubtype(CardSubtype.USED)); actions.addAll(used); }
        var lost=STOMACH.actions(playerId,game,result,self);
        if(lost!=null) { lost.forEach(action->action.setPlayedAsSubtype(CardSubtype.LOST)); actions.addAll(lost); }
        return actions;
    }
    private static final class StomachActions extends Card8_135 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) { return super.getGameTextOptionalAfterActions(playerId,game,result,self); }
    }
}
