package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Subtype: Utinni
 * Title: Send A Detachment Down
 */
public class Card1210_048 extends AbstractUtinniEffect {
    public Card1210_048() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Send_A_Detachment_Down, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader sent Imperial stormtroopers to the surface of Tatooine in search of the stolen Death Star plans. 'There'll be no one to stop us this time.'");
        setGameText("Deploy on opponent's card (a device, droid, or weapon) that may be stolen by a character. Target your trooper at another location. When reached by target, place this Utinni Effect on target; target is power +2, steals opponent's card, and you may retrieve 3 Force.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.device, Filters.droid, Filters.weapon),
                Filters.canBeStolenBy(self, Filters.character));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        if (GameConditions.isUtinniEffectReached(game, self)) {
            return Filters.and(Filters.your(self), Filters.trooper);
        }
        return Filters.and(Filters.opponents(self), Filters.or(Filters.device, Filters.droid, Filters.weapon));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.trooper, Filters.not(Filters.at(Filters.sameLocation(deployTarget))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.hasAttached(self), Filters.trooper), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        final PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);
        final PhysicalCard host = self.getAttachedTo();

        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && target != null
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

            final String playerId = self.getOwner();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Relocate to " + GameUtils.getCardLink(target));
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " on " + GameUtils.getCardLink(target) + " and steal " + GameUtils.getCardLink(host));
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                        }
                    }
            );
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, target));
            if (host != null && !host.equals(target)) {
                action.appendEffect(
                        new StealCardAndAttachFromTableEffect(action, host, target));
            }
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Retrieve 3 Force?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new RetrieveForceEffect(action, playerId, 3));
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
