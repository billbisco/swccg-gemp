package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Veers
 */
public class Card1220_017 extends AbstractImperial {
    public Card1220_017() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Veers", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("General of the AT-AT assault armor division sent by Darth Vader to crush the Rebellion on Hoth. Cold and ruthless.");
        setGameText("Add 3 to power of anything he pilots. Leader. Once per turn, may reveal from hand to / 6th marker or Blizzard 1 to Hoth. Deploys free aboard Blizzard 1. While piloting Blizzard 1, adds one destiny to total power.");
        addPersona(Persona.VEERS);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PREMIUM);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Persona.BLIZZARD_1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Blizzard_1), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VEERS_V__DEPLOY_SIXTH_MARKER_OR_BLIZZARD_1;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy 6th Marker or Blizzard 1");
            action.setActionMsg("Deploy 6th Marker or Blizzard 1 to Hoth");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.Blizzard_1, Filters.Sixth_Marker), Title.Hoth, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
