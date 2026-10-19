package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateCardToSideOfTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Imperial Navigation Charts
 */
public class Card1281_032 extends AbstractNormalEffect {
    public Card1281_032() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Imperial Navigation Charts", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setGameText("Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, 'steal' (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.partOfSystem(Title.Nar_Shaddaa), Filters.battleground_site);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.location;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return stealIfControlsThree(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return stealIfControlsThree(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return stealIfWonBattleOrDrainedHere(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return stealIfWonBattleOrDrainedHere(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String owner = self.getOwner();
        String opponent = game.getOpponent(owner);

        if (TriggerConditions.isTableChanged(game, effectResult)
                && (self.getZone() == Zone.SIDE_OF_TABLE || self.isStolen())
                && GameConditions.controls(game, opponent, 3, Filters.partOfSystem(Title.Nar_Shaddaa))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<TopLevelGameTextAction> stealIfControlsThree(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_NAVIGATION_CHARTS__STEAL;
        if (!alreadyOnYourSideOfTable(self, playerId)
                && GameConditions.controls(game, playerId, 3, Filters.partOfSystem(Title.Nar_Shaddaa))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Steal' Imperial Navigation Charts");
            action.appendEffect(stealToSideOfTable(action, self, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<OptionalGameTextTriggerAction> stealIfWonBattleOrDrainedHere(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_NAVIGATION_CHARTS__STEAL;
        Filter here = Filters.here(self);
        if (!alreadyOnYourSideOfTable(self, playerId)
                && (TriggerConditions.wonBattleAt(game, effectResult, playerId, here)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, here))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Steal' Imperial Navigation Charts");
            action.appendEffect(stealToSideOfTable(action, self, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    private boolean alreadyOnYourSideOfTable(PhysicalCard self, String playerId) {
        return playerId.equals(self.getOwner()) && self.getZone() == Zone.SIDE_OF_TABLE;
    }

    private PassthruEffect stealToSideOfTable(final com.gempukku.swccgo.logic.timing.Action action, final PhysicalCard self, final String playerId) {
        return new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                self.setOwner(playerId);
                action.appendEffect(new RelocateCardToSideOfTableEffect(action, self, playerId));
            }
        };
    }
}
