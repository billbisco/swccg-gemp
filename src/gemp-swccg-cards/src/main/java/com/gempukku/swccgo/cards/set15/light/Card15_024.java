package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_044;
import com.gempukku.swccgo.cards.set1.light.Card1_062;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.*;
import java.util.*;

public class Card15_024 extends Card1_062 {
    private static final BeggarActions BEGGAR=new BeggarActions();
    public Card15_024() {
        setLore("");addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent(Title.Beggar,Uniqueness.UNIQUE),new TitleComponent(Title.Revolution,Uniqueness.UNRESTRICTED));
        setGameText("Use 3 Force to deploy on a location. Rotate the location so that the icons and game texts switch direction. (If Expand the Empire present, it also switches.) If Effect later canceled, location rotates back again. Also, you may use any amount of Force in the opponent's Force Pile during your turns. However, if you use more than 1 of the opponent's Force in a turn, Effect is lost at the end of that turn.");
    }
    @Override public String getTitle() {return "Beggar & Revolution";}
    @Override public Uniqueness getUniqueness() {return Uniqueness.UNIQUE;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.addAll(BEGGAR.modifiers(game,self));return modifiers;
    }
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId,SwccgGame game,Effect effect,PhysicalCard self,int sourceId) {
        return BEGGAR.optional(playerId,game,effect,self,sourceId);
    }
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game,Effect effect,PhysicalCard self,int sourceId) {
        return BEGGAR.before(game,effect,self,sourceId);
    }
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
        return BEGGAR.after(game,result,self,sourceId);
    }
    private static class BeggarActions extends Card1_044 {
        List<Modifier> modifiers(SwccgGame game,PhysicalCard self) {return super.getGameTextWhileActiveInPlayModifiers(game,self);}
        List<OptionalGameTextTriggerAction> optional(String playerId,SwccgGame game,Effect effect,PhysicalCard self,int sourceId) {return super.getGameTextOptionalBeforeTriggers(playerId,game,effect,self,sourceId);}
        List<RequiredGameTextTriggerAction> before(SwccgGame game,Effect effect,PhysicalCard self,int sourceId) {return super.getGameTextRequiredBeforeTriggers(game,effect,self,sourceId);}
        List<RequiredGameTextTriggerAction> after(SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {return super.getGameTextRequiredAfterTriggers(game,result,self,sourceId);}
    }
}
