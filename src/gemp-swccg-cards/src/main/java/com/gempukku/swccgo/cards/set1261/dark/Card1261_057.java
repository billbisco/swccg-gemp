package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.1 DS-61-11 In Black 11. */
public class Card1261_057 extends AbstractStarfighter {
    public Card1261_057() {
        super(Side.DARK, 2, 3, 4, null, 3, null, 4, "DS-61-11 In Black 11",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Stationed aboard command ship Executor. Pilot known as 'Wampa' for icy precision in battle. Often serves as escort to Lord Vader's shuttle.");
        setGameText("Permanent pilot is •DS-61-11, who provides ability of 2. Whenever you deploy a TIE simultaneously with a pilot character, your Force retrieval from Sienar Fleet Systems is +1. Immune to attrition < 5.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return List.of(new AbstractPermanentAboard("DS-61-11", Uniqueness.UNIQUE, true, false, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionLessThanModifier(self, 5));
    }
}
