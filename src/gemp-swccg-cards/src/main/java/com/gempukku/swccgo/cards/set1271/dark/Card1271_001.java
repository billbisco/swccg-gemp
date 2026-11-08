package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.Arrays;
import java.util.List;

/** Virtual Block7.1 final title; printed Republic character, not a droid. */
public class Card1271_001 extends AbstractRepublic {
    public Card1271_001() {
        super(Side.DARK, 1, 6, 7, 5, 8, "Grievous, Hunter Of Jedi", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Commander. Leader.");
        setGameText("[Virtual Block 5] Dark Jedi Lightsaber may deploy on Grievous. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4.");
        addPersona(Persona.GRIEVOUS);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.SPECIAL_EDITION);
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter blade = Filters.and(Filters.your(self), Icon.LEGACY_BLOCK_5, Filters.title("Dark Jedi Lightsaber"));
        Filter jediPresent = Filters.and(Filters.Jedi, Filters.presentWith(self));
        Condition armed = new ArmedWithCondition(self, Filters.lightsaber);
        return Arrays.asList(
            new MayDeployToTargetModifier(self, blade, self),
            new MayUseWeaponModifier(self, blade),
            new PowerModifier(self, Filters.and(jediPresent, Filters.not(Filters.Jedi_Council_member)), -1),
            new PowerModifier(self, Filters.and(jediPresent, Filters.Jedi_Council_member), -2),
            new EachBattleDestinyModifier(self, Filters.here(self), armed, 1, self.getOwner()),
            new MayNotHaveForfeitValueReducedModifier(self, self, armed),
            new ImmuneToAttritionLessThanModifier(self, 4));
    }
}
