package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DuelEndingResult;

import java.util.List;

/** Legacy Block 6.2: Power Of The Sith. */
public class Card1262_007 extends AbstractImmediateEffect {
    public Card1262_007() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Power Of The Sith", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("Many have experienced the awesome power of the Dark Lord in battle. Few have lived to tell about it.");
        setGameText("If your character just won an epic duel, deploy on that character. Character may not be Disarmed, and is immune to Clash Of Sabers and Sorry About The Mess. During your control phase, if at a battleground, opponent loses 1 Force. If this card just lost, place it in Used Pile. (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Control);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
                                                         PlayCardOptionId option, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result,
                                                                   PhysicalCard self, int sourceId) {
        var duel = game.getGameState().getDuelState();
        if (duel == null || !duel.isEpicDuel()
                || !TriggerConditions.wonDuel(game, result, Filters.and(Filters.your(self), Filters.character))) {
            return null;
        }
        var winner = ((DuelEndingResult) result).getWinningCharacter();
        var action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null,
                null, false, 0, Filters.sameCardId(winner), null);
        return action == null ? null : List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter bearer = Filters.hasAttached(self);
        return List.of(new MayNotBeDisarmedModifier(self, bearer),
                new ImmuneToTitleModifier(self, bearer, Title.Clash_Of_Sabers),
                new ImmuneToTitleModifier(self, bearer, Title.Sorry_About_The_Mess));
    }

    private boolean canResolveControlLoss(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        var bearer = self.getAttachedTo();
        return bearer != null && Filters.at(Filters.battleground).accepts(game, bearer)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, Phase.CONTROL);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        if (!canResolveControlLoss(playerId, game, self, sourceId)) return null;
        var action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Make opponent lose 1 Force");
        action.appendUsage(new OncePerPhaseEffect(action));
        action.appendEffect(new LoseForceEffect(action, game.getOpponent(playerId), 1));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        String playerId = self.getOwner();
        if (!TriggerConditions.isEndOfYourPhase(game, self, result, Phase.CONTROL)
                || !canResolveControlLoss(playerId, game, self, sourceId)) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Make opponent lose 1 Force");
        action.appendEffect(new LoseForceEffect(action, game.getOpponent(playerId), 1));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justLost(game, result, self)
                && !TriggerConditions.justForfeitedToLostPileFromLocation(game, result, self, Filters.any)) return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Place in Used Pile");
        action.appendEffect(new PlaceCardsInUsedPileFromOffTableEffect(action, List.of(self)));
        return List.of(action);
    }
}
