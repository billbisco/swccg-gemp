package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: WED15-I662 'Treadwell' Droid
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[WED15-I662 'Treadwell' Droid]]. As printed its starfighter
 * immunity applies only at the related system. The errata ([[WED15-I662 'Treadwell' Droid (Errata)]], Decipher Glossary
 * Version 2.0 (November 1998)) added sectors.
 * Copy of Card1_197 with "system or sector" narrowed to "system"; everything else plays as Card1_197.
 */
public class Card1_197o extends AbstractDroid {
    public Card1_197o() {
        super(Side.DARK, 3, 2, 1, 3, "WED15-I662 'Treadwell' Droid", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Standard treadwell droid. This one, also known as 'Eyesixsixtoo', is typical of the thousands of droids which repair and maintain heavy machinery and starfighters.");
        setGameText("All your vehicles and droids at same location are immune to attrition < 2. Also, if 'treadwell' droid is at a docking bay, all your starfighters at the related system are immune to attrition < 3.");
        setTextState(new TextState("1_197", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
        addModelType(ModelType.MAINTENANCE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.vehicle,
                Filters.droid), Filters.at(Filters.sameLocation(self))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.starfighter,
                Filters.at(Filters.relatedSystem(self))), new AtCondition(self, Filters.docking_bay), 3));
        return modifiers;
    }
}
