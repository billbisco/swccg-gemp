package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_050;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/** Legacy Block 5: With Thunderous Applause. */
public class Card1250_022 extends Card1116_050 {
    public Card1250_022() {
        setLore("With enough political support, any number of devious plans can be put into motion.");
        setGameText("Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious.");
        addIcons(Icon.CORUSCANT, Icon.SIDIOUS, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                  EffectResult result, PhysicalCard self,
                                                                                  int sourceId) {
        var bearer = self.getAttachedTo();
        if (!TriggerConditions.isStartOfYourTurn(game, result, self)
                || bearer == null || !Filters.at(Filters.Galactic_Senate).accepts(game, bearer)
                || game.getGameState().getReserveDeckSize(playerId) < 2) return null;

        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Draw 2 cards from Reserve Deck");
        action.appendEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
        return List.of(action);
    }

    private Filter relocationTargets(PhysicalCard self) {
        return Filters.and(Filters.or(Filters.Rebel, Filters.Jedi), Filters.at(Filters.battleground),
                Filters.not(Filters.sameCardId(self.getAttachedTo())));
    }

    private boolean canRelocate(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        return self.getAttachedTo() != null
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, Phase.MOVE)
                && GameConditions.canSpot(game, self, relocationTargets(self));
    }

    private void appendRelocation(Action action, String playerId, PhysicalCard self) {
        action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose a Rebel or Jedi at a battleground",
                relocationTargets(self)) {
            @Override
            protected void cardSelected(PhysicalCard target) {
                action.appendEffect(new AttachCardFromTableEffect(action, self, target));
            }
        });
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        if (!canRelocate(playerId, game, self, sourceId)) return null;
        var action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Relocate to a Rebel or Jedi at a battleground");
        action.appendUsage(new OncePerPhaseEffect(action));
        appendRelocation(action, playerId, self);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        String owner = self.getOwner();
        if (!TriggerConditions.isEndOfYourPhase(game, self, result, Phase.MOVE)
                || !canRelocate(owner, game, self, sourceId)) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Relocate to a Rebel or Jedi at a battleground");
        appendRelocation(action, owner, self);
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new CancelImmunityToAttritionModifier(self,
                Filters.and(Filters.hasAttached(self), Filters.not(Filters.Sidious))));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justLost(game, result, self)) return null;
        var sidious = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Sidious));
        if (sidious == null) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Relocate to Sidious");
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                // This is a relocation after loss, so do not perform a new deployment.
                if (GameUtils.getZoneFromZoneTop(self.getZone()) == Zone.LOST_PILE
                        && Filters.onTable.accepts(game, sidious)) {
                    var state = game.getGameState();
                    state.sendMessage(GameUtils.getCardLink(self) + " relocates to " + GameUtils.getCardLink(sidious));
                    state.removeCardFromZone(self);
                    state.attachCard(self, sidious);
                }
            }
        });
        return List.of(action);
    }
}
