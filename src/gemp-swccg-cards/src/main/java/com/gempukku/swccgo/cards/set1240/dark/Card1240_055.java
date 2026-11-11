package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacySpiceCards;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceStackedCardOutOfPlayEffect;

import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Extraction Facility
 */
public class Card1240_055 extends AbstractSite {
    public Card1240_055() {
        super(Side.DARK, "Kessel: Spice Mines - Extraction Facility", Title.Kessel,
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationDarkSideGameText("During your control phase, may use 1 Force to place a 'spice' card in owner's Used Pile.");
        setLocationLightSideGameText("If you occupy, may use 3 Force to place a 'spice' card out of play.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        // PC-E1 (PC forum p637956, 2011-07-25): the slip covers the Episode I icon; "This location is NOT an \"Episode I\" location."
        addIcons(Icon.TATOOINE, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerId, SwccgGame game,
                                                                            PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                || !GameConditions.canUseForce(game, playerId, 1)
                || LegacySpiceCards.spiceCards(game, self, Filters.any).isEmpty()) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Place a 'spice' card in owner's Used Pile");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new ChooseStackedCardEffect(action, playerId, LegacySpiceCards.SPICE_MINE_OPERATIONS_FILTER, Filters.any) {
            @Override
            protected void cardSelected(PhysicalCard spice) {
                action.appendEffect(new PutStackedCardInUsedPileEffect(action, playerId, spice, false));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game,
                                                                             PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.occupies(game, playerId, self)
                || !GameConditions.canUseForce(game, playerId, 3)
                || LegacySpiceCards.spiceCards(game, self, Filters.any).isEmpty()) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Place a 'spice' card out of play");
        action.appendCost(new UseForceEffect(action, playerId, 3));
        action.appendEffect(new PlaceStackedCardOutOfPlayEffect(action, playerId, LegacySpiceCards.SPICE_MINE_OPERATIONS_FILTER, Filters.any));
        return List.of(action);
    }
}
