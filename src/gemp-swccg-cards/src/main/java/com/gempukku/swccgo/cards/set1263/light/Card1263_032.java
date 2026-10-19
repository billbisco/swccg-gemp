package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithTopCardOfLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Location
 * Subtype: Site
 * Title: Spaceport Scoundrels Guild
 */
public class Card1263_032 extends AbstractSite {
    public Card1263_032() {
        super(Side.LIGHT, "Spaceport Scoundrels Guild", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLocationLightSideGameText("Once per game, if top card of your Lost Pile is a Corellian (except Corran or Han), may exchange it with a card in hand.");
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, or Yavin 4. Characters (except Lando, bounty hunters, Corellians and smugglers) are each power and forfeit -1 here.");
        setGameText("Light: Once per game, if top card of your Lost Pile is a Corellian (except Corran or Han), may exchange it with a card in hand. Dark: May not deploy to Bespin, Dagobah, Endor, Hoth, or Yavin 4. Characters (except Lando, bounty hunters, Corellians and smugglers) are each power and forfeit -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SPECIAL_EDITION);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Yavin_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter excepted = Filters.or(Filters.Lando, Filters.bounty_hunter, Filters.Corellian, Filters.smuggler);
        Filter affected = Filters.and(Filters.character, Filters.here(self), Filters.not(excepted));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, affected, -1));
        modifiers.add(new ForfeitModifier(self, affected, -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SPACEPORT_SCOUNDRELS_GUILD__EXCHANGE_CORELLIAN;
        Filter corellianExceptCorranOrHan = Filters.and(Filters.Corellian, Filters.except(Filters.Corran_Horn), Filters.except(Filters.Han));
        PhysicalCard topOfLostPile = game.getGameState().getTopOfCardPile(playerOnLightSideOfLocation, Zone.LOST_PILE);

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerOnLightSideOfLocation)
                && topOfLostPile != null
                && corellianExceptCorranOrHan.accepts(game, topOfLostPile)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange top of Lost Pile with a card in hand");
            action.setActionMsg("Exchange a card in hand with the top card of Lost Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new ExchangeCardInHandWithTopCardOfLostPileEffect(action, playerOnLightSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }
}
