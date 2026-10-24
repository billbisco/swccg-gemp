package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Objective
 * Title: Imperial Entanglements / No One To Stop Us This Time
 */
public class Card1210_068_BACK extends AbstractObjective {
    public Card1210_068_BACK() {
        super(Side.DARK, 7, Title.No_One_To_Stop_Us_This_Time, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setGameText(Card1210_068.PACK_GAMETEXT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter trooperRidingDewback = Filters.and(Filters.your(self), Filters.trooper, Filters.aboardAsPassenger(Filters.Dewback));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new DuringBattleAtCondition(Filters.sameLocationAs(self, trooperRidingDewback)), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Local_Trouble, ModifyGameTextType.LEGACY__LOCAL_TROUBLE__MAY_PLAY_AT_EXTERIOR_TATOOINE_SITES));
        modifiers.add(new MoveCostFromLocationUsingLandspeedModifier(self, Filters.and(Filters.opponents(self), Filters.character), 1, Filters.Tatooine_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NO_ONE_TO_STOP_US_THIS_TIME_V__PEEK_RESERVE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final int valueOfX = (int) Math.min(game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X,
                    Filters.countTopLocationsOnTable(game, Filters.and(Filters.Tatooine_location, Filters.occupies(playerId)))),
                    game.getGameState().getReserveDeckSize(playerId));
            if (valueOfX > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at top cards of Reserve Deck");
                action.setActionMsg("Peek at the top " + valueOfX + " cards of Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s) — printed "the top X cards", not "up to X"
                action.appendEffect(
                        new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, valueOfX, 1, 1));
                action.appendEffect(
                        new ShuffleReserveDeckEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {
            if (Filters.countTopLocationsOnTable(game, Filters.and(Filters.Tatooine_site, Filters.controls(opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))) >
                    Filters.countTopLocationsOnTable(game, Filters.and(Filters.Tatooine_site, Filters.controls(playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE)))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
