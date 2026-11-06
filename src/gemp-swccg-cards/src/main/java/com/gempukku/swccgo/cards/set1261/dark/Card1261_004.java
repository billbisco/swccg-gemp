package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.List;

/** Legacy Virtual Block 6.1: Boba Fett, Bounty Hunter. */
public class Card1261_004 extends AbstractAlien {
    public Card1261_004() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Boba Fett, Bounty Hunter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setArmor(5);
        setLore("The most feared bounty hunter in the galaxy. Justifiably, his fee is as large as his reputation.");
        setGameText("Adds 3 to power of anything he pilots. May 'fly' (landspeed = 3). Adds one battle destiny if with your alien or Imperial. Immune to attrition < 3 (< 5 if with Vader or a bounty hunter).");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.REFLECTIONS_III);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(
                new AddsPowerToPilotedBySelfModifier(self, 3),
                new DefinedByGameTextLandspeedModifier(self, 3),
                new AddsBattleDestinyModifier(self,
                        new WithCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.alien, Filters.Imperial))), 1),
                new ImmuneToAttritionLessThanModifier(self,
                        new ConditionEvaluator(3, 5, new WithCondition(self, Filters.or(Filters.Vader, Filters.bounty_hunter)))));
    }
}
