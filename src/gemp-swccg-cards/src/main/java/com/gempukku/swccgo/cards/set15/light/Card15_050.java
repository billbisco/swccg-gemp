package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set4.light.Card4_025;
import com.gempukku.swccgo.cards.set4.light.Card4_037;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_050 extends Card4_025 {
    private static final ScrambleActions SCRAMBLE=new ScrambleActions();
    public Card15_050() {
        setLore("");addIcon(Icon.DAGOBAH,0);addIcons(Icon.REFLECTIONS_GOLD);addComboCardTitles(Title.Scramble,"Hiding In The Garbage");
        setGameText("Deploy on table. At any time, you may declare a card type. Use Force one by one, revealing each card used, until a card of that type is revealed (take it into hand) or Force Pile is depleted. Also, opponent's pilots (except Vader) present at sites may not use ability to draw battle destiny. Effect canceled when opponent has no pilots at sites.");
    }
    @Override public String getTitle() {return "Scramble & Hiding In The Garbage";}
    @Override public Float getDestiny() {return 4f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game,PhysicalCard self) {return null;}
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {return SCRAMBLE.modifiers(game,self);}
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {return SCRAMBLE.after(game,result,self,sourceId);}
    private static class ScrambleActions extends Card4_037 {
        List<Modifier> modifiers(SwccgGame game,PhysicalCard self) {return super.getGameTextWhileActiveInPlayModifiers(game,self);}
        List<RequiredGameTextTriggerAction> after(SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {return super.getGameTextRequiredAfterTriggers(game,result,self,sourceId);}
    }
}
