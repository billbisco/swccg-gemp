package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_047;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithSeizureEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCaptureCardResult;

import java.util.List;

/** Legacy Block 5: Nute Gunray's Bounty. */
public class Card1250_019 extends Card1116_047 {
    public Card1250_019() {
        setLore("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being.");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    private static Filter eligibleHunters(PhysicalCard bounty, PhysicalCard target) {
        return Filters.and(Filters.your(bounty), Filters.alien, Keyword.BOUNTY_HUNTER,
                Filters.presentWith(target), Filters.canEscortCaptive(target));
    }

    private static boolean canCapture(SwccgGame game, PhysicalCard bounty, PhysicalCard target) {
        return bounty.getZone().isInPlay() && target == bounty.getAttachedTo() && !target.isCaptive()
                && !GameConditions.canSpot(game, bounty, Filters.and(Filters.Jedi, Filters.here(bounty)))
                && GameConditions.canTarget(game, bounty, TargetingReason.TO_BE_CAPTURED, Filters.sameCardId(target));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                      PhysicalCard self, int sourceId) {
        var target = self.getAttachedTo();
        if (target == null || !GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                || !canCapture(game, self, target)) return null;

        Filter hunters = eligibleHunters(self, target);
        if (!GameConditions.canSpot(game, self, hunters)) return null;

        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Capture and seize the character");
        action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose alien bounty hunter to seize the character", hunters) {
            @Override
            protected void cardSelected(PhysicalCard escort) {
                action.appendEffect(new SeizeForBountyEffect(action, target, escort));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                  PhysicalCard self, int sourceId) {
        return escortLoss(game, result, self, sourceId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        return escortLoss(game, result, self, sourceId);
    }

    private static List<RequiredGameTextTriggerAction> escortLoss(SwccgGame game, EffectResult result,
                                                                   PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isStartOfYourPhase(game, result, Phase.CONTROL, self.getOwner())) return null;
        var captive = self.getAttachedTo();
        if (captive == null || !captive.isCaptive() || captive.getEscort() == null
                || !Filters.alien.accepts(game, captive.getEscort())) return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
        return List.of(action);
    }

    /** The printed capture requires seizure by the chosen hunter, with the usual capture response window. */
    private static final class SeizeForBountyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
        private final PhysicalCard target;
        private final PhysicalCard escort;
        private boolean prevented;
        private CaptureWithSeizureEffect seizure;

        private SeizeForBountyEffect(Action action, PhysicalCard target, PhysicalCard escort) {
            super(action);
            this.target = target;
            this.escort = escort;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            PhysicalCard bounty = _action.getActionSource();
            return !prevented && canCapture(game, bounty, target)
                    && GameConditions.canSpot(game, bounty, Filters.and(eligibleHunters(bounty, target), Filters.sameCardId(escort)));
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            var subAction = new SubAction(_action);
            subAction.appendEffect(new TriggeringResultEffect(subAction, new AboutToCaptureCardResult(subAction, target, this)));
            subAction.appendEffect(new PassthruEffect(subAction) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    if (isPlayableInFull(game)) {
                        seizure = new CaptureWithSeizureEffect(subAction, target, escort);
                        subAction.appendEffect(seizure);
                    }
                }
            });
            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return seizure != null && seizure.wasCarriedOut();
        }

        @Override
        public void preventEffectOnCard(PhysicalCard card) {
            if (card == target) prevented = true;
        }

        @Override
        public boolean isEffectOnCardPrevented(PhysicalCard card) {
            return card == target && prevented;
        }
    }
}
