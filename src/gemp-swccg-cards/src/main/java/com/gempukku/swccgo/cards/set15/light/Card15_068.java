package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.set15.dark.Card15_228;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

/** The Light and Dark combinations print the same two actions. */
public class Card15_068 extends AbstractUsedOrStartingInterrupt {
    private static final PlanningActions ACTIONS=new PlanningActions();
    public Card15_068() {
        super(Side.LIGHT,3,"Careful Planning & Heading For The Medical Frigate",Uniqueness.UNIQUE,ExpansionSet.REFLECTIONS_GOLD,Rarity.UNKNOWN);
        setLore("");addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent(Title.Careful_Planning,Uniqueness.UNIQUE),new TitleComponent("Heading For The Medical Frigate",Uniqueness.UNIQUE));
        setGameText("USED: If a battle was just initiated, draw destiny and activate up to that much Force. STARTING: If you have not deployed an Objective, deploy from Reserve Deck one battleground site related to your starting location. Also, you may deploy up to two Effects if each of them deploys for free, is always immune to Alter and has \"deploy on table\" (or \"deploy on your side of table\") in its game text. Place Interrupt in Lost Pile.");
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {return ACTIONS.after(playerId,game,result,self);}
    @Override protected PlayInterruptAction getGameTextStartingAction(String playerId,SwccgGame game,PhysicalCard self) {return ACTIONS.starting(playerId,game,self);}
    private static class PlanningActions extends Card15_228 {
        List<PlayInterruptAction> after(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {return super.getGameTextOptionalAfterActions(playerId,game,result,self);}
        PlayInterruptAction starting(String playerId,SwccgGame game,PhysicalCard self) {return super.getGameTextStartingAction(playerId,game,self);}
    }
}
