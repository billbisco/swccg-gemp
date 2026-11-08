package com.gempukku.swccgo.cards.set2210.light;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Obi-Wan Kenobi
 * Original version of 1210_107 (Virtual Block 1 release sheet, Dec 2009): "may not be targeted by cards that capture" ("never" on the Virtual Cards Master List, 2013-04-27). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_107 extends AbstractRebel {
    public Card2210_107() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, "Obi-Wan Kenobi", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jedi Knight. Trained by Yoda. Friend of Bail Organa. General Kenobi became a hero of the Old Republic during the Clone Wars. Mentor of Anakin and Luke Skywalker.");
        setGameText("Obi-Wan's game text may not be canceled and he may not be targeted by cards that capture. Unless another Jedi present, may Force drain here regardless of Objective restrictions. Immune to attrition < 5 (< 8 if with a Dark Jedi).");
        setLegacyVersion(new LegacyVersion("1210_107", "",
                "Virtual Block 1 release sheet (2009-12-07), card #107", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        addPersona(Persona.OBIWAN);
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self));
        modifiers.add(new MayNotTargetToBeCapturedModifier(self, self));
        modifiers.add(new IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(self, Filters.sameLocation(self),
                new NotCondition(new PresentCondition(self, Filters.and(Filters.Jedi, Filters.other(self)))), playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 8, new PresentCondition(self, Filters.Dark_Jedi))));
        return modifiers;
    }
}
