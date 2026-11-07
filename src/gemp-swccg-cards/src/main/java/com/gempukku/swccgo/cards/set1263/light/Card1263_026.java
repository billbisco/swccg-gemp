package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6: Phylo Gandish (V). */
public class Card1263_026 extends AbstractAlien {
    public Card1263_026() {
        super(Side.LIGHT, 2, 3, 1, 4, 4, "Phylo Gandish", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Good-natured former pilot whose family owns a galactic transportation company. Years of travel has enabled her to learn many loopholes in spaceport protocol.");
        setGameText("Adds 2 to power of anything she pilots. If opponent's [Virtual Block 6] card just added a destiny to total power or attrition here, draw two destiny, choose one, and subtract it from their attrition and total power. Your characters here are immune to You Are Beaten and attrition < 3.");
        addIcons(Icon.PILOT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var charactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));
        return List.of(
                new AddsPowerToPilotedBySelfModifier(self, 2),
                new ImmuneToTitleModifier(self, charactersHere, Title.You_Are_Beaten),
                new ImmuneToAttritionLessThanModifier(self, charactersHere, 3));
    }
}
