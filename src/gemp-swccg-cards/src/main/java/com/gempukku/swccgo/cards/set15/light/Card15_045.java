package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.set1.light.Card1_054;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.TitleComponent;
public class Card15_045 extends Card1_054 {
    public Card15_045() {
        setLore("");addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Obi-Wan's Cape",Uniqueness.UNIQUE),new TitleComponent(Title.Lightsaber_Proficiency,Uniqueness.UNIQUE));
        setGameText("Use 1 Force to deploy on a character with ability > 2 armed with a lightsaber. That character is power +3 in battles, immune to attrition < 5, and may add 1 to Force drain where present. Also, when present at the start of a battle, may use 1 Force to cause one opponent's character of ability = 1 (your choice) to move away (for free), or that character is immediately lost. Effect lost if character loses lightsaber.");
    }
    @Override public String getTitle() {return "Obi-Wan's Cape & Lightsaber Proficiency";}
    @Override public Float getDestiny() {return 3f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game,PhysicalCard self) {return Collections.singletonList(new DefinedByGameTextDeployCostModifier(self,1));}
    @Override protected Filter getGameTextValidDeployTargetFilter(SwccgGame game,PhysicalCard self,PlayCardOptionId option,boolean asReact) {
        return Filters.and(Filters.character_with_a_lightsaber,Filters.abilityMoreThan(2));
    }
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new LinkedList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self,Filters.hasAttached(self),5));return modifiers;
    }
    private static final CapeActions CAPE=new CapeActions();
    @Override protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
        List<OptionalGameTextTriggerAction> actions=new LinkedList<>();
        var proficiency=super.getGameTextOptionalAfterTriggers(playerId,game,result,self,sourceId);
        if(proficiency!=null)actions.addAll(proficiency);
        var cape=CAPE.actions(playerId,game,result,self,sourceId);
        if(cape!=null)actions.addAll(cape);
        return actions.isEmpty()?null:actions;
    }
    private static class CapeActions extends com.gempukku.swccgo.cards.set1.light.Card1_057 {
        List<OptionalGameTextTriggerAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self,int sourceId) {
            return super.getGameTextOptionalAfterTriggers(playerId,game,result,self,sourceId);
        }
    }
}
