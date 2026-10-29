package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set6.light.Card6_055;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.ArrayList;
import java.util.List;

public class Card15_037 extends Card6_055 {
    public Card15_037() {
        setLore("");addIcon(Icon.JABBAS_PALACE,0);addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles(Title.I_Must_Be_Allowed_To_Speak,Title.Goo_Nee_Tay);
        setGameText("Deploy on a Jabba's Palace site. Luke may deploy at this site regardless of presence or location deployment restrictions. When he is deployed here, relocate Effect to Luke, he is immune to attrition while on Tatooine and all opponent's characters of ability > 2 are deploy +2 here. (Immune to Alter.)");
    }
    @Override public String getTitle() {return "I Must Be Allowed To Speak & Goo Nee Tay";}
    @Override public Float getDestiny() {return 3f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.add(new DeployCostToLocationModifier(self,Filters.and(Filters.opponents(self),Filters.character,Filters.abilityMoreThan(2)),
                new AttachedCondition(self,Filters.Luke),2,Filters.here(self)));
        return modifiers;
    }
}
