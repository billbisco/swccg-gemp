package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Grievous, Hunter Of Jedi (Virtual Block 7.1, printed as Cyborg Commander, Hunter Of Jedi); Republic character, not a droid.
 * Original version of 1271_1: the Virtual Block 7.1 release sheet text (Jun 2010; lore "Leader. \"He's no Jedi.\"", so Leader but not Commander).
 * Superseded by the PC website card list text (by 2012-07-21, PC-E1, 2271_1001); the current card is the Virtual Cards Master List text
 * (Apr 2013, PC-E2; Cyborg Commander renamed Grievous on 2013-01-29, lore "Commander. Leader."). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_001 extends AbstractRepublic {
    public Card2271_001() {
        super(Side.DARK, 1, 6, 7, 5, 8, "Grievous, Hunter Of Jedi", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Leader. \"He's no Jedi.\"");
        setGameText("[Virtual Block 5] Dark Jedi Lightsaber may deploy on Cyborg Commander. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1271_1", "",
                "Virtual Block 7.1 release sheet (2010-06-18); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
        setDisplayTitle("Cyborg Commander, Hunter Of Jedi");
        addPersona(Persona.GRIEVOUS);
        addKeywords(Keyword.LEADER);
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
