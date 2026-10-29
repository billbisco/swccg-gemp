package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set2.dark.Card2_119;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Card15_199 extends Card2_119 {
    public Card15_199() {
        setLore(""); setGameText("Deploy on any exterior planet site (except Hoth) to add one [Dark] and one [Light]. Opponent's Force drains are -1 here. (Immune to Alter when a swamp creature is present.)");
        setTitleComponents(new TitleComponent(Title.Presence_Of_The_Force, Uniqueness.RESTRICTED_2),
                new TitleComponent(Title.Dark_Waters, Uniqueness.UNRESTRICTED));
        addIcon(Icon.A_NEW_HOPE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Presence Of The Force & Dark Waters"; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.RESTRICTED_2; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game,PhysicalCard self,PlayCardOptionId option,boolean asReact) {
        return Filters.and(Filters.exterior_planet_site,Filters.not(Filters.Hoth_site));
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.add(new IconModifier(self,Filters.hasAttached(self),Icon.DARK_FORCE,1));
        modifiers.add(new IconModifier(self,Filters.hasAttached(self),Icon.LIGHT_FORCE,1));
        return modifiers;
    }
}
