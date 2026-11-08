package com.gempukku.swccgo.cards.set2220.dark;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Piett
 * Original version of 1220_6 (Virtual Block 2 release sheet, Dec 2009): adds 3 to power of anything he pilots (2 on the PC card list by 2012-07-21). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_006 extends AbstractImperial {
    public Card2220_006() {
        super(Side.DARK, 2, 3, 2, 3, 4, "Captain Piett", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Captain on the Executor. Monitored probe droid telemetry. His flawless record of arrests and suppressions has contributed to an impressive rise through the ranks.");
        setGameText("Adds 3 to power of anything he pilots. Twice per game during opponent's turn, may place three cards from hand on your Force Pile to draw top two cards from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1220_6", "",
                "Virtual Block 2 release sheet (2009-12-07), card #6; superseded by the PC card list errata (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        addPersona(Persona.PIETT);
        addIcons(Icon.PILOT, Icon.HOTH);
        addKeywords(Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CAPTAIN_PIETT_V__PLACE_CARDS_DRAW_RESERVE;
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.isOpponentsTurn(game, playerId)
                && GameConditions.numCardsInHand(game, playerId) >= 3
                && GameConditions.hasReserveDeck(game, playerId)
                && game.getGameState().getReserveDeckSize(playerId) >= 2) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place 3 cards on Force Pile to draw 2");
            action.setActionMsg("Place three cards from hand on Force Pile to draw top two cards from Reserve Deck");
            action.appendUsage(
                    new TwicePerGameEffect(action));
            action.appendCost(
                    new PutCardsFromHandOnForcePileEffect(action, playerId, 3, 3));
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
