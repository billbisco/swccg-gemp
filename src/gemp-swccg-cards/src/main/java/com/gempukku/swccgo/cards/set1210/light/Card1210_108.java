package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardFromHandWithStackedCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Owen Lars
 */
public class Card1210_108 extends AbstractRebel {
    public Card1210_108() {
        super(Side.LIGHT, 2, 2, 1, 3, 4, Title.Owen_Lars, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        addPersona(Persona.OWEN);
        setVirtualSuffix(true);
        setLore("Guardian of Luke Skywalker. Husband of Beru Lars. Brother of Obi- Wan Kenobi. Farmer of moisture. Disapprover of Luke's desire to leave Tatooine.");
        setGameText("Opponent activates no Force from Beru Stew. While present with Lars' Vaporator: during your control phase, may use 3 Force to exchange one card from hand with one 'crop' card; cards with 'lightsaber' in title may not deploy here.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        PresentWithCondition withVaporator = new PresentWithCondition(self, Filters.title("Lars' Vaporator"));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Beru_Stew, ModifyGameTextType.LEGACY__OWEN_LARS_V__OPPONENT_ACTIVATES_NO_FORCE_FROM_BERU_STEW));
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.or(Filters.lightsaber, Filters.titleContains("lightsaber")), withVaporator,
                Filters.locationAndCardsAtLocation(Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OWEN_LARS_V__EXCHANGE_CROP;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentWith(game, self, Filters.title("Lars' Vaporator"))
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.title("Lars' Hydroponics Station"), Filters.hasStacked(Filters.any)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange a card from hand with a 'crop'");
            action.setActionMsg("Use 3 Force to exchange a card from hand with a 'crop'");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            action.appendEffect(
                    new ExchangeCardFromHandWithStackedCardEffect(action, Filters.any,
                            Filters.and(Filters.your(self), Filters.title("Lars' Hydroponics Station")), Filters.any));
            return Collections.singletonList(action);
        }
        return null;
    }
}
