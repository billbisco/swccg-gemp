package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.List;

/** BoShek, Brash Smuggler, Virtual Block 6.1. */
public class Card1261_065 extends AbstractAlien {
    public Card1261_065() {
        super(Side.LIGHT, 1, 3, 2, 4, 4, "BoShek, Brash Smuggler", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Rogue pilot. Outlaw starship tech. Has secret lab in Mos Eisley. He bragged about beating Han Solo's Kessel Run record. Left fringe life behind after meeting Obi-Wan Kenobi.");
        setGameText("Adds 3 to power of anything he pilots. While piloting a freighter at a battleground system, adds 1 to attrition against opponent here and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.SMUGGLER);
        addPersona(Persona.BOSHEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition pilotingFreighterAtBattlegroundSystem = new AndCondition(new PilotingCondition(self, Filters.freighter),
                new AtCondition(self, Filters.and(Filters.system, Filters.battleground)));
        return Arrays.asList(new AddsPowerToPilotedBySelfModifier(self, 3),
                new AttritionModifier(self, Filters.sameLocation(self), pilotingFreighterAtBattlegroundSystem, 1, opponent),
                new MayNotCloakModifier(self, Filters.and(Filters.opponents(self), Filters.starship, Filters.here(self)),
                        pilotingFreighterAtBattlegroundSystem),
                new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.system, Filters.sameLocation(self)), 3, opponent));
    }
}
