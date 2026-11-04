package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Republic
 * Title: Supreme Chancellor Valorum (V)
 */
public class Card1281_030 extends AbstractRepublic {
    public Card1281_030() {
        super(Side.LIGHT, 3, 3, 2, 4, 4, Title.Valorum, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setPolitics(3);
        setLore("Although Finis Valorum maintains the Galactic Senate's ultimate title, his real power is mired by endless bureaucracy, petty corruption, and incessant plotting.");
        setGameText("Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to take into hand from Reserve Deck a card with 'governor,' 'political,' or 'Valorum' in lore; reshuffle.");
        addIcons(Icon.EPISODE_I);
    }

    private static Filter yourCharactersWithPolitics(String playerId) {
        return Filters.and(Filters.your(playerId), Filters.character, Filters.politicsMoreThan(0),
                Filters.not(Filters.or(Filters.Wedge, Filters.senator)));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.ORDER));
        modifiers.add(new MayBeTargetedByModifier(self, "Might Of The Republic"));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter politicians = yourCharactersWithPolitics(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, politicians, -1, Filters.here(self)));
        modifiers.add(new IconModifier(self, Filters.and(politicians, Filters.here(self)), Icon.REPUBLIC));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SUPREME_CHANCELLOR_VALORUM_V__TAKE_CARD;

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with 'governor,' 'political,' or 'Valorum' in lore into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.or(Filters.loreContains("governor"), Filters.loreContains("political"), Filters.loreContains("Valorum")), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
