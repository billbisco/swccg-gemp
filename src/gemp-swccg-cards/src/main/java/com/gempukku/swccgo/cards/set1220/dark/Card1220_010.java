package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Dengar
 */
public class Card1220_010 extends AbstractAlien {
    public Card1220_010() {
        super(Side.DARK, 1, 3, 2, 2, 6, "Dengar", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian bounty hunter. Assassin trained by the Empire. Has reflexenhancing cyber implants. Gravely injured during a swoop race in the crystal swamp of Agrilat. Blames Han Solo.");
        setGameText("Adds 2 to power of anything he pilots. Deploys free aboard Punishing One, and while piloting it, draws one battle destiny if unable to otherwise. At same site, Han's game text is canceled. Immune to Fallen Portal.");
        addPersona(Persona.DENGAR);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DAGOBAH);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setMatchingStarshipFilter(Filters.Punishing_One);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.Punishing_One));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Punishing_One), 1));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Han, Filters.atSameSite(self))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Fallen_Portal));
        return modifiers;
    }
}
