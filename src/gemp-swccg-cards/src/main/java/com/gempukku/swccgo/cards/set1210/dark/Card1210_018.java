package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Reegesk
 */
public class Card1210_018 extends AbstractAlien {
    public Card1210_018() {
        super(Side.DARK, 3, 1, 1, 1, 3, Title.Reegesk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Ranat thief and scavenger from Aralia. Regularly trades with Jawas. Adept at pilfering items without alerting the owner. Willing to steal anything, even trash.");
        setGameText("During opponent's turn, if present at a battleground site, may steal a device, droid, starfighter, vehicle, or weapon in opponent's Lost Pile into your hand.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.THIEF, Keyword.SCAVENGER);
        setSpecies(Species.RANAT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REEGESK_V__STEAL_FROM_LOST_PILE;
        Filter stealFilter = Filters.or(Filters.device, Filters.droid, Filters.starfighter, Filters.vehicle, Filters.weapon);

        if (GameConditions.isOpponentsTurn(game, playerId)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal card from Lost Pile");
            action.setActionMsg("Steal a device, droid, starfighter, vehicle, or weapon from opponent's Lost Pile into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, stealFilter));
            return Collections.singletonList(action);
        }
        return null;
    }
}
