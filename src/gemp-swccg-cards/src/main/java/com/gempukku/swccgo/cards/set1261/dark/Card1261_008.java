package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.1 Carnor Jax, Royal Guard. */
public class Card1261_008 extends AbstractImperial {
    public Card1261_008() {
        super(Side.DARK, 2, 4, 5, 2, 5, "Carnor Jax, Royal Guard",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setArmor(3);
        setGameText("Imperials may not have their deploy costs increased (or be excluded from battle) by opponent here. When using a Force Pike, may draw two weapon destiny and choose one (may take the other into hand). Immune to attrition < 3.");
        addIcons(Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.ROYAL_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionLessThanModifier(self, 3));
    }
}
