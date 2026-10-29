package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_045;
import com.gempukku.swccgo.cards.set1.light.Card1_066;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import java.util.*;

public class Card15_056 extends Card1_045 {
    private static final TrafficControlActions TRAFFIC_CONTROL = new TrafficControlActions();
    public Card15_056() {
        setLore("");addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Traffic Control",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Crash_Site_Memorial,Uniqueness.UNIQUE));
        setGameText("Deploy on table. You may place lost vehicles, droids, weapons, and devices here instead of Lost Pile. Once during your deploy phase, you may exchange any one card from hand with one card from your 'memorial.' Also, during your control phase, you may return any cards from your hand to the top of your Used Pile.");
    }
    @Override public String getTitle() {return "Traffic Control & Crash Site Memorial";}
    @Override public Float getDestiny() {return 3f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self,int sourceId) {
        List<TopLevelGameTextAction> actions=new ArrayList<>();
        var memorial=super.getGameTextTopLevelActions(playerId,game,self,sourceId);
        if(memorial!=null)actions.addAll(memorial);
        var traffic=TRAFFIC_CONTROL.actions(playerId,game,self,sourceId);
        if(traffic!=null)actions.addAll(traffic);
        return actions.isEmpty()?null:actions;
    }
    private static class TrafficControlActions extends Card1_066 {
        List<TopLevelGameTextAction> actions(String playerId,SwccgGame game,PhysicalCard self,int sourceId) {
            return super.getGameTextTopLevelActions(playerId,game,self,sourceId);
        }
    }
}
