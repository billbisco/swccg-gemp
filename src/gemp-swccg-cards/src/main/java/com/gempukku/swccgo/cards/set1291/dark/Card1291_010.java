package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: Maul's Cape
 */
public class Card1291_010 extends AbstractNormalEffect {
    public Card1291_010() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Maul's Cape", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("A Sith Lord can use the Force to reach out and find what he seeks.");
        setGameText("Deploy on your character of ability > 3. Character gains scout skill, their weapon destiny draws are +1, and character is immune to Clash Of Sabers and attrition < 5.");
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter character = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, character, Keyword.SCOUT));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, character, 1));
        modifiers.add(new ImmuneToTitleModifier(self, character, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, character, 5));
        return modifiers;
    }
}
