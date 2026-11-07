package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.3: Grotto Werribee (V). */
public class Card1263_004 extends AbstractAlien {
    public Card1263_004() {
        super(Side.DARK, 1, 3, 2, 4, 4, "Grotto Werribee", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Once a junior traffic controller for an interstellar shipping corporation, Grotto brags that there is nothing about docking bay procedure that he doesn't know. Information broker.");
        setGameText("Adds 2 to power of anything he pilots. If opponent's [Virtual Block 6] card just added a destiny to total power or attrition here, draw two destiny, choose one, and subtract it from their attrition and total power. Your characters here are immune to Clash of Sabers and attrition <3.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.INFORMATION_BROKER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var charactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new ImmuneToTitleModifier(self, charactersHere, Title.Clash_Of_Sabers),
                new ImmuneToAttritionLessThanModifier(self, charactersHere, 3));
    }
}
