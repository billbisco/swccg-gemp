package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Subtype: Immediate
 * Title: Imperial Propaganda (V)
 */
public class Card1106_039 extends AbstractImmediateEffect {
    public Card1106_039() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Propaganda, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial data transmissions depict Rebel incursions as terrorist acts. The Alliance is portrayed as a danger to civilians of the Empire.");
        setGameText("If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card), that loss is cumulatively -1 (to a minimum of 1).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(game.getGameState().getTopForceLossState())));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && self.getWhileInPlayData() != null) {

            if (GameConditions.canReduceForceLoss(game)
                    && self.getWhileInPlayData().getForceLossState() != null) {

                ForceLossState forceLossState = self.getWhileInPlayData().getForceLossState();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Reduce Force loss by 2");

                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendCost(
                        new SetWhileInPlayDataEffect(action, self, null));
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, 2, 0, false, forceLossState));
                actions.add(action);
            }
        }

        return actions;
    }
}
