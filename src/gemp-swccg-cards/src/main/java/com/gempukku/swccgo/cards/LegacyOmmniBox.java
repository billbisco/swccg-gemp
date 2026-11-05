package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * The Modal Nodes' Ommni Box (Virtual Block 8.3) changes the text of your musicians and of 'cover charge' cards:
 * <ul>
 *   <li>"'At same site' on your musicians may be treated as 'at battleground sites.'" A card or site counts if it
 *   fits either reading; a count of cards "at same site" uses whichever reading gives the higher number.</li>
 *   <li>"'Cover charges' only apply to opponent and are limited to 3."</li>
 * </ul>
 * The musicians and cover-charge cards call these helpers in place of their "at same site" filters.
 */
public final class LegacyOmmniBox {

    public static final String TITLE = "The Modal Nodes' Ommni Box";

    private LegacyOmmniBox() {
    }

    /**
     * @return true if 'at same site' on this card may be treated as 'at battleground sites'
     */
    public static boolean atSameSiteMayBeAtBattlegroundSites(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.LEGACY__OMMNI_BOX__AT_SAME_SITE_MAY_BE_TREATED_AS_AT_BATTLEGROUND_SITES);
    }

    /**
     * @return true if this card's 'cover charges' only apply to opponent and are limited to 3
     */
    public static boolean coverChargesLimited(SwccgGame game, PhysicalCard self) {
        return game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__OMMNI_BOX__COVER_CHARGES_ONLY_OPPONENT_AND_LIMITED_TO_3);
    }

    /**
     * "At same site" (cards) on a musician: cards at same site as the musician, or, under Ommni Box, cards at a
     * battleground site.
     * @param self the musician
     * @return the filter
     */
    public static Filter atSameSite(final PhysicalCard self) {
        final Filter atSameSite = Filters.atSameSite(self);
        final Filter atBattlegroundSite = Filters.at(Filters.battleground_site);
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (atSameSite.accepts(gameState, modifiersQuerying, physicalCard))
                    return true;
                return atSameSiteMayBeAtBattlegroundSites(gameState, modifiersQuerying, self)
                        && atBattlegroundSite.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * "Same site" (as a location) on a musician: the musician's site, or, under Ommni Box, any battleground site.
     * @param self the musician
     * @return the filter
     */
    public static Filter sameSite(final PhysicalCard self) {
        final Filter sameSite = Filters.sameSite(self);
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (sameSite.accepts(gameState, modifiersQuerying, physicalCard))
                    return true;
                return atSameSiteMayBeAtBattlegroundSites(gameState, modifiersQuerying, self)
                        && Filters.battleground_site.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Counts active cards "at same site" as the musician. Under Ommni Box, the count may instead be of cards at
     * battleground sites; the higher count is used.
     * @param game the game
     * @param self the musician
     * @param filter the cards to count
     * @return the count
     */
    public static int countAtSameSite(SwccgGame game, PhysicalCard self, Filterable filter) {
        int atSameSite = Filters.countActive(game, self, Filters.and(filter, Filters.atSameSite(self)));
        if (!atSameSiteMayBeAtBattlegroundSites(game.getGameState(), game.getModifiersQuerying(), self))
            return atSameSite;
        int atBattlegroundSites = Filters.countActive(game, self, Filters.and(filter, Filters.at(Filters.battleground_site)));
        return Math.max(atSameSite, atBattlegroundSites);
    }

    /**
     * "For each ... at same site" as an evaluator. Under Ommni Box, the count may instead be of cards at
     * battleground sites; the higher count is used.
     * @param self the musician
     * @param filter the cards to count
     * @return the evaluator
     */
    public static Evaluator atSameSiteEvaluator(final PhysicalCard self, final Filterable filter) {
        final int permCardId = self.getPermanentCardId();
        final Evaluator printed = new AtSameSiteEvaluator(self, filter);
        return new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                float atSameSite = printed.evaluateExpression(gameState, modifiersQuerying, cardAffected);
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (!atSameSiteMayBeAtBattlegroundSites(gameState, modifiersQuerying, card))
                    return atSameSite;
                int atBattlegroundSites = Filters.countActive(gameState.getGame(), card, Filters.and(filter, Filters.at(Filters.battleground_site)));
                return Math.max(atSameSite, atBattlegroundSites);
            }
        };
    }

    /**
     * A 'cover charge' amount: limited to 3 under Ommni Box.
     * @param self the card adding the cover charge
     * @param coverCharge the printed cover charge
     * @return the evaluator
     */
    public static Evaluator coverCharge(final PhysicalCard self, final Evaluator coverCharge) {
        final int permCardId = self.getPermanentCardId();
        return new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                float amount = coverCharge.evaluateExpression(gameState, modifiersQuerying, cardAffected);
                return coverChargesLimited(gameState.getGame(), card) ? Math.min(3, amount) : amount;
            }
        };
    }

    /**
     * Characters a 'cover charge' applies to: only opponent's under Ommni Box.
     * @param self the card adding the cover charge
     * @param affected the printed affected cards
     * @return the filter
     */
    public static Filter coverChargePayers(final PhysicalCard self, Filterable affected) {
        final Filter printed = Filters.and(affected);
        final Filter opponents = Filters.opponents(self);
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!printed.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;
                return !coverChargesLimited(gameState.getGame(), self) || opponents.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }
}
