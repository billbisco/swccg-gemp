package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set9.dark.Card9_123;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Card15_178 extends Card9_123 {
    public Card15_178() {
        setLore(""); setGameText("Deploy on your Throne Room. You may not Force drain here. While Emperor here, opponent's characters are deploy +2 to Death Star II sites and, once per turn, you may lose Effect (or 1 Force from top of Reserve Deck) to add one battle destiny anywhere. Also, while Emperor is at a battleground planet site you control, at each related site where an Imperial is present, your Force drains are +1. Effect lost if Emperor lost from table. (Immune to Alter.)"); setAlternateDestiny(3);
        addComboCardTitles(Title.Emperors_Power,"Overseeing It Personally");
        addIcon(Icon.DEATH_STAR_II,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Emperor's Power & Overseeing It Personally"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        Filter relatedToEmperor = Filters.relatedSiteTo(self, Filters.and(Filters.Emperor,
                Filters.at(Filters.and(Filters.battleground, Filters.planet_site, Filters.controls(self.getOwner())))));
        modifiers.add(new ForceDrainModifier(self,Filters.and(relatedToEmperor,Filters.wherePresent(self,Filters.Imperial)),1,self.getOwner()));
        return modifiers;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
        if(TriggerConditions.justLost(game,result,Filters.Emperor)) {
            RequiredGameTextTriggerAction action=new RequiredGameTextTriggerAction(self,textId);
            action.setText("Lose Effect"); action.appendEffect(new LoseCardFromTableEffect(action,self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
