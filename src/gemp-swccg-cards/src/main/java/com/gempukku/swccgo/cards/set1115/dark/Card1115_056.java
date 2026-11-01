package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: No Bargain
 */
public class Card1115_056 extends AbstractNormalEffect {
    public Card1115_056() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.No_Bargain, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Zeet tu seet. Jabba no tuzindy honkabee.'");
        setGameText("Deploy on your [Reflections II] Objective; 'Xizor' on it (and on Guri) is treated as 'Shada'. Non-[Lightsaber] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent] starships. Unique (*) Mistryl (except Kitik) are power +2. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.hasAttached(self), Filters.Guri), ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.not(Filters.lightsaber), Filters.alien, Filters.female), Keyword.MISTRYL));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.not(Filters.lightsaber), Filters.alien, Filters.female), Keyword.BLACK_SUN_AGENT));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.except(Filters.Mistryl), Filters.except(Filters.and(Icon.INDEPENDENT, Filters.starship))), self.getOwner()));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Mistryl, Filters.unique, Filters.except(Filters.title("Kitik Keed'kak"))), 2));
        return modifiers;
    }
}
