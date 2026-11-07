package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToTargetModifier;

import java.util.List;

/** Legacy Block 6.3: Bane, The Bounty Hunter. */
public class Card1263_001 extends AbstractAlien {
    private static final Filter HOLD_OUT_BLASTER = Filters.title("Ponda Baba's Hold-out Blaster");

    public Card1263_001() {
        super(Side.DARK, 1, 4, 4, 2, 4, "Bane, The Bounty Hunter", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Bounty hunter.");
        setGameText("Adds 2 to power of anything he pilots. Ponda Baba's Hold-out Blaster deploys and fires for free on Bane. Once per turn, if Bane just used a weapon to hit a character, may 'annihilate' target (target is placed out of play when forfeited this battle).");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.BOUNTY_HUNTER);
        addPersona(Persona.CAD);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new ResetDeployCostToTargetModifier(self, Filters.and(Filters.your(self), HOLD_OUT_BLASTER), 0, self),
                new FireWeaponFiredByForFreeModifier(self, HOLD_OUT_BLASTER));
    }
}
