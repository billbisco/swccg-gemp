package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveTotalAbilityReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Obi-Wan In Radiant VII
 * Current text: the PC website card list (2012-07-21 to 2013-01-11, PC-E1), which drops the quote marks around 'cloak'
 * and prints the title "Obi-Wan in Radiant VII" (small i). The release-sheet original is 2250_83 (batch C1,
 * swccg-guild/work#236).
 * Every publication is a version (Bill, 2026-11-11): the Virtual Cards Master List (2013-04-27, PC-E2) repeats the
 * release-sheet wording word for word (2250_83) and is the current text; the PC website card-list text
 * (PC-E1) is 2250_1083. Same play.
 */
public class Card601_249 extends AbstractStarfighter {
    public Card601_249() {
        super(Side.LIGHT, 2, 5, 4, 4, null, 4, 6, "Obi-Wan In Radiant VII", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Optimized for diplomatic missions with sensor-proof pods that have ejection capabilities. Easily identified by its red coloration.");
        setGameText("May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not 'cloak'. Your total ability here may not be reduced.");
        addPersona(Persona.RADIANT_VII);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.LEGACY_BLOCK_5);
        addModelType(ModelType.CORELLIAN_REPUBLIC_CRUISER);
        setPilotCapacity(1);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.OBIWAN, 6) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotCloakModifier(self, Filters.and(Filters.opponents(self), Filters.starship)));
        modifiers.add(new MayNotHaveTotalAbilityReducedModifier(self, Filters.here(self), playerId));
        return modifiers;
    }
}
