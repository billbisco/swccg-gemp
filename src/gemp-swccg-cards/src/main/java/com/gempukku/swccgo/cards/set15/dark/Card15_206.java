package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set6.dark.Card6_149;
import com.gempukku.swccgo.cards.set6.dark.Card6_145;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;
public class Card15_206 extends Card6_149 {
    private static final ComponentActions COMPONENT=new ComponentActions();
    public Card15_206() {
        setLore(""); setGameText("Deploy on Audience Chamber. Whenever you have an Information Broker at a location where you just initiated a battle, you may examine the top four cards of opponent's Reserve Deck. You may then lose 1 Force to cancel the battle. Also, while all of your ability on table is provided only by aliens and independent starship pilots, your aliens and starships deploy -1 and you retrieve 2 Force whenever you initiate battle. (Immune to Alter if you control three Jabba's Palace sites.)");
        addComboCardTitles(Title.Scum_And_Villainy,"Information Exchange");
        addIcon(Icon.JABBAS_PALACE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Scum And Villainy & Information Exchange"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int textId) { return COMPONENT.actions(playerId,game,result,self,textId); }
    private static final class ComponentActions extends Card6_145 {
        List<OptionalGameTextTriggerAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int textId) { return super.getGameTextOptionalAfterTriggers(playerId,game,result,self,textId); }
    }
}
