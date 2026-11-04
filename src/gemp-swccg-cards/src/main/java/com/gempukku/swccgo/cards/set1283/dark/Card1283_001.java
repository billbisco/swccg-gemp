package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOnBottomOfUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Imperial
 * Title: AT-AT Commander
 */
public class Card1283_001 extends AbstractImperial {
    public Card1283_001() {
        super(Side.DARK, 2, 2, 2, 2, 4, "AT-AT Commander", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Leader.");
        setGameText("Adds 2 to the power of anything he pilots. May place AT-AT Commander on bottom of Used Pile to cancel an attempt to target a vehicle he is aboard with Under Attack. Once during each of your turns, may peek at the top card of your Force Pile. Projection Of A Skywalker here is suspended.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.title(Title.Projection_Of_A_Skywalker),
                Filters.or(Filters.here(self), Filters.attachedTo(Filters.sameLocation(self))))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        Filter vehicleHeIsAboard = Filters.and(Filters.vehicle, Filters.hasAboard(self));

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Under_Attack, vehicleHeIsAboard)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Choose target(s) and perform result(s)
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setText("Cancel Under Attack");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " on bottom of Used Pile to cancel Under Attack");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOnBottomOfUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AT_AT_COMMANDER__PEEK_AT_TOP_OF_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfForcePileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
