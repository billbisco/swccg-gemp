package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set4.light.Card4_045;
import com.gempukku.swccgo.cards.set5.light.Card5_065;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Effect;
import java.util.List;

public class Card15_064 extends Card4_045 {
    private static final PutThatDownActions PUT_THAT_DOWN=new PutThatDownActions();
    public Card15_064() {
        setLore("");addIcon(Icon.DAGOBAH,0);addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Away Put Your Weapon","Put That Down");
        setGameText("USED: If a lightsaber was just used to enhance a Force drain, place it in Owner's Used Pile. LOST: Place any or all of your devices and character weapons on table in your Used Pile. OR If one of your characters was just targeted by a weapon during battle, use 3 Force to cancel that targeting.");
    }
    @Override public String getTitle() {return "Away Put Your Weapon & Put That Down";}
    @Override public Float getDestiny() {return 3f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId,SwccgGame game,Effect effect,PhysicalCard self) {
        if(!TriggerConditions.isTargetedByWeapon(game,effect,Filters.and(Filters.your(self),Filters.character),Filters.weapon))return null;
        var actions=PUT_THAT_DOWN.actions(playerId,game,effect,self);
        if(actions!=null)for(var action:actions)action.setPlayedAsSubtype(CardSubtype.LOST);
        return actions;
    }
    private static class PutThatDownActions extends Card5_065 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,Effect effect,PhysicalCard self) {return super.getGameTextOptionalBeforeActions(playerId,game,effect,self);}
    }
}
