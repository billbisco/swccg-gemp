package com.gempukku.swccgo.cards.set1114.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Character
 * Subtype: Rebel
 * Title: Admiral Ackbar
 */
public class Card1114_001 extends AbstractRebel {
    public Card1114_001() {
        super(Side.LIGHT, 1, 4, 3, 3, 7, Title.Ackbar, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mon Calamari leader of Rebel fleet. Master military strategist. Early prisoner of Grand Moff Tarkin. Convinced his people to join the Alliance.");
        setGameText("Adds 3 to power of anything he pilots. It's a Trap! is destiny +2. Adds one battle destiny while piloting a Mon Calamari Star Cruiser. Once per game, may deploy a capital starship to a system (even as a 'react') from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setSpecies(Species.MON_CALAMARI);
        addPersona(Persona.ACKBAR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DestinyModifier(self, Filters.Its_A_Trap, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, Filters.Star_Cruiser), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ACKBAR_V__DEPLOY_CAPITAL;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy capital starship from Reserve Deck");
            action.setActionMsg("Deploy a capital starship to a system (even as a 'react') from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.capital_starship, Filters.system, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
