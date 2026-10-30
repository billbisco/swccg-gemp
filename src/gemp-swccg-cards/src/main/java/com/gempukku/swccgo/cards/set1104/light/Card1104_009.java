package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Effect
 * Subtype: Immediate
 * Title: Heroic Sacrifice
 */
public class Card1104_009 extends AbstractImmediateEffect {
    public Card1104_009() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Heroic_Sacrifice, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Alliance lost many fine soldiers at conflicts such as the Battle of Hoth. The Rebel High Command is aware of the cost of freedom from Imperial tyranny.");
        setGameText("Deploy on opponent's just deployed [Maintenance] card; opponent loses 1 Force and you may retrieve 1 Force. That card may not apply its ability toward drawing battle destiny, is power -3, and gains (in addition to its own maintenance cost): \"★Lose 1 Force, or Place out of play.\" (Immune to Control.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.and(Filters.opponents(self), Icon.MAINTENANCE, Filters.canBeTargetedBy(self)))) {
            PhysicalCard justDeployedCard = ((PlayCardResult) effectResult).getPlayedCard();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(justDeployedCard), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.hasAttached(self)));
        modifiers.add(new PowerModifier(self, Filters.hasAttached(self), -3));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.setActionMsg("Make opponent lose 1 Force");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        PhysicalCard host = self.getAttachedTo();
        if (host != null
                && TriggerConditions.isEndOfYourTurn(game, effectResult, host.getOwner())) {
            final PhysicalCard hostCard = host;
            final String hostOwner = host.getOwner();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(hostOwner);
            action.setText("Pay extra maintenance");
            action.setActionMsg("Lose 1 Force or place " + GameUtils.getCardLink(hostCard) + " out of play");
            action.appendEffect(
                    new PlayoutDecisionEffect(action, hostOwner,
                            new MultipleChoiceAwaitingDecision("Choose extra maintenance option for " + GameUtils.getCardLink(hostCard),
                                    new String[]{"Lose 1 Force", "Place out of play"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(
                                                new LoseForceEffect(action, hostOwner, 1));
                                    } else {
                                        action.appendEffect(
                                                new PlaceCardOutOfPlayFromTableEffect(action, hostCard));
                                    }
                                }
                            }));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasLostPile(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
