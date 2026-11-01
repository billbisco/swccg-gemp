package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Device
 * Title: Lars' Vaporator
 */
public class Card1115_017 extends AbstractLocationDevice {
    public Card1115_017() {
        super(Side.LIGHT, 4, "Lars' Vaporator", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLore("Essential for life on desert planets. Condenses water vapor from atmosphere. Has purification filters and coolant tanks. Protects against drought and harsh conditions.");
        setGameText("Deploy on a farm. Characters at same site are immune to Sandwhirl. Whenever you are about to activate Force from Beru Stew or Blue Milk, you may first take into hand from Reserve Deck Biggs, Camie, Crell, Fixer, Moisture Farmer, a Lars, or a [Special Edition] Tatooine site.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.farm;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.farm;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.character, Filters.atSameSite(self)), Title.Sandwhirl));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LARS_VAPORATOR__TAKE_CARD;
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.or(Filters.Beru_Stew, Filters.Blue_Milk))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take Biggs, Camie, Crell, Fixer, Moisture Farmer, a Lars, or a [Special Edition] Tatooine site into hand from Reserve Deck");
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, vaporatorTakeFilter(), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static Filter vaporatorTakeFilter() {
        return Filters.or(Filters.Biggs, Filters.title("Camie"), Filters.Ardon_Crell, Filters.Fixer,
                Filters.title("Moisture Farmer"), Filters.Owen, Filters.Beru,
                Filters.and(Filters.Tatooine_site, Icon.SPECIAL_EDITION));
    }
}
