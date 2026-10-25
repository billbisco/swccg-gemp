package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Imperial Domination
 */
public class Card1220_039 extends AbstractNormalEffect {
    public Card1220_039() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Domination, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("When Vader's forces impose the New Order upon a region, Rebel resources and lifelines are quickly eliminated.");
        setGameText("Deploy on table. If your non-Imperial character (or [Independent Starship] starship) on table, this Effect lost. Unless you occupy fewer locations than opponent: if you initiate or win a battle, retrieve an Imperial; and, during battle, may take an Imperial just drawn for destiny into hand to cancel and redraw that destiny. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    private boolean occupiesFewerLocationsThanOpponent(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        int yours = Filters.countTopLocationsOnTable(game, Filters.occupies(playerId));
        int opps = Filters.countTopLocationsOnTable(game, Filters.occupies(opponent));
        return yours < opps;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self),
                Filters.or(Filters.and(Filters.character, Filters.not(Filters.Imperial)),
                        Filters.and(Filters.starship, Icon.INDEPENDENT))))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        if (!occupiesFewerLocationsThanOpponent(game, self)) {
            GameTextActionId retrieveActionId = GameTextActionId.LEGACY__IMPERIAL_DOMINATION_V__RETRIEVE_IMPERIAL;
            if (TriggerConditions.battleInitiated(game, effectResult, playerId) || TriggerConditions.wonBattle(game, effectResult, playerId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, retrieveActionId);
                action.setText("Retrieve an Imperial");
                action.appendEffect(
                        new RetrieveCardEffect(action, playerId, Filters.Imperial));
                actions.add(action);
            }

            if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                    && GameConditions.isDuringBattle(game)
                    && GameConditions.isDestinyCardMatchTo(game, Filters.Imperial)
                    && GameConditions.canTakeDestinyCardIntoHand(game, playerId)
                    && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Take Imperial into hand and redraw");
                action.appendEffect(
                        new TakeDestinyCardIntoHandEffect(action));
                action.appendEffect(
                        new CancelDestinyAndCauseRedrawEffect(action));
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }
}
