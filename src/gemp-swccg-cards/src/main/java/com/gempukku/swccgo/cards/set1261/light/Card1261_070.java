package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Obi-Wan, Crazy Wizard, final Light master page 12 and complete official face. */
public class Card1261_070 extends AbstractRebel {
    public Card1261_070() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, "Obi-Wan, Crazy Wizard", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("\"That old man's just a crazy wizard.\" Obi-Wan lived in Tatooine's deserts for years... ready for the right moment to act.");
        setGameText("Old Ben is a Used Interrupt and may target this site like a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter darkJediLightsaber = Filters.or(
                Filters.and(Filters.lightsaber, Filters.attachedTo(Filters.Dark_Jedi)),
                Filters.and(Filters.Dark_Jedi, Filters.hasPermanentWeapon(Filters.lightsaber)));
        Filter battleHere = Filters.and(Filters.site, Filters.sameLocation(self), Filters.battleLocation);
        return List.of(new MayFireOnlyOncePerBattleModifier(self, Filters.and(darkJediLightsaber, Filters.at(battleHere))),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }
}
