package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_048;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostCardFromTableResult;

import java.util.List;

/** Legacy Block 5: Order 66. */
public class Card1250_020 extends Card1116_048 {
    public Card1250_020() {
        setLore("Hologram.");
        setGameText("Deploy on Sidious. If with [Virtual Block 5] Expand The Empire during your control phase: relocate this card to an [Episode I] Jedi at a battleground or Senate (otherwise, retrieve 1 Force). If on a Jedi, add one battle destiny here, and if that Jedi just lost, relocate this card to Sidious.");
        addIcons(Icon.CORUSCANT, Icon.SIDIOUS, Icon.LEGACY_BLOCK_5);
        addKeyword(Keyword.HOLOGRAM);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    private Filter relocationTargets(PhysicalCard self) {
        return Filters.and(Icon.EPISODE_I, Filters.Jedi, Filters.at(Filters.or(Filters.battleground, Filters.Galactic_Senate)),
                Filters.not(Filters.sameCardId(self.getAttachedTo())));
    }

    private boolean canRelocate(SwccgGame game, PhysicalCard self) {
        return self.getAttachedTo() != null
                && GameConditions.canSpot(game, self, Filters.and(Filters.here(self), Filters.Expand_The_Empire, Icon.LEGACY_BLOCK_5))
                && GameConditions.canSpot(game, self, relocationTargets(self));
    }

    private void resolveControl(Action action, String owner, PhysicalCard self, SwccgGame game) {
        if (!canRelocate(game, self)) {
            action.appendEffect(new RetrieveForceEffect(action, owner, 1));
            return;
        }
        action.appendEffect(new ChooseCardOnTableEffect(action, owner, "Choose an Episode I Jedi at a battleground or Senate",
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
        if (!GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, Phase.CONTROL)) return null;
        var action = new TopLevelGameTextAction(self, sourceId);
        action.setText(canRelocate(game, self) ? "Relocate Order 66 to an Episode I Jedi" : "Retrieve 1 Force with Order 66");
        action.appendUsage(new OncePerPhaseEffect(action));
        resolveControl(action, playerId, self, game);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        String owner = self.getOwner();
        if (!TriggerConditions.isEndOfYourPhase(game, self, result, Phase.CONTROL)
                || !GameConditions.isOnceDuringYourPhase(game, self, owner, sourceId, Phase.CONTROL)) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText(canRelocate(game, self) ? "Relocate Order 66 to an Episode I Jedi" : "Retrieve 1 Force with Order 66");
        resolveControl(action, owner, self, game);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!(result instanceof LostCardFromTableResult loss) || loss.getCard() != self) return null;
        var bearer = loss.getFromAttachedTo();
        if (bearer == null || !Filters.Jedi.accepts(game, bearer)
                || GameUtils.getZoneFromZoneTop(bearer.getZone()) != Zone.LOST_PILE) return null;
        var sidious = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Sidious));
        if (sidious == null) return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Relocate Order 66 to Sidious");
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
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
