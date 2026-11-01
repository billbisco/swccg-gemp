package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Extraction Facility
 */
public class Card1113_021 extends AbstractSite {
    public Card1113_021() {
        super(Side.DARK, "Kessel: Spice Mines - Extraction Facility", Title.Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If you occupy, may use 3 Force to place a 'spice' card out of play.");
        setLocationDarkSideGameText("During your control phase, may use 1 Force to place a 'spice' card in owner's Used Pile.");
        setGameText("Light: If you occupy, may use 3 Force to place a 'spice' card out of play. Dark: During your control phase, may use 1 Force to place a 'spice' card in owner's Used Pile.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.TATOOINE, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    private Filter spiceCard() {
        return Filters.or(Filters.titleContains("Spice"), Filters.titleContains("spice"));
    }

    private TopLevelGameTextAction placeSpice(final SwccgGame game, final String playerId, final PhysicalCard self, int gameTextSourceCardId, final int force, final boolean outOfPlay) {
        final Filter spice = Filters.and(spiceCard(), Filters.onTable);
        if (!GameConditions.canUseForce(game, playerId, force) || !Filters.canSpot(game, self, spice)) {
            return null;
        }
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText(outOfPlay ? "Place a spice card out of play" : "Place a spice card in Used Pile");
        action.appendCost(new UseForceEffect(action, playerId, force));
        action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose spice card", spice) {
            @Override
            protected void cardSelected(PhysicalCard selectedCard) {
                if (outOfPlay) {
                    action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, selectedCard));
                } else {
                    action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, selectedCard));
                }
            }
        });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.occupies(game, playerOnLightSideOfLocation, self)) {
            TopLevelGameTextAction action = placeSpice(game, playerOnLightSideOfLocation, self, gameTextSourceCardId, 3, true);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.CONTROL)) {
            TopLevelGameTextAction action = placeSpice(game, playerOnDarkSideOfLocation, self, gameTextSourceCardId, 1, false);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
